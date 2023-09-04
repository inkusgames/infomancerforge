package com.inkus.infomancerforge.display.factories.cells;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.ImageUtilities.FontType;

public enum CellType {
	ArrayStart(null,ImageUtilities.getFont(FontType.MonoBold, 14),true),
	ArrayItem(null,null,false),
	ArrayLabel(ImageUtilities.PROPERTIES_ARRAY_LABEL,ImageUtilities.getFont(FontType.Light, 10),false),
	Normal(null,null,false);
	
	Font font;
	Color forgroundGroundColor;
	boolean borderTop=false;

	private CellType(Color forgroundGroundColor,Font font,boolean borderTop) {
		this.forgroundGroundColor = forgroundGroundColor;
		this.font = font;
		this.borderTop=borderTop;	
	}
	
	public Component adjustComponent(Component component) {
//		if (forgroundGroundColor!=null) {
//			component.setForeground(forgroundGroundColor);
//		}
//		if (font!=null) {
//			component.setFont(font);
//		}
////		
////		if (component instanceof JComponent jc) {
////			if (borderTop) {
////				jc.setBorder(BorderFactory.createLineBorder(Color.white));
////				
////			}else {
////				jc.setBorder(null);
////			}
////		}
//		
		return component;
	}
}