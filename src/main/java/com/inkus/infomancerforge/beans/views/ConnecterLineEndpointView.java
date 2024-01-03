package com.inkus.infomancerforge.beans.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.actions.BaseViewAction;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

public class ConnecterLineEndpointView implements ViewDrawable {
	private static final long serialVersionUID = 1L;
	
	public static int SIZE=6;
	private static Shape arrow=null;
	private static Shape crowFoot=null;

	// Positions around the frame of the GobView
	private float x=-1f;
	private float y=-1f;
	private String uuid;
	
	transient private ConnectorLineView connectorLineView;

	public ConnecterLineEndpointView() {
		uuid=UUID.randomUUID().toString();
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Point2D getXandY() {
		return new Point2D.Float(x,y);
	}

	private Shape getShape(AdventureProjectModel adventureProjectModel) {
		Shape shape;
		if (connectorLineView.getConnecterView().isReferance(adventureProjectModel)) {
			if (arrow==null) {
				synchronized (ConnectorLineView.class) {
					if (arrow==null) {
						GeneralPath gp=new GeneralPath();
						gp.moveTo(0, 0);
						gp.lineTo(3, 4);
						gp.lineTo(0, 3);
						gp.lineTo(-3, 4);
						gp.closePath();
						arrow=gp;
					}				
				}
			}
			shape=arrow;
		} else {
			if (crowFoot==null) {
				synchronized (ConnectorLineView.class) {
					if (crowFoot==null) {
						GeneralPath gp=new GeneralPath();
						gp.moveTo(0, 4);
						gp.lineTo(0, 0);
						gp.moveTo(0, 4);
						gp.lineTo(3, 0);
						gp.moveTo(0, 4);
						gp.lineTo(-3, 0);
						crowFoot=gp;
					}				
				}
			}
			shape=crowFoot;
		}
		return shape;
	}

	public void setConnectorLineView(ConnectorLineView connectorLineView) {
		x=-1f;
		y=-1f;
		this.connectorLineView=connectorLineView;
	}
	
	@Override
	public Rectangle bounds() {
		// TODO: This should be cached unless it needs to update
		if (connectorLineView==null) {
			return null;
		}

		GobView destination=connectorLineView.getDestinationGobView();
		if (destination!=null) {
			Rectangle gobBounds=destination.bounds();
			if (x==-1) {
				Rectangle toBounds=connectorLineView.getConnecterView().bounds();
				
				// Find best suggested location for edge. Line from center of the item we connecting to back to the start of the line segment.
				Line2D l=new Line2D.Double(gobBounds.getCenterX(),gobBounds.getCenterY(),toBounds.getCenterX(),toBounds.getCenterY());
				Rectangle2D r=new Rectangle2D.Double(gobBounds.getMinX(),gobBounds.getMinY(),gobBounds.getWidth(),gobBounds.getHeight());
				Point2D intersection=ImageUtilities.getLineToRectIntersection(l,r);
				
				if (intersection==null) {
					System.out.println("No intersection found");
					intersection=new Point2D.Double(toBounds.getCenterX(),toBounds.getCenterY());
				}
				moveTo((int)intersection.getX(), (int)intersection.getY());
			}
			
			int x,y;
			x=(int)(gobBounds.x+gobBounds.width*this.x-SIZE/2);
			y=(int)(gobBounds.y+gobBounds.height*this.y-SIZE/2);
			if (this.x==0) {
				x-=SIZE/2;
			}
			if (this.x==1) {
				x+=SIZE/2;
			}
			if (this.y==0) {
				y-=SIZE/2;
			}
			if (this.y==1) {
				y+=SIZE/2;
			}
			return new Rectangle(x,y,SIZE,SIZE);		
		}
		return null;
	}

	@Override
	public boolean isVisable(Rectangle r) {
		return false;
	}

	@Override
	public ViewDrawable findOver(Point p) {
		Rectangle bounds=bounds();
		if (bounds!=null && bounds.contains(p)) {
			double dx=p.x-bounds.getCenterX();
			double dy=p.y-bounds.getCenterY();
			double d=Math.sqrt(dx*dx+dy*dy);
			if (d<=SIZE/2) {
				return this;
			}
		}
		return null;	
	}

	@Override
	public void moveTo(int x, int y) {
		GobView destination=connectorLineView.getDestinationGobView();

		Rectangle gobBounds=destination.bounds();
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
	public void paintDrawable(AdventureProjectModel adventureProjectModel, Graphics2D g2, boolean isSelected, boolean isHighlighted, ViewEditor viewEditor) {
		Rectangle bounds=bounds();
		
		if (bounds!=null) {
			
			Color c;
			float s=(float)(2f/viewEditor.getView().getScale());
			g2.setStroke(new BasicStroke(s));
			
			if (connectorLineView.getConnecterView().getGobPropertyDefinition()!=null) {
				c=connectorLineView.getConnecterView().getGobPropertyDefinition().getColor();
				if (c==null) {
					GOB connectorGob=adventureProjectModel.getNamedResourceByUuid(GOB.class, connectorLineView.getConnecterView().getGobPropertyDefinition().getGobType());
					if (connectorGob!=null) {
						c=connectorGob.getColorBackground();
					}
				}
				if (c==null) {
					c=Color.yellow;
				}
				g2.setColor(c);
				
				double ax,ay,aa;
				
				if (x==0) {
					aa=Math.PI/2;
					ax=bounds.getMaxX()-1;
					ay=bounds.getCenterY();
				} else if (x==1){
					aa=-Math.PI/2;
					ax=bounds.getMinX()+1;
					ay=bounds.getCenterY();
				} else if (y==0){
					aa=Math.PI;
					ax=bounds.getCenterX();
					ay=bounds.getMaxY()-1;
				} else {
					aa=0;
					ax=bounds.getCenterX();
					ay=bounds.getMinY()+1;
				}
				
				g2.translate(ax, ay);
				g2.rotate(aa);
				g2.draw(getShape(adventureProjectModel));
				g2.rotate(-aa);
				g2.translate(-ax, -ay);
			}
			
			if (isSelected) {
				int g=(int)Math.ceil(s*3/2);
				g2.setColor(ImageUtilities.VIEW_SELECTED_OUTLINE_COLOUR);
				g2.drawOval(bounds.x-g,bounds.y-g,bounds.width+g*2,bounds.height+g*2);
			}
		}
	}

	@Override
	public void recalcSize(AdventureProjectModel adventureProjectModel) {
		bounds();
	}

	@Override
	public List<BaseViewAction> getActions(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel){
		return null;
	}

	@Override
	public RightDragHandler getRightDragHandler() {
		return null;
	}

	@Override
	public Collection<? extends ViewDrawable> getChildren() {
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnecterLineEndpointView other = (ConnecterLineEndpointView) obj;
		return Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
				&& Float.floatToIntBits(y) == Float.floatToIntBits(other.y);
	}

	public int getSortOrder() {
		return 100;
	}
	
}
