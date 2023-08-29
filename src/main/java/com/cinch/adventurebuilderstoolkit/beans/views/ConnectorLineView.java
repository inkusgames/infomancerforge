package com.cinch.adventurebuilderstoolkit.beans.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.cinch.adventurebuilderstoolkit.Alignment;
import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.ImageUtilities.FontType;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBReferance;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.actions.BaseViewAction;
import com.cinch.adventurebuilderstoolkit.editor.gob.ViewEditor;

public class ConnectorLineView implements ViewDrawable {
	private static final long serialVersionUID = 1L;
	
	public final static int CENTER_SIZE=10; 

	private String uuid;
	
	private ConnecterLineEndpointView connecterLineEndpointView;
	private GOBReferance destinationReferance;
	private transient ConnectorView connecterView;
	private transient List<ConnecterLineEndpointView> connecterLineEndpointViews=new ArrayList<>();
	private transient Point center=null;
	
	public ConnectorLineView() {
		uuid=UUID.randomUUID().toString();
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public void setConnectorView(ConnectorView connectorView) {
		this.connecterView=connectorView;
		if (connecterLineEndpointView==null) {
			connecterLineEndpointView=new ConnecterLineEndpointView();
			connecterLineEndpointViews.clear();
		}
		connecterLineEndpointView.setConnectorLineView(this);
		
	}
	
	public ConnecterLineEndpointView getConnecterLineEndpointView() {
		return connecterLineEndpointView;
	}

	public GOBReferance getDestinationReferance() {
		return destinationReferance;
	}

	public void setDestinationReferance(GOBReferance destinationReferance) {
		this.destinationReferance = destinationReferance;
	}

	public ConnectorView getConnecterView() {
		return connecterView;
	}
	
	public boolean isDestinationInView() {
		return getDestinationGobView()!=null;
	}
	
	// If this returns null the gob view is not visible on the current view.
	public GobView getDestinationGobView() {
//		System.out.println("Find Ref:"+destinationReferance);
		return connecterView.getGobView().getView().findReferance(destinationReferance);
	}

	@Override
	public Rectangle bounds() {
		var b=connecterView.bounds().createUnion(connecterLineEndpointView.bounds());
		return new Rectangle((int)b.getMinX(),(int)b.getMinY(),(int)b.getWidth(),(int)b.getHeight()); 
	}

	@Override
	public boolean isVisable(Rectangle r) {
		return r.intersects(bounds());
	}

	@Override
	public ViewDrawable findOver(Point p) {
		// TODO: Search lines too if we want to do edits on those
		if (center!=null) {
			if (center.distance(p)<=CENTER_SIZE/2) {
				return this;
			}
		}
		return connecterLineEndpointView.findOver(p);
	}

	@Override
	public void moveTo(int x, int y) {
		// TODO: We should still consider how to handle selecting the actual lines
	}
	
	@Override
	public void paintDrawable(AdventureProjectModel adventureProjectModel, Graphics2D g2, boolean isSelected, boolean isHighlighted, ViewEditor viewEditor) {
		if (isDestinationInView()) {
			Color c;
			float s=(float)(2f/viewEditor.getView().getScale());
			g2.setStroke(new BasicStroke(s));
			
			Rectangle from=connecterView.bounds();
			Rectangle to=connecterLineEndpointView.bounds();
			
			if (connecterView.getGobPropertyDefinition()!=null) {
				c=connecterView.getGobPropertyDefinition().getColor();
				if (c==null) {
					c=Color.yellow;
				}
				g2.setColor(c);
				
				//g2.drawLine((int)from.getCenterX(),(int)from.getCenterY(),(int)to.getCenterX(),(int)to.getCenterY());
				
				CubicCurve2D cc = new CubicCurve2D.Double();
				// draw CubicCurve2D.Double with set coordinates
				// TODO: // Factor this with some consideration for the connectors facing away in perculiar ways from each other.
				int distance=Math.max(60, Math.min((int)Math.abs(from.getCenterX()-to.getCenterX()), (int)Math.abs(from.getCenterY()-to.getCenterY())));
				Point fromAdjustment=ImageUtilities.getLineAdjustment(connecterView.getXandY(),ConnectorView.SIZE+4);
				Point fromCurveAdjustment=ImageUtilities.getLineAdjustment(connecterView.getXandY(),distance);
				Point toAdjustment=ImageUtilities.getLineAdjustment(connecterLineEndpointView.getXandY(),ConnecterLineEndpointView.SIZE);
				Point toCurveAdjustment=ImageUtilities.getLineAdjustment(connecterLineEndpointView.getXandY(),distance);
				cc.setCurve(
						(int)from.getCenterX()+fromAdjustment.getX(),(int)from.getCenterY()+fromAdjustment.getY(), 
						(int)from.getCenterX()+fromCurveAdjustment.getX(),(int)from.getCenterY()+fromCurveAdjustment.getY(), 
						(int)to.getCenterX()+toCurveAdjustment.getX(),(int)to.getCenterY()+toCurveAdjustment.getY(), 
						(int)to.getCenterX()+toAdjustment.getX(),(int)to.getCenterY()+toAdjustment.getY());
				g2.draw(cc);
				
				if (connecterView.isArray()) {
					CubicCurve2D left = new CubicCurve2D.Double();
					CubicCurve2D right = new CubicCurve2D.Double();
					cc.subdivide(left,right);
					center=new Point((int)left.getX2(), (int)left.getY2());				
					g2.fillOval(center.x-CENTER_SIZE/2, center.y-CENTER_SIZE/2, CENTER_SIZE, CENTER_SIZE);
					if (isSelected) {
						int g=(int)Math.ceil(s*3/2);
						g2.setColor(ImageUtilities.VIEW_SELECTED_OUTLINE_COLOUR);
						g2.drawOval(center.x-CENTER_SIZE/2-g, center.y-CENTER_SIZE/2-g, CENTER_SIZE+g*2, CENTER_SIZE+g*2);
					}
					
					// Print index
					int index=connecterView.getConnectorIndex(destinationReferance)+1;
					var p=ImageUtilities.fitParagraphIntoLine(ImageUtilities.getFont(FontType.MonoBold, ImageUtilities.VIEW_GOB_ARRAY_SIZE),g2,""+index, CENTER_SIZE, Alignment.Center);
					g2.setColor(ImageUtilities.getSuitableTextColorForBackground(c));
					ImageUtilities.drawParagraph(g2, p, new Rectangle(center.x-CENTER_SIZE/2, center.y-CENTER_SIZE/2, CENTER_SIZE, CENTER_SIZE+2),Alignment.Center);
				}
			}
		}
	}

	@Override
	public void recalcSize(AdventureProjectModel adventureProjectModel) {
	}

	@Override
	public List<BaseViewAction> getActions(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel){
		return null;
	}

	@Override
	public RightDragHandler getRightDragHandler() {
		return new ConnectorOrderRightDragHandler();
	}

	@Override
	public Collection<? extends ViewDrawable> getChildren() {
//		System.out.println("Children of = "+this);
		if (connecterLineEndpointViews.size()==0) {
			connecterLineEndpointViews.add(connecterLineEndpointView);
		}

		return connecterLineEndpointViews;
	}

	@Override
	public int hashCode() {
		return Objects.hash(connecterLineEndpointView, destinationReferance);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectorLineView other = (ConnectorLineView) obj;
		return Objects.equals(connecterLineEndpointView, other.connecterLineEndpointView)
				&& Objects.equals(destinationReferance, other.destinationReferance);
	}

	class ConnectorOrderRightDragHandler implements RightDragHandler {

		@Override
		public ViewDrawable getDrawable() {
			return ConnectorLineView.this;
		}

		@Override
		public boolean canDragOnto(AdventureProjectModel adventureProjectModel, ViewDrawable viewDrawable) {
			if (viewDrawable instanceof ConnectorLineView clv) {
				if (clv.connecterView==connecterView) {
					System.out.println("FOUND!!!!!!!!!!!!!!!!!");
					return true;
				}
			}
			return false;
		}

		@Override
		public void dragOnto(AdventureProjectModel adventureProjectModel, ViewDrawable viewDrawable) {
			if (viewDrawable instanceof ConnectorLineView clv) {
				if (clv.connecterView==connecterView) {
					connecterView.swapOrder(adventureProjectModel,destinationReferance,clv.destinationReferance);
				}
			}
		}

		@Override
		public List<BaseViewAction> draggedTo(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel, int x, int y, int vx, int vy, int sx, int sy) {
			return null;
		}
		
	}
	
}
