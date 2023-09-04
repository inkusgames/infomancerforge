package com.inkus.infomancerforge;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public enum Alignment {
	Top{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)(destination.getX()+(destination.getWidth()-source.getWidth())/2), (float)destination.getY()); 
		}
	},
	Left{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)destination.getX(), (float)(destination.getY()+(destination.getHeight()-source.getHeight())/2)); 
		}
	},
	Right{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)(destination.getX()+destination.getWidth()-source.getWidth()), (float)(destination.getY()+(destination.getHeight()-source.getHeight())/2)); 
		}
	},
	Bottom{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)(destination.getX()+(destination.getWidth()-source.getWidth())/2), (float)(destination.getY()+destination.getHeight()-source.getHeight())); 
		}
	},
	Center{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)(destination.getX()+(destination.getWidth()-source.getWidth())/2), (float)(destination.getY()+(destination.getHeight()-source.getHeight())/2)); 
		}
	},
	TopLeft{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)destination.getX(), (float)destination.getY()); 
		}
	},
	TopRight{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)(destination.getX()+destination.getWidth()-source.getWidth()), (float)destination.getY()); 
		}
	},
	BottomLeft{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)destination.getX(), (float)(destination.getY()+destination.getHeight()-source.getHeight())); 
		}
	},
	BottomRight{
		public Point2D position(Rectangle2D source,Rectangle2D destination) {
			return new Point2D.Float((float)(destination.getX()+destination.getWidth()-source.getWidth()), (float)(destination.getY()+destination.getHeight()-source.getHeight())); 
		}
	};
	
	public abstract Point2D position(Rectangle2D source,Rectangle2D destination);
}
