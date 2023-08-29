package com.cinch.adventurebuilderstoolkit.beans.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOB;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBInstance;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBProperty;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBPropertyDefinition;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBReferance;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.actions.BaseViewAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.HideAllGOBReferances;
import com.cinch.adventurebuilderstoolkit.editor.actions.LinkGobInstanceToView;
import com.cinch.adventurebuilderstoolkit.editor.actions.NewGobInstanceToView;
import com.cinch.adventurebuilderstoolkit.editor.actions.ShowAllGOBReferances;
import com.cinch.adventurebuilderstoolkit.editor.gob.ViewEditor;
import com.cinch.adventurebuilderstoolkit.editor.wizards.NewGobInstanceWizard;

public class ConnectorView implements ViewDrawable {
	private static final long serialVersionUID = 1L;
	static private final Logger log = LogManager.getLogger(ConnectorView.class);
	
	public static int SIZE=6;
	
	// Positions around the frame of the GobView
	private String uuid;
	private float x=0.5f;
	private float y=1f;
	
	private transient GobView gobView;
	private transient GOBPropertyDefinition gobPropertyDefinition=null;
	private transient List<GOBReferance> destinationReferances=null;
	// In this case this is a list of all the connectors to GOB instances. The list will only have one item if this is not an array.
	private List<ConnectorLineView> connectors=new ArrayList<>();

	public ConnectorView() {
		uuid=UUID.randomUUID().toString();
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public GobView getGobView() {
		return gobView;
	}

	public GOBPropertyDefinition getGobPropertyDefinition() {
		return gobPropertyDefinition;
	}
	
	public Point2D getXandY() {
		return new Point2D.Float(x,y);
	}
	
	public int getConnectorIndex(GOBReferance destinationReferance) {
		return destinationReferances.indexOf(destinationReferance);
	}

	public void swapOrder(AdventureProjectModel adventureProjectModel, GOBReferance destinationReferance, GOBReferance destinationReferance2) {
		System.out.println("Swap Order");
		GOBInstance gob=gobView.getGobReferance().getGobInstance(adventureProjectModel);
		GOBProperty<?> property=gob.getProperty(gobPropertyDefinition);
		Object value=property!=null?property.getValue():null;
		if (value!=null && value instanceof List<?> destGobInstances) {
			System.out.println("Swap Order Got Value");
			@SuppressWarnings("unchecked")
			List<GOBInstance> list=(List<GOBInstance>)destGobInstances;
			int[] pos=new int[2];
			int p=0;
			int count=0;
			for (GOBInstance gi:list) {
				if (gi!=null) {
					if (gi.getUuid().equals(destinationReferance.getUuid()) || gi.getUuid().equals(destinationReferance2.getUuid())) {
						System.out.println("Swap Order Found Item "+p);
						pos[p++]=count;
					}
				}
				if (p>1) {
					break;
				}
				count++;
			}
			if (p==2) {
				System.out.println("Swap Order Found 2 items");
				GOBInstance removed1=list.remove(pos[1]);
				GOBInstance removed2=list.remove(pos[0]);
				list.add(pos[0],removed1);
				list.add(pos[1],removed2);
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						adventureProjectModel.fireDataInstanceChange(this, gob);
					}
				});
			}
		}
	}
	
	public boolean setGobPropertyDefinitions(GobView gobView,GOBPropertyDefinition gobPropertyDefinition,List<GOBReferance> destinationReferances) {
		boolean changed=false;
		this.gobView=gobView;
		this.gobPropertyDefinition=gobPropertyDefinition;
		this.destinationReferances=destinationReferances;

		List<GOBReferance> newConnections=new ArrayList<>();
		List<ConnectorLineView> lostConnections=new ArrayList<>();
		Set<GOBReferance> currentConnections=new HashSet<>();
		
		for (var c:connectors) {
			var ref=c.getDestinationReferance();
			if (!destinationReferances.contains(ref)) {
				lostConnections.add(c);
				changed=true;
			}else {
				currentConnections.add(ref);
			}
		}
		
		connectors.removeAll(lostConnections);
		
		for (int t=0;t<destinationReferances.size();t++) {
			var ref=destinationReferances.get(t);
			if (!currentConnections.contains(ref)) {
				changed=true;
				newConnections.add(ref);
			}
		}
		
		for (var destinationReferance:newConnections) {
			ConnectorLineView c;
			c=new ConnectorLineView();
			connectors.add(c);
			c.setDestinationReferance(destinationReferance);
			c.setConnectorView(this);
		}
		return changed;
	}

	@Override
	public Rectangle bounds() {
		if (gobView==null) {
			return null;
		}
		Rectangle gobBounds=gobView.bounds();
		int x,y;
		x=(int)(gobBounds.x+gobBounds.width*this.x-SIZE/2);
		y=(int)(gobBounds.y+gobBounds.height*this.y-SIZE/2);
		if (this.x==0) {
			x-=SIZE/2+1;
		}
		if (this.x==1) {
			x+=SIZE/2+1;
		}
		if (this.y==0) {
			y-=SIZE/2+1;
		}
		if (this.y==1) {
			y+=SIZE/2+1;
		}
		return new Rectangle(x,y,SIZE,SIZE);
	}

	@Override
	public boolean isVisable(Rectangle r) {
		return false;
	}

	@Override
	public void moveTo(int x, int y) {
		Rectangle gobBounds=gobView.bounds();
		x=(int)Math.max(gobBounds.getMinX(), Math.min(gobBounds.getMaxX(), x));
		y=(int)Math.max(gobBounds.getMinY(), Math.min(gobBounds.getMaxY(), y));
		int dx=(int)Math.min(x-gobBounds.getMinX(), gobBounds.getMaxX()-x);
		int dy=(int)Math.min(y-gobBounds.getMinY(), gobBounds.getMaxY()-y);
		if (dx>dy) {
			if (y-gobBounds.getMinY()<gobBounds.getMaxY()-y) {
				y=(int)gobBounds.getMinY();
			} else {
				y=(int)gobBounds.getMaxY();
			}
		} else {
			if (x-gobBounds.getMinX()<gobBounds.getMaxX()-x) {
				x=(int)gobBounds.getMinX();
			} else {
				x=(int)gobBounds.getMaxX();
			}
		}
		
		this.x=(float)((x-gobBounds.getMinX())/gobBounds.width);
		this.y=(float)((y-gobBounds.getMinY())/gobBounds.height);
		bounds();
	}
	
	@Override
	public ViewDrawable findOver(Point p) {
		Rectangle bounds=bounds();
		if (bounds.contains(p)) {
			double dx=p.x-bounds.getCenterX();
			double dy=p.y-bounds.getCenterY();
			double d=Math.sqrt(dx*dx+dy*dy);
			if (d<=SIZE/2) {
				return this;
			}
		}
		// TODO: Search lines too if we want to do edits on those
		for (var gcl:connectors) {
			ViewDrawable found=gcl.findOver(p);
			if (found!=null) {
				return found;
			}
			
		}
		return null;
	}

	// Returns how many connectors do not have a matching gobview in the view
	public int getMissingInViewCount() {
		int count=0;
		for (var c:connectors) {
			c.setConnectorView(this);
			if (c.getDestinationGobView()==null) {
				count++;
			}
		}
		return count;
	}
	
	public boolean isReferance(AdventureProjectModel adventureProjectModel) {
		// TODO: Might want to cache this
		GOB destinationType=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobPropertyDefinition.getGobType());
		if (destinationType==null) {
			log.error("Unable to find destinationType for "+gobPropertyDefinition.getGobType());
			return false;
		}
		return destinationType.getType().isReferance();
	}
	
	public boolean isArray() {
		return gobPropertyDefinition.isArray();
	}

	@Override
	public void paintDrawable(AdventureProjectModel adventureProjectModel, Graphics2D g2, boolean isSelected, boolean isHighlighted, ViewEditor viewEditor) {
		if (gobView==null) {
			return;
		}
		
		Rectangle bounds=bounds();
		
		Color c;
		float s=(float)(2f/viewEditor.getView().getScale());
		g2.setStroke(new BasicStroke(s));
		if (gobPropertyDefinition!=null) {
			c=gobPropertyDefinition.getColor();
			if (c==null) {
				c=Color.yellow;
			}
			g2.setColor(c);
			if (isReferance(adventureProjectModel)) {
				if (isArray()) {
					g2.fillOval(bounds.x,bounds.y,bounds.width,bounds.height);
				} else {
					g2.drawOval(bounds.x,bounds.y,bounds.width,bounds.height);
				}
			} else {
				if (isArray()) {
					g2.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
				} else {
					g2.drawRect(bounds.x,bounds.y,bounds.width,bounds.height);
				}
			}
			
			int count=getMissingInViewCount();

			if (count>0) {
				if (gobPropertyDefinition.isArray()) {
					g2.setColor(ImageUtilities.getSuitableTextColorForBackground(c));
				}
				g2.drawLine((int)bounds.getMinX(),(int)bounds.getCenterY(),(int)bounds.getMaxX(),(int)bounds.getCenterY());
				g2.drawLine((int)bounds.getCenterX(),(int)bounds.getMinY(),(int)bounds.getCenterX(),(int)bounds.getMaxY());
			}
		
			if (isSelected) {
				int g=(int)Math.ceil(s*3/2);
				g2.setColor(ImageUtilities.VIEW_SELECTED_OUTLINE_COLOUR);
				if (isReferance(adventureProjectModel)) {
					g2.drawOval(bounds.x-g,bounds.y-g,bounds.width+g*2,bounds.height+g*2);
				} else {
					g2.drawRect(bounds.x-g,bounds.y-g,bounds.width+g*2,bounds.height+g*2);
				}
			}
		}		
	}

	@Override
	public void recalcSize(AdventureProjectModel adventureProjectModel) {
		bounds();
	}

	@Override
	public Collection<ConnectorLineView> getChildren(){
		return connectors;
	}
	
	@Override
	public List<BaseViewAction> getActions(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel){
		List<BaseViewAction> actions=new ArrayList<>();
		
		
		// If we have some hidden references then allow to show all
		actions.add(new ShowAllGOBReferances(viewEditor, this, adventureProjectModel));
		actions.add(new HideAllGOBReferances(viewEditor, this, adventureProjectModel));
	
		return actions;
	}
	
	public RightDragHandler getRightDragHandler() {
		return new ConnectorRightDragHandler();
	}
	
	public void hideAll(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel) {
		boolean removed=false;
		for (var c:connectors) {
			c.setConnectorView(this);
			GobView gobView=c.getDestinationGobView();
			if (gobView!=null) {
				viewEditor.getView().getGobs().remove(gobView);
				
				removed=true;			
			}
		}
		if (removed) {
			viewEditor.getView().touch();
			adventureProjectModel.fireFileGameObjectChange(this, viewEditor.getView());
		}
	}
	
	public void showAll(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel) {
		Rectangle bounds=bounds();

		int nx=(int)bounds.getCenterX();
		int ny=(int)bounds.getCenterY();
		int xd=10;
		int yd=10;

		if (x==0) {
			nx-=240;
			xd=0;
			yd=1;
		} else if (x==1) {
			nx+=80;
			xd=0;
			yd=1;
		} else if (y==0) {
			ny-=80;
			xd=0;
			yd=-1;
		} else if (y==1) {
			ny+=40;
			xd=0;
			yd=1;
		}
		
		boolean added=false;
		for (var c:connectors) {
			c.setConnectorView(this);
			if (c.getDestinationGobView()==null) {
				GobView gobView=new GobView();
				gobView.setGobReferance(c.getDestinationReferance());
				gobView.setBounds(new Rectangle(nx, ny, 200, 40));
				gobView.recalcSize(adventureProjectModel);
				gobView.setView(viewEditor.getView());
				if (gobView.getGobReferance().getGob(adventureProjectModel).getDefaultViewMode()!=null) {
					gobView.setViewMode(gobView.getGobReferance().getGob(adventureProjectModel).getDefaultViewMode());
				}
				viewEditor.getView().getGobs().add(gobView);
				
				nx+=xd;
				ny+=80*yd;
				added=true;			
			}
		}
		if (added) {
			viewEditor.getView().touch();
			adventureProjectModel.fireFileGameObjectChange(this, viewEditor.getView());
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(connectors, x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectorView other = (ConnectorView) obj;
		return Objects.equals(connectors, other.connectors) && Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
				&& Float.floatToIntBits(y) == Float.floatToIntBits(other.y);
	}



	class ConnectorRightDragHandler implements RightDragHandler {
		
		@Override
		public ViewDrawable getDrawable() {
			return ConnectorView.this;
		}

		@Override
		public boolean canDragOnto(AdventureProjectModel adventureProjectModel, ViewDrawable viewDrawable){
			boolean canLink=false;
			if (viewDrawable instanceof GobView gobView) {
//				System.out.println("Found type "+gobView.getGobReferance().getTypeUuid());
				GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobPropertyDefinition.getGobType());
				if (gob!=null) {
//					System.out.println("Got GOB "+gob.getUuid());
					if (gob.getUuid().equals(gobView.getGobReferance().getTypeUuid())) {
//						System.out.println("Can link Direct");
						canLink=true;
					} else {
						List<GOB> gobDescendants=adventureProjectModel.getGOBChildren(gob);
						for (GOB g:gobDescendants) {
//							System.out.println("Test GOB "+g.getUuid());
							if (g.getUuid().equals(gobView.getGobReferance().getTypeUuid())) {
								canLink=true;
//								System.out.println("Can link");
								break;
							}
						}
					}
				} else {
					// TODO: Log a warning here
				}
			}
			return canLink;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public void dragOnto(AdventureProjectModel adventureProjectModel, ViewDrawable viewDrawable) {
			if (viewDrawable instanceof GobView destinationGob) {
				if (canDragOnto(adventureProjectModel, viewDrawable)) {
					GOBInstance gobInstance=gobView.getGobReferance().getGobInstance(adventureProjectModel);
					if (!gobPropertyDefinition.isArray()) {
						ConnectorLineView c;
						if (connectors.size()>0) {
							c=connectors.get(0);
						}else {
							c=new ConnectorLineView();
							connectors.add(c);
						}
						
						if (destinationGob.getGobReferance().equals(c.getDestinationReferance())) {
							gobInstance.getProperty(gobPropertyDefinition).setValue(null);
							connectors.clear();
						} else {
							gobInstance.getProperty(gobPropertyDefinition).setValue(destinationGob.getGobReferance().getGobInstance(adventureProjectModel));
							c.setDestinationReferance(destinationGob.getGobReferance());
						}
						c.setConnectorView(ConnectorView.this);
						adventureProjectModel.fireDataInstanceChange(this,gobView.getGobReferance().getGobInstance(adventureProjectModel));
						adventureProjectModel.getGOBDataTableModel(gobView.getGobReferance().getGob(adventureProjectModel)).changeRowByUUID(gobView.getGobReferance().getUuid());
						adventureProjectModel.fireFileGameObjectChange(this, gobView.getGobReferance().getGob(adventureProjectModel));
					} else {
						// Test if this already exists if so break the link it. It not add it.
						Object object=gobInstance.getProperty(gobPropertyDefinition).getValue();
						List<GOBInstance> gobInstances;
						if (object instanceof GOBInstance objInstance) {
							gobInstances=new ArrayList<>();
							gobInstances.add(objInstance);
							gobInstance.getProperty(gobPropertyDefinition).setValue(gobInstances);
							} else {
							gobInstances=(List<GOBInstance>)object;
						}
						
						if (gobInstances==null) {
							gobInstances=new ArrayList<GOBInstance>();
							gobInstance.getProperty(gobPropertyDefinition).setValue(gobInstances);
						}
						// Find current
						int current=-1;
						for (int t=0;t<gobInstances.size();t++) {
							if (gobInstances.get(t).getUuid().equals(destinationGob.getGobReferance().getUuid())) {
								current=t;
								break;
							}
						}
						if (current!=-1) {
							// Remove this item and connector.
							gobInstances.remove(current);
							for (int t=0;t<connectors.size();t++) {
								if (destinationGob.getGobReferance().equals(connectors.get(t).getDestinationReferance())) {
									connectors.remove(t);
									break;
								}
							}
						} else {
							// Add a new item and connector.
							gobInstances.add(destinationGob.getGobReferance().getGobInstance(adventureProjectModel));
							//gobView.refreshConnectors(adventureProjectModel);
							ConnectorLineView c=new ConnectorLineView();
							connectors.add(c);
							c.setDestinationReferance(destinationGob.getGobReferance());
							c.setConnectorView(ConnectorView.this);

						}
						adventureProjectModel.fireDataInstanceChange(this,gobView.getGobReferance().getGobInstance(adventureProjectModel));
						adventureProjectModel.getGOBDataTableModel(gobView.getGobReferance().getGob(adventureProjectModel)).changeRowByUUID(gobView.getGobReferance().getUuid());
						adventureProjectModel.fireFileGameObjectChange(this, gobView.getGobReferance().getGob(adventureProjectModel));
					}
				}
			}
		}
	
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public List<BaseViewAction> draggedTo(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel, int x, int y, int vx, int vy, int sx, int sy) {
			// Show a menu to link or add a GOBInstance here. If this is an embedded type it can only be added.
			GOB childGobType=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobPropertyDefinition.getGobType());
			boolean isEmbedded=!childGobType.getType().isBase();
			boolean isHasDifferentChildrenTypes=adventureProjectModel.getGOBChildren(childGobType).size()>0;
			
			List<BaseViewAction> menuItems=new ArrayList<>();
			
			GOBInstance gobInstance=gobView.getGobReferance().getGobInstance(adventureProjectModel);
			GOBProperty<GOBInstance> linkedIntoProperty=(GOBProperty<GOBInstance>) gobInstance.getProperty(gobPropertyDefinition);
			
			if (!isEmbedded || isHasDifferentChildrenTypes) {
				// TODO: What should I do when this happens
			}
			
			if (!isEmbedded) {
				// Add menu item to create new
				menuItems.add(new NewGobInstanceToView(viewEditor, gobView.getGobReferance().getGobInstance(adventureProjectModel), linkedIntoProperty));
				// Link a gob in
				menuItems.add(new LinkGobInstanceToView(viewEditor, gobView.getGobReferance().getGobInstance(adventureProjectModel),linkedIntoProperty));
			} else if (isHasDifferentChildrenTypes){
				// Show a menu even for embedded as we need to pick a type\
				NewGobInstanceWizard wizard=new NewGobInstanceWizard(adventureProjectModel,viewEditor.getView(),viewEditor.getViewDesigner().getActiveMouseX(), viewEditor.getViewDesigner().getActiveMouseY(), viewEditor.getViewDesigner().getActiveMouseScreenX(), viewEditor.getViewDesigner().getActiveMouseScreenY(),gobInstance,linkedIntoProperty);
				wizard.setLocation(viewEditor.getViewDesigner().getActiveMouseScreenX(), viewEditor.getViewDesigner().getActiveMouseScreenY());				
			} else {
				// No embedded allocated create one.
				
				GOBInstance gobNewInstance=new GOBInstance();
				gobNewInstance.setGobType(gobPropertyDefinition.getGobType());
				
				var gobDataTableModel=adventureProjectModel.getGOBDataTableModel(childGobType);
				int pos=gobDataTableModel.addRow(gobNewInstance);
				gobDataTableModel.fireTableRowsInserted(pos, pos);

				// Add instance reference to view
				GOBReferance gobReferance=new GOBReferance();
				gobReferance.setTypeUuid(childGobType.getUuid());
				gobReferance.setUuid(gobNewInstance.getUuid());
				
				GobView newGobView=new GobView();
				newGobView.setGobReferance(gobReferance);
				newGobView.setBounds(new Rectangle(x, y, 200, 40));
				newGobView.recalcSize(adventureProjectModel);
				newGobView.setView(gobView.getView());
				newGobView.getView().getGobs().add(newGobView);

				if (linkedIntoProperty!=null) {
					if (linkedIntoProperty.getGOBPropertyDefinition().isArray()) {
						Object value=linkedIntoProperty.getValue();
						if (value==null) {
							var list=new ArrayList();
							list.add(gobReferance.getGobInstance(adventureProjectModel));
							linkedIntoProperty.setValue(list);
						} else if (value instanceof List list) {
							list.add(gobReferance.getGobInstance(adventureProjectModel));
						} else {
							log.error("Unable to add linked type to base object '"+value.getClass().getName()+"'");
						}
					}else {
						linkedIntoProperty.setValue(gobReferance.getGobInstance(adventureProjectModel));
					}
					
					GOB changedGob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobView.getGobReferance().getTypeUuid());
					adventureProjectModel.fireDataInstanceChange(this,gobView.getGobReferance().getGobInstance(adventureProjectModel));
					adventureProjectModel.getGOBDataTableModel(changedGob).changeRowByUUID(gobView.getGobReferance().getUuid());
					adventureProjectModel.fireFileGameObjectChange(this, changedGob);
				}
				
				newGobView.getView().touch();
				adventureProjectModel.fireDataInstanceChange(this, gobNewInstance);
				adventureProjectModel.fireFileGameObjectChange(this, childGobType);
				
				ConnectorLineView c;
				if (!linkedIntoProperty.getGOBPropertyDefinition().isArray() && connectors.size()>0) {
					c=connectors.get(0);
				}else {
					c=new ConnectorLineView();
					c.setConnectorView(ConnectorView.this);
					connectors.add(c);
				}
				c.setDestinationReferance(newGobView.getGobReferance());

			}
			return menuItems;
		}
	}
}
