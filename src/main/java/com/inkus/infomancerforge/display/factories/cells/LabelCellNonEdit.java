package com.inkus.infomancerforge.display.factories.cells;

import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.formdev.flatlaf.extras.components.FlatLabel;

public class LabelCellNonEdit extends GeneralCellEditor {

	private FlatLabel label=null;
	private Object value=null;
	
	public LabelCellNonEdit(CellType type) {
		super(true,true,type);
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return false;
	}

	@Override
	public Object getCellEditorValue() {
		return value;
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		this.value=value;
		if (label==null) {
			label=new FlatLabel();
		}
		label.setText(value==null?null:value.toString());
		getType().adjustComponent(label);
		return label;
	}

}
