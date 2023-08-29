package com.cinch.adventurebuilderstoolkit.editor.swing;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.cinch.adventurebuilderstoolkit.beans.NamedResource;

public class NamedResourceComboBoxRenderer extends BasicComboBoxRenderer{
	private static final long serialVersionUID = 1L;
	
	public NamedResourceComboBoxRenderer() {
	}
	
    @Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	return super.getListCellRendererComponent(list, value==null?null:((NamedResource)value).getName(), index, isSelected, cellHasFocus);
    }

}
