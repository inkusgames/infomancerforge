package com.inkus.infomancerforge.beans.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.inkus.infomancerforge.Alignment;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.ImageUtilities.FontType;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.ConnectorType;
import com.inkus.infomancerforge.beans.gobs.GOBReferance;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.actions.BaseViewAction;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

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
			
			if (connecterView.getGobPropertyDefinition().getConnectorType()==null) {
				connecterView.getGobPropertyDefinition().setConnectorType(ConnectorType.Solid);
			}
			
			switch (connecterView.getGobPropertyDefinition().getConnectorType()) {
			case Dashes:
				g2.setStroke(new BasicStroke(s,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,s/2,new float[] { 4.0f, 4.0f},0f));
				break;
			case Dots:
				g2.setStroke(new BasicStroke(s,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,s/2,new float[] { 1.0f, 3.0f},0f));
				break;
			case DotDash:
				g2.setStroke(new BasicStroke(s,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,s/2,new float[] { 1.0f, 3.0f, 4f, 3f},0f));
				break;
			case Solid:
			default:
				g2.setStroke(new BasicStroke(s,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
				break;
			}
			
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
				
				if (connecterView.isArray() && connecterView.getGobPropertyDefinition().isShowOrdered()) {
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
				
				if (connecterView.getGobPropertyDefinition().isLabelConnector()) {
					var pathI=cc.getPathIterator(null,1);
					float[] coords=new float[6];
					
					float dx=0,dy=0;
					float lx=0,ly=0;
					float length=0;
					List<Point2D> path=new ArrayList<>();
					while (!pathI.isDone()) {
						switch (pathI.currentSegment(coords)) {
						case PathIterator.SEG_LINETO:
							dx=coords[0]-lx;
							dy=coords[1]-ly;
							length+=Math.sqrt(dx*dx+dy*dy);
							//g2.drawLine((int)lx,(int)ly,(int)coords[0],(int)coords[1]);
						case PathIterator.SEG_MOVETO:
							path.add(new Point2D.Float(coords[0],coords[1]));
							lx=coords[0];
							ly=coords[1];
							break;
						case PathIterator.SEG_QUADTO:
						case PathIterator.SEG_CUBICTO:
						case PathIterator.SEG_CLOSE:
							// Turn this into a log
							System.out.println("Unexpected segment type "+pathI);
							break;
						}
						
						pathI.next();
					}
					if (path.get(0).getX()>path.get(path.size()-1).getX()) {
						Collections.reverse(path);
					}
					
					g2.setFont(ImageUtilities.getFont(FontType.Regular, 9));
					String label=connecterView.getGobPropertyDefinition().getName();
			        FontRenderContext frc = g2.getFontRenderContext();
			        GlyphVector gv = g2.getFont().createGlyphVector(frc, label);
			        int gnum = gv.getNumGlyphs();
			        int fp=0;
			        float fx=(float)path.get(fp).getX();
			        float fy=(float)path.get(fp).getY();
			        
			        float gx=0;
			        float width=0;
			        for (int i = 0; i < gnum; i++) {
			        	width+=gv.getGlyphMetrics(i).getAdvance();
			        }
			        // Only draw if we can fit the text on the line
			        if (width<length*.8f) {
				        float advance=(length-width)/2;
				        float a=0;
				        int lineSeg=1;
				        for (int i = 0; i < gnum; i++) {
				        	float w=gv.getGlyphMetrics(i).getAdvance();
				        	float px=0,py=0;
				        	
				        	advance+=w/2;
				        	while (advance>0 && lineSeg<path.size()) {
				        		float tx=(float)path.get(lineSeg).getX(),ty=(float)path.get(lineSeg).getY();
				        		dx=tx-fx;
				        		dy=ty-fy;
				        		float d=(float)Math.sqrt(dx*dx+dy*dy);
				        		if (d<advance) {
				        			advance-=d;
				        			fx=tx;
				        			fy=ty;
				        			lineSeg++;
				        		} else {
				        			a=(float)Math.atan2(dy, dx);
				        			fx=fx+(float)Math.cos(a)*advance;
				        			fy=fy+(float)Math.sin(a)*advance;
				        			
				        			px=fx+(float)Math.cos(a-Math.PI/2)*2;
				        			py=fy+(float)Math.sin(a-Math.PI/2)*2;
				        			
				        			advance=0;
				        		}
				        	}
				        	advance+=w/2;
				        	
				            AffineTransform at = AffineTransform.getTranslateInstance(px,py);
				            at.rotate(a);
				            at.translate(-gx-w/2, 0);
				            gx+=w;
				            
				            Shape glyph = gv.getGlyphOutline(i);
				            Shape transformedGlyph = at.createTransformedShape(glyph);
				            g2.fill(transformedGlyph);
				        }
					}
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

	public int getSortOrder() {
		return 50;
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
