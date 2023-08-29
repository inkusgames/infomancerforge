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

public class DoubleCellEditor extends GeneralCellEditor {

	private FlatFormattedTextField field=null;
	private Integer precision;

	public DoubleCellEditor(CellType type) {
		super(false,false,type);
	}

	public DoubleCellEditor(CellType type,int precision) {
		this(type);
		this.precision=precision;
	}

	@Override
	public Object getCellEditorValue() {
		if (field.getText()==null || field.getText().length()==0) {
			return SpecialValues.UnSet;
		} else {
			return field.getValue()==null?null:((Number)field.getValue()).doubleValue();
		}
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		if (field==null) {
			field=new FlatFormattedTextField();
			field.setFormatterFactory(new AbstractFormatterFactory() {
				@Override
				public AbstractFormatter getFormatter(JFormattedTextField tf) {
					if (precision==null) {
						return NullNumberFormatter.createDoubleFormatter();
					} else {
						return NullNumberFormatter.createDoubleFormatter(precision);
					}
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
