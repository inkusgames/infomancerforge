package com.inkus.infomancerforge.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.formdev.flatlaf.extras.components.FlatTextField;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer.SpecialValues;

public class StringCellEditor extends GeneralCellEditor {

	private FlatTextField field=null;
	
	public StringCellEditor(CellType type) {
		super(false,false, type);
	}

	@Override
	public Object getCellEditorValue() {
		if (field.getText()==null || field.getText().length()==0) {
			return SpecialValues.UnSet;
		}
		return field==null?null:field.getText();
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		if (field==null) {
			field=new FlatTextField();
		}
		getType().adjustComponent(field);
		field.setText(value==null?null:value.toString());
		return field;
	}

}
