package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.formdev.flatlaf.extras.components.FlatLabel;

public class NamedResourceCellRenderer<type extends NamedResource> extends GeneralCellRenderer {
	static private final Logger log = LogManager.getLogger(NamedResourceCellRenderer.class);

	private FlatLabel field=null;
	
	public NamedResourceCellRenderer(CellType type) {
		super(type);
	}

	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (field==null) {
			field=new FlatLabel();
			field.setOpaque(true);
		}
		if (value!=null) {
			if (value instanceof NamedResource namedResource) {
				field.setText(namedResource!=null?namedResource.getName():"");
			} else {
				log.warn("Type was not a named resource '"+value.getClass().getName()+"'");
				field.setText("");
			}
		}else {
			field.setText("");
		}
		ImageUtilities.setTableCellBackgroundColor(field, isSelected, hasFocus, row, column);
		
		return field;
	}	
}
