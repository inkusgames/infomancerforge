package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.cinch.adventurebuilderstoolkit.editor.swing.FormColorCustomEditor;

public class ColorCellEditor extends GeneralCellEditor {

	private FormColorCustomEditor formColorCustomEditor;

	public ColorCellEditor(CellType type) {
		this(null,type);
	}

	public ColorCellEditor(String description,CellType type) {
		super(true,true,type);
		formColorCustomEditor=new FormColorCustomEditor();
	}

	@Override
	public Object getCellEditorValue() {
		return formColorCustomEditor.getColor();
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		if (value instanceof Color color) {
			formColorCustomEditor.setColor(color);
		}
		getType().adjustComponent(formColorCustomEditor);
		return formColorCustomEditor;
	}

}
