package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;

public abstract class GeneralCellRenderer {
	protected CellType type;

	public enum SpecialValues {
		Mixed,
		UnSet // For multi row value items this will not change them, for single items will set them to null
	}
	
	public GeneralCellRenderer(CellType type) {
		this.type=type;
	}

	public CellType getType() {
		return type;
	}

	public void setType(CellType type) {
		this.type = type;
	}
	
	public abstract JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
}
