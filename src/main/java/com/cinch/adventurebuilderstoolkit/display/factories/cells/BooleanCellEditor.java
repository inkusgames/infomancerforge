package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.components.FlatCheckBox;

public class BooleanCellEditor extends GeneralCellEditor {

	private FlatCheckBox field=null;
	
	public BooleanCellEditor(CellType type) {
		super(true,true,type);
	}

	@Override
	public Object getCellEditorValue() {
		return field==null?null:field.isSelected();
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		if (field==null) {
			field=new FlatCheckBox();
			field.setFocusable(false);
			field.setHorizontalAlignment(SwingConstants.CENTER);
			field.setBorderPainted(true);
		}
		if (value==null || value instanceof Boolean) {
			field.setSelected(value!=null && true==(Boolean)value);
		}
//		SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//            	field.requestFocus();
//            }
//        });
		getType().adjustComponent(field);
		return field;
	}

}
