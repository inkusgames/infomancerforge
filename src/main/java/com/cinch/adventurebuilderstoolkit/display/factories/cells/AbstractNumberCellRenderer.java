package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import java.awt.Color;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.ImageUtilities.FontType;
import com.cinch.adventurebuilderstoolkit.editor.swing.NullNumberFormatter;
import com.formdev.flatlaf.extras.components.FlatLabel;

public abstract class AbstractNumberCellRenderer extends GeneralCellRenderer {
	static private final Logger log = LogManager.getLogger(AbstractNumberCellRenderer.class);

	private FlatLabel field=null;
	private NullNumberFormatter formatter;

	public AbstractNumberCellRenderer(NullNumberFormatter formatter,CellType type) {
		super(type);
		this.formatter=formatter;
	}

	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (field==null) {
			field=new FlatLabel();
			field.setHorizontalAlignment(JTextField.RIGHT);
			field.setOpaque(true);
			field.setFont(ImageUtilities.getFont(FontType.MonoRegular, field.getFont().getSize()));
			
		}
		if (value instanceof Number valueNumber) {
			try {
				if (valueNumber.doubleValue()<0) {
					field.setForeground(Color.red);
				} else {
					field.setForeground(Color.green);
				}
				field.setText(formatter.valueToString(value));
			} catch (ParseException e) {
				log.error(e.getMessage(),e);
				field.setText(value.toString());
			}
		} else {
			field.setText(value==null?null:value.toString());
		}
		getType().adjustComponent(field);

		return field;
	}	
}
