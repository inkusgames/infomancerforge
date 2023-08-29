package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.cinch.adventurebuilderstoolkit.beans.NamedResource;

public class NamedResourceListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	public NamedResourceListCellRenderer() {
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		return super.getListCellRendererComponent(list, value!=null?((NamedResource)value).getName():"", index, isSelected, cellHasFocus);
	}

}
