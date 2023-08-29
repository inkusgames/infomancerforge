package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;

public class EnumCellEditor extends ComboBoxCellEditor<Object> {

	public EnumCellEditor(CellType type) {
		super(type);
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		setValues(value.getClass().getEnumConstants());
		return super.getTableCellEditorComponent(table, value, isSelected);
	}

}
