package com.inkus.infomancerforge.beans.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.Alignment;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.Paragraph;
import com.inkus.infomancerforge.ImageUtilities.FontType;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBProperty;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBReferance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.actions.BaseViewAction;
import com.inkus.infomancerforge.editor.actions.SetViewTypeGobInstance;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

public class GobView implements ViewDrawable{
	private static final long serialVersionUID = 1L;

	private static int ROUNDED_SIZE=5;
	private static int MARGIN=5;
	private static int H_GAP=3;
	private static int V_GAP=3;
	
	public enum ViewMode {
		Tag(FluentUiRegularMZ.TAG_20,180,Alignment.Center){
			public Rectangle calculateBounds(AdventureProjectModel adventureProjectModel,GobView gobView) {
				return new Rectangle(gobView.bounds.x,gobView.bounds.y,getWidth(),(int)(Math.max(20,(Math.ceil((float)(gobView.headingParagraph.maxHeight+V_GAP*2))/20f)*20)));
			}
			
			public boolean available(AdventureProjectModel adventureProjectModel,GobView gobView) {
				return gobView.getGobReferance().getGob(adventureProjectModel).isNamed();
			}
		},
		Summary(FluentUiRegularMZ.SLIDE_LAYOUT_20,180,Alignment.Center){
			public Rectangle calculateBounds(AdventureProjectModel adventureProjectModel,GobView gobView) {
				return new Rectangle(gobView.bounds.x,gobView.bounds.y,getWidth(),(int)(Math.max(20,(Math.ceil((float)(gobView.summaryParagraph.maxHeight+V_GAP*2)/20f)*20))));
			}
		},
		Fields(FluentUiRegularMZ.SLIDE_TEXT_24,180,Alignment.Left){
			public Rectangle calculateBounds(AdventureProjectModel adventureProjectModel,GobView gobView) {
				float total=gobView.headingParagraph.maxHeight;
				total+=V_GAP;
				for (int t=0;t<gobView.fieldParagraphs.size();t++) {
					total+=Math.max(gobView.fieldParagraphs.get(t).maxHeight,gobView.fieldValueParagraphs.get(t).maxHeight);
				}
				return new Rectangle(gobView.bounds.x,gobView.bounds.y,getWidth(),(int)(Math.max(20,(Math.ceil((float)(total+V_GAP*2)/20f)*20))));
			}
		},
		Full(FluentUiRegularMZ.PREVIEW_LINK_24,180,Alignment.Left){
			public Rectangle calculateBounds(AdventureProjectModel adventureProjectModel,GobView gobView) {
				float total=gobView.headingParagraph.maxHeight;
				total+=gobView.summaryParagraph.maxHeight;
				total+=V_GAP*2;
				for (int t=0;t<gobView.fieldParagraphs.size();t++) {
					total+=Math.max(gobView.fieldParagraphs.get(t).maxHeight,gobView.fieldValueParagraphs.get(t).maxHeight);
				}
				return new Rectangle(gobView.bounds.x,gobView.bounds.y,getWidth(),(int)(Math.max(20,(Math.ceil((float)(total+V_GAP*2)/20f)*20))));
			}
		};
		
		private Ikon ikon;
		private int width;
		private Alignment topAlignment;
		
		private ViewMode(Ikon ikon,int width,Alignment topAlignment) {
			this.ikon=ikon;
			this.width=width;
			this.topAlignment=topAlignment;
		}
		
		public Ikon getIkon() {
			return ikon;
		}
		
		public int getWidth() {
			return width;
		}
		
		public Alignment getTopAlignment() {
			return topAlignment;
		}

		public boolean available(AdventureProjectModel adventureProjectModel,GobView gobView) {
			return true;
		}
		
		public int getTextWidth() {
			return getWidth()-MARGIN*2;
		}
		
		public int getNameWidth() {
			return (int)(getTextWidth()-H_GAP)/3;
		}
		
		public int getFieldWidth() {
			return getTextWidth()-H_GAP-getNameWidth();
		}
		
		public abstract Rectangle calculateBounds(AdventureProjectModel adventureProjectModel,GobView gobView);
	}
	
	private Rectangle bounds;
	
	private GOBReferance gobReferance;
	private Map<String,ConnectorView> connectors=new HashMap<>();
	
	private ViewMode viewMode=ViewMode.Summary;
	
	transient View view;
	transient String summary=null;
	transient Paragraph summaryParagraph=null;
	transient Paragraph headingParagraph=null;
	transient List<Color> fieldColors=new ArrayList<>();
	transient List<Paragraph> fieldParagraphs=new ArrayList<>();
	transient List<Paragraph> fieldValueParagraphs=new ArrayList<>();
	transient boolean recalBounds=true;
	
	public GobView() {
	}
	
	public String getUuid() {
		return gobReferance.getUuid();
	}
	
	public View getView() {
		return view;
	}

	public void setView(View view) {
		System.out.println("View set "+view.getName());
		this.view = view;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public GOBReferance getGobReferance() {
		return gobReferance;
	}

	public void setGobReferance(GOBReferance gobReferance) {
		this.gobReferance = gobReferance;
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}
	
	// This will position this connector in a suitable place considering others and the current layout
	public void positionConnector(int pos,int count,ConnectorView connectorView) {
		float widthPer=1f/count;
		float x=(float)pos*widthPer+widthPer/2f;
		connectorView.setX(x);
	}

	@SuppressWarnings("unchecked")
	public boolean refreshConnectors(AdventureProjectModel adventureProjectModel) {
		recalcSize(adventureProjectModel);
		boolean changed=false;
		var properties=adventureProjectModel.getAllGobProperties(gobReferance.getGob(adventureProjectModel));
		Set<String> expiredKeys=new HashSet<>(connectors.keySet());
		Map<String,Integer> newKeys=new HashMap<>();
		int pos=0;
		int count=0;
		for (var p:properties) {
			if (p.getType()==Type.GOB && p.getGobType()!=null) {
				count++;
			}
		}
		
		for (var p:properties) {
			if (p.getType()==Type.GOB && p.getGobType()!=null) {
				expiredKeys.remove(p.getGobFieldName());
				if (!connectors.containsKey(p.getGobFieldName())) {
					var connectorView=new ConnectorView();
					
					positionConnector(pos,count,connectorView);
					connectors.put(p.getGobFieldName(), connectorView);
					newKeys.put(p.getGobFieldName(), pos);
				}
				GOBInstance gobInstance=gobReferance.getGobInstance(adventureProjectModel);
				GOBProperty<?> property=gobInstance.getProperty(p);
				Object value=property!=null?property.getValue():null;
				List<GOBReferance> destinationReferances=new ArrayList<>();
				if (p.isArray()) {
					if (value!=null && value instanceof List<?> destGobInstances) {
						System.out.println("Found gob destination ");
						GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstance.getGobType());
						for (GOBInstance gi:(List<GOBInstance>)destGobInstances) {
							if (gi!=null) {
								destinationReferances.add(new GOBReferance(gob,gi));
							}
						}
					}
				} else {
					if (value!=null && value instanceof GOBInstance destGobInstance) {
						System.out.println("Found gob destination ");
						GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstance.getGobType());
						destinationReferances.add(new GOBReferance(gob,destGobInstance));
					}
				}
				changed|=connectors.get(p.getGobFieldName()).setGobPropertyDefinitions(this,p,destinationReferances);
				pos++;
			}
		}
		
	// Position New keys
//		for (var k:newKeys.keySet()) {
//			changed=true;
//			var connectorView=connectors.get(k);
//			if (bounds.width>bounds.height) {
//				connectorView.moveTo((int)(bounds.getMaxX()+bounds.width*newKeys.get(k)/connectors.size()+bounds.width/connectors.size()/2),(int)bounds.getMaxY() );
//			} else {
//				connectorView.moveTo((int)bounds.getMaxX(), (int)(bounds.getMaxY()+bounds.height*newKeys.get(k)/connectors.size()+bounds.height/connectors.size()/2));
//			}
//		}
		
		// Remove connectors that we no longer have
		for (String k:expiredKeys) {
			changed=true;
			connectors.remove(k);
		}
		
		return changed;
	}
	
	@Override
	public void recalcSize(AdventureProjectModel adventureProjectModel) {
		recalBounds=true;
	}

	private void recalcSize(Graphics2D g,AdventureProjectModel adventureProjectModel) {
		if (gobReferance!=null) {
			GOB gob=gobReferance.getGob(adventureProjectModel);
			GOBInstance gobInstance=gobReferance.getGobInstance(adventureProjectModel);
			if (!gob.isNamed()) {
				headingParagraph=new Paragraph();
			} else {
				String heading=gobInstance.getName();
				if (heading==null || heading.length()==0) {
					heading="~~~";
				}
				headingParagraph=ImageUtilities.breakParagraphIntoLines(ImageUtilities.getFont(FontType.Bold, ImageUtilities.VIEW_GOB_HEADING_SIZE),g.getFontRenderContext(),heading, viewMode.getTextWidth(), Alignment.Left);
			}
			
			summary=getSummary(adventureProjectModel,gob,gobInstance);
			if (summary!=null && summary.length()>0) {
				summaryParagraph=ImageUtilities.breakParagraphIntoLines(ImageUtilities.getFont(FontType.Regular, ImageUtilities.VIEW_GOB_SUMMARY_SIZE),g.getFontRenderContext(),summary, viewMode.getTextWidth(), Alignment.Left);
			}else {
				summaryParagraph=new Paragraph();
			}
			
			fieldParagraphs.clear();
			fieldValueParagraphs.clear();
			
			List<GOBPropertyDefinition> gobPropertyDefinitions=adventureProjectModel.getAllGobProperties(gobReferance.getGob(adventureProjectModel));
			var properties=gobReferance.getGobInstance(adventureProjectModel).getProperties();
			for (GOBPropertyDefinition gobPropertyDefinition:gobPropertyDefinitions) {
				// TODO: Allow for arrays to be included here (Limited to first {n} values).
				if (!gobPropertyDefinition.isArray() && gobPropertyDefinition.getType()!=Type.GOB && gobPropertyDefinition.getType()!=Type.ID) {
					Paragraph nameParagraph=null;
					Paragraph valueParagraph=null;
					//int w=(int)(bounds.getWidth()-13);
					
					String name=gobPropertyDefinition.getName();
					if (name!=null && name.length()>0) {
						nameParagraph=ImageUtilities.fitParagraphIntoLine(ImageUtilities.getFont(FontType.Light, ImageUtilities.VIEW_GOB_FIELD_SIZE),g,name, viewMode.getNameWidth(), Alignment.Left);
						nameParagraph.overrideAlignment=Alignment.Right;
					}
					
					Object value=null;
					if (properties.containsKey(gobPropertyDefinition.getGobFieldName())) {
						value=properties.get(gobPropertyDefinition.getGobFieldName()).getValue();
						if (value!=null) {
							switch (gobPropertyDefinition.getType()) {
//							case Audio:
//								break;
							case Boolean:
								valueParagraph=ImageUtilities.breakParagraphIntoLines(ImageUtilities.getFont(FontType.Regular, ImageUtilities.VIEW_GOB_FIELD_SIZE),g.getFontRenderContext(), value.toString(), viewMode.getFieldWidth(), Alignment.Left);
								break;
							case Float:
								valueParagraph=ImageUtilities.breakParagraphIntoLines(ImageUtilities.getFont(FontType.Regular, ImageUtilities.VIEW_GOB_FIELD_SIZE),g.getFontRenderContext(), value.toString(), viewMode.getFieldWidth(), Alignment.Left);
								valueParagraph.overrideAlignment=Alignment.Right;
								break;
							case GOB:
								break;
							case ID:
								break;
//							case Image:
//								break;
							case Integer:
								valueParagraph=ImageUtilities.breakParagraphIntoLines(ImageUtilities.getFont(FontType.Regular, ImageUtilities.VIEW_GOB_FIELD_SIZE),g.getFontRenderContext(), value.toString(), viewMode.getFieldWidth(), Alignment.Left);
								valueParagraph.overrideAlignment=Alignment.Right;
								break;
							case String:
								valueParagraph=ImageUtilities.breakParagraphIntoLines(ImageUtilities.getFont(FontType.Regular, ImageUtilities.VIEW_GOB_FIELD_SIZE),g.getFontRenderContext(), value.toString(), viewMode.getFieldWidth(), Alignment.Left);
								break;
							default:
								break;
							}
						}
					}
					if (nameParagraph!=null && valueParagraph!=null) {
						fieldParagraphs.add(nameParagraph);
						fieldColors.add(gobPropertyDefinition.getColor());
						fieldValueParagraphs.add(valueParagraph);
					}
				}
			}
		}
		
		bounds=viewMode.calculateBounds(adventureProjectModel, this);
		refreshConnectors(adventureProjectModel);
		recalBounds=false;
	}
	
	@Override
	public Rectangle bounds() {
		return bounds;
	}

	@Override
	public void moveTo(int x, int y) {
		bounds=new Rectangle(x,y,bounds.width,bounds.height);
	}
	
	@Override
	public ViewDrawable findOver(Point p) {
		if (bounds.contains(p)) {
			return this;
		}
		for (var c:connectors.values()) {
			var found=c.findOver(p);
			if (found!=null) {
				return found;
			}
		}
		return null;
	}

	@Override
	public boolean isVisable(Rectangle r) {
		return false;
	}
	
	private String getSummary(AdventureProjectModel adventureProjectModel, GOB gob, GOBInstance gobInstance) {
		String summary=gob.getSummary();
		if (summary!=null && summary.length()>0) {
			summary=adventureProjectModel.embedLuaTokens(summary, gobInstance);
		}
		return summary;
	}

	public float getHeadingHeight(int gap) {
		return headingParagraph.maxHeight==0?0:headingParagraph.maxHeight+gap;
	}
	
	public float drawHeading(AdventureProjectModel adventureProjectModel, Graphics2D g2, float y) {
		GOB gob=gobReferance.getGob(adventureProjectModel);
		Color backgroundColor=gob.getColorBackground();
		if (backgroundColor==null) {
			backgroundColor=Color.gray;
		}
		g2.setColor(ImageUtilities.getSuitableTextColorForBackground(backgroundColor));
		
		ImageUtilities.drawParagraph(
				g2, 
				headingParagraph, 
				new Rectangle2D.Double(bounds.getX()+MARGIN,bounds.getY()+y,viewMode.getTextWidth(),headingParagraph.maxHeight), 
				viewMode.getTopAlignment());
		return getHeadingHeight(summaryParagraph.maxHeight>0?V_GAP:0);
	}
	
	public float getSummaryHeight(int gap) {
		return summaryParagraph.maxHeight==0?0:summaryParagraph.maxHeight+gap;
	}
	
	public float drawSummary(AdventureProjectModel adventureProjectModel, Graphics2D g2, float y) {
		GOB gob=gobReferance.getGob(adventureProjectModel);
		Color backgroundColor=gob.getColorBackground();
		if (backgroundColor==null) {
			backgroundColor=Color.gray;
		}
		g2.setColor(ImageUtilities.getSuitableTextColorForBackground(backgroundColor));
		
		ImageUtilities.drawParagraph(
				g2, 
				summaryParagraph, 
				new Rectangle2D.Double(bounds.getX()+MARGIN,bounds.getY()+y,viewMode.getTextWidth(),summaryParagraph.maxHeight), 
				viewMode.getTopAlignment());
		return getSummaryHeight(fieldParagraphs.size()>0?V_GAP:0);
	}

	public float getFieldHeight(Paragraph fieldName,Paragraph fieldValue) {
		return Math.max(fieldName.maxHeight,fieldValue.maxHeight);
	}
	
	public float drawField(AdventureProjectModel adventureProjectModel, Graphics2D g2, float y,Paragraph fieldName,Paragraph fieldValue,Color fieldColor) {
		GOB gob=gobReferance.getGob(adventureProjectModel);

		var nameBounds=new Rectangle2D.Double(bounds.getX()+MARGIN,bounds.getY()+y,viewMode.getNameWidth(),fieldName.maxHeight);
		var parBounds=new Rectangle2D.Double(bounds.getX()+MARGIN+viewMode.getTextWidth()-viewMode.getFieldWidth(),bounds.getY()+y,viewMode.getFieldWidth(),fieldValue.maxHeight);
		var fullBounds=nameBounds.createUnion(parBounds);
		
		Color backgroundColor=fieldColor!=null?fieldColor:gob.getColorBackground();
		if (backgroundColor==null) {
			backgroundColor=Color.gray;
		}
		g2.setColor(backgroundColor);
		g2.fillRect((int)fullBounds.getMinX(),(int)fullBounds.getMinY(),(int)Math.ceil(fullBounds.getWidth()),(int)Math.ceil(fullBounds.getHeight()));

		g2.setColor(ImageUtilities.getSuitableTextColorForBackground(backgroundColor));
		
		ImageUtilities.drawParagraph(
				g2, 
				fieldName, 
				nameBounds, 
				Alignment.Left);

		ImageUtilities.drawParagraph(
				g2, 
				fieldValue, 
				parBounds, 
				Alignment.Left);

		return getFieldHeight(fieldName,fieldValue);
	}

	@Override
	public void paintDrawable(AdventureProjectModel adventureProjectModel, Graphics2D g2, boolean isSelected, boolean isHighlighted, ViewEditor viewEditor) {
		if (recalBounds) {
			recalcSize(g2, adventureProjectModel);
		}
		
		// Get background color
		GOB gob=gobReferance.getGob(adventureProjectModel);
		Color backgroundColor=gob.getColorBackground();
		if (backgroundColor==null) {
			backgroundColor=Color.gray;
		}
		
		// Fill background
		g2.setColor(backgroundColor);
		g2.fillRoundRect(bounds.x,bounds.y,bounds.width,bounds.height,ROUNDED_SIZE,ROUNDED_SIZE);

		float y=bounds.height;
		switch (viewMode) {
		case Fields:
			y-=getHeadingHeight(fieldParagraphs.size()>0?V_GAP:0);
			for (int t=0;t<fieldParagraphs.size();t++) {
				y-=getFieldHeight(fieldParagraphs.get(t), fieldValueParagraphs.get(t));
			}
			break;
		case Full:
			y-=getHeadingHeight(getSummaryHeight(0)>0?V_GAP:0);
			y-=getSummaryHeight(fieldParagraphs.size()>0?V_GAP:0);
			for (int t=0;t<fieldParagraphs.size();t++) {
				y-=getFieldHeight(fieldParagraphs.get(t), fieldValueParagraphs.get(t));
			}
			break;
		case Summary:
			y-=getSummaryHeight(0);
			break;
		case Tag:
			y-=getHeadingHeight(0);
		default:
			break;
		}
		
		y=y/2;
		
		switch (viewMode) {
		case Fields:
			y+=drawHeading(adventureProjectModel, g2, y);
			for (int t=0;t<fieldParagraphs.size();t++) {
				y+=drawField(adventureProjectModel, g2, y, fieldParagraphs.get(t), fieldValueParagraphs.get(t),fieldColors.get(t));
			}
			break;
		case Full:
			y+=drawHeading(adventureProjectModel, g2, y);
			y+=drawSummary(adventureProjectModel, g2, y);
			for (int t=0;t<fieldParagraphs.size();t++) {
				y+=drawField(adventureProjectModel, g2, y, fieldParagraphs.get(t), fieldValueParagraphs.get(t),fieldColors.get(t));
			}
			break;
		case Summary:
			drawSummary(adventureProjectModel, g2, y);
			break;
		case Tag:
			drawHeading(adventureProjectModel, g2, y);
		default:
			break;
		}
		
		// Draw outline
		g2.setStroke(new BasicStroke((float)(2f/viewEditor.getView().getScale())));
		g2.setColor(backgroundColor.brighter().brighter());
		g2.drawRoundRect(bounds.x,bounds.y,bounds.width,bounds.height,ROUNDED_SIZE,ROUNDED_SIZE);
		
		if (isSelected) {
			g2.setStroke(new BasicStroke((float)(2f/viewEditor.getView().getScale())));
			g2.setColor(ImageUtilities.VIEW_SELECTED_OUTLINE_COLOUR);
			g2.drawRoundRect(bounds.x,bounds.y,bounds.width,bounds.height,ROUNDED_SIZE,ROUNDED_SIZE);
		}
		if (isHighlighted) {
			float gap=(float)(4f/viewEditor.getView().getScale());
			int gabInt=(int)Math.ceil(gap);
			g2.setStroke(new BasicStroke(gap/2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,0, new float[]{gap}, 0));	
			g2.setColor(ImageUtilities.VIEW_SELECTED_OUTLINE_COLOUR);
			g2.drawRoundRect((int)(bounds.x-gabInt),(int)(bounds.y-gabInt),(int)(bounds.width+2*gabInt),(int)(bounds.height+2*gabInt),ROUNDED_SIZE,ROUNDED_SIZE);
		}
	}

	@Override
	public Collection<ConnectorView> getChildren(){
		return connectors.values();
	}
	
	@Override
	public List<BaseViewAction> getActions(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel){
		List<BaseViewAction> actions=new ArrayList<>();
		
		for (var vm:ViewMode.values()) {
			if (vm.available(adventureProjectModel, this)) {
				actions.add(new SetViewTypeGobInstance(viewEditor, getGobReferance().getTypeUuid(),getGobReferance().getUuid(), vm , adventureProjectModel));
			}
		}
		
		// Add change all if there are others of this type in the display
		int count=0;
		for (var g:view.getGobs()) {
			if (g.getGobReferance().getTypeUuid().equals(gobReferance.getTypeUuid())) {
				count++;
			}
		}
		
		if (count>1) {
			actions.add(null);
			
			for (var vm:ViewMode.values()) {
				if (vm.available(adventureProjectModel, this)) {
					actions.add(new SetViewTypeGobInstance(viewEditor, getGobReferance().getTypeUuid(),null, vm , adventureProjectModel));
				}
			}
		}
		
		// TODO: Add remove from view if type is base
		
		// TODO: Add delete from system
		
		return actions;
	}

	public RightDragHandler getRightDragHandler() {
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bounds, connectors, gobReferance, viewMode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GobView other = (GobView) obj;
		return Objects.equals(bounds, other.bounds) && Objects.equals(connectors, other.connectors)
				&& Objects.equals(gobReferance, other.gobReferance) && viewMode == other.viewMode;
	}

}
