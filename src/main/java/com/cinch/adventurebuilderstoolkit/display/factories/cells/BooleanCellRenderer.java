package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox.State;

public class BooleanCellRenderer extends GeneralCellRenderer {

	private FlatTriStateCheckBox field=null;

	public BooleanCellRenderer(CellType type) {
		super(type);
	}

	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (field==null) {
			field=new FlatTriStateCheckBox();
			field.setBorderPainted(true);
			field.setHorizontalAlignment(SwingConstants.CENTER);
		}
		if (value == SpecialValues.Mixed) {
			field.setState(State.INDETERMINATE);
		} else {
			field.setState((value!=null && true==(Boolean)value)?State.SELECTED:State.UNSELECTED);
		}
		getType().adjustComponent(field);
		return field;
	}

}
