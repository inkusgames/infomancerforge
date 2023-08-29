package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.ImageUtilities.FontType;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.GeneralCellRenderer.SpecialValues;
import com.cinch.adventurebuilderstoolkit.editor.swing.NullNumberFormatter;
import com.formdev.flatlaf.extras.components.FlatFormattedTextField;

public class IntegerCellEditor extends GeneralCellEditor {

	private FlatFormattedTextField field=null;

	public IntegerCellEditor(CellType type) {
		super(false,false, type);
	}
	
	@Override
	public Object getCellEditorValue() {
		if (field.getText()==null || field.getText().length()==0) {
			return SpecialValues.UnSet;
		} else {
			return field.getValue()==null?null:((Number)field.getValue()).intValue();
		}
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		if (field==null) {
			field=new FlatFormattedTextField();
			field.setFormatterFactory(new AbstractFormatterFactory() {
				@Override
				public AbstractFormatter getFormatter(JFormattedTextField tf) {
					return NullNumberFormatter.createIntegerFormatter();
				}
			});
			field.setHorizontalAlignment(JTextField.RIGHT);
			field.setFont(ImageUtilities.getFont(FontType.MonoRegular, field.getFont().getSize()));
		}
		if (value instanceof Number) {
			field.setValue(value);
		} else {
			field.setValue(null);
		}
		getType().adjustComponent(field);
		return field;
	}

}
