package com.cinch.adventurebuilderstoolkit.display.factories;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class DynamicListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private static ListDisplayFactory listDisplayFactory=new ListDisplayFactory();
	
	public DynamicListCellRenderer(){
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component baseComponent=super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		Component component=listDisplayFactory.getListDisplay((Serializable) value);
		if (component!=null) {
			component.setBackground(baseComponent.getBackground());
		}else {
			component=baseComponent;
		}
		
		return component;
	}

}
