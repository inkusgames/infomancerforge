package com.inkus.infomancerforge.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.formdev.flatlaf.extras.components.FlatLabel;

public class StringCellRenderer extends GeneralCellRenderer{

	private FlatLabel label=null;

	public StringCellRenderer(CellType type) {
		super(type);
	}

	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (label==null) {
			label=new FlatLabel();
			label.setOpaque(true);
		}
		label.setText(value==null?null:value.toString());
		getType().adjustComponent(label);
		return label;
	}

}
