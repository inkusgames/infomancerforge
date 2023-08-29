package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.cinch.adventurebuilderstoolkit.editor.swing.FormColorCustomEditor;

public class ColorCellRenderer extends GeneralCellRenderer {
	private FormColorCustomEditor formColorCustomEditor;
	
	public ColorCellRenderer(CellType type) {
		super(type);
		formColorCustomEditor=new FormColorCustomEditor();
		formColorCustomEditor.setEnabled(false);
	}

	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Color color) {
			formColorCustomEditor.setColor(color);
		}
		getType().adjustComponent(formColorCustomEditor);
		return formColorCustomEditor;
	}

}
