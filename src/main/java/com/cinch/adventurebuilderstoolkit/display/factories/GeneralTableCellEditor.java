package com.cinch.adventurebuilderstoolkit.display.factories;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.BooleanCellEditor;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.CellType;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.ComboBoxCellEditor;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.DoubleCellEditor;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.GeneralCellEditor;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.IntegerCellEditor;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.LabelCellNonEdit;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.StringCellEditor;

public class GeneralTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	private Map<Class<?>, GeneralCellEditor> knowEditors=new HashMap<>();
	private GeneralCellEditor defaultEditor=new LabelCellNonEdit(CellType.Normal);
	
	public List<EditorMap> editors=new ArrayList<>();

	private GeneralCellEditor editor;

	public GeneralTableCellEditor() {
		knowEditors.put(String.class, new StringCellEditor(CellType.Normal));
		knowEditors.put(Boolean.class, new BooleanCellEditor(CellType.Normal));
		knowEditors.put(boolean.class, new BooleanCellEditor(CellType.Normal));
		knowEditors.put(Integer.class, new IntegerCellEditor(CellType.Normal));
		knowEditors.put(int.class, new IntegerCellEditor(CellType.Normal));
		knowEditors.put(Double.class, new DoubleCellEditor(CellType.Normal));
		knowEditors.put(double.class, new DoubleCellEditor(CellType.Normal));
	}
	
	public void install(JTable table) {
		for (var c:knowEditors.keySet()) {
			table.setDefaultEditor(c, this);
		}
		for (var e:editors) {
			if (e.getClass()!=null) {
				table.setDefaultEditor(e.getClass(), this);
			}
		}
		table.setDefaultEditor(Object.class, this);
	}

	public void addRowEditor(int row,Class<?> classType,GeneralCellEditor editor) {
		editors.add(new EditorMap(row, null, classType, editor));
	}

	public void addColumnEditor(int column,Class<?> classType,GeneralCellEditor editor) {
		editors.add(new EditorMap(null, column, classType, editor));
	}

	public void addCellEditor(int row,int column,Class<?> classType,GeneralCellEditor editor) {
		editors.add(new EditorMap(row, column, classType, editor));
	}

	public void addClassEditor(Class<?> classType,GeneralCellEditor editor) {
		editors.add(new EditorMap(null, null, classType, editor));
	}
	
	@Override
	public Object getCellEditorValue() {
		return editor!=null?editor.getCellEditorValue():null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value!=null) {
			editor=knowEditors.get(value.getClass());
			int m=0;
			for (var editorMap:editors) {
				if (m<editorMap.getMatchFor(row, column, value)) {
					m=editorMap.getMatchFor(row, column, value);
					editor=editorMap.cellEditor;
				}
			}
			if (editor==null && value.getClass().isEnum()) {
				editor=new ComboBoxCellEditor<Enum<?>>((Enum[])value.getClass().getEnumConstants(),CellType.Normal);
			}
		} else {
			editor=null;
		}
		if (editor==null) {
			editor=defaultEditor;
		}
		
		JComponent component=editor.getTableCellEditorComponent(table, value, true);
//		component.setForeground((Color)UIManager.get("Table.selectionForeground"));
//		component.setBackground((Color)UIManager.get("Table.selectionBackground"));
//		component.setBorder((Border)UIManager.get("Table.focusSelectedCellHighlightBorder"));

		ImageUtilities.setTableCellBackgroundColor(component, isSelected, true, row, column);
		
		return component;
	}

	private static class EditorMap {
		private Integer row;
		private Integer column;
		private Class<?> classType;

		private GeneralCellEditor cellEditor;

		private EditorMap(Integer row, Integer column, Class<?> classType, GeneralCellEditor cellEditor) {
			super();
			this.row = row;
			this.column = column;
			this.classType = classType;
			this.cellEditor = cellEditor;
		}

		private int getMatchFor(int row, int column, Object value) {
			int match=0;
			if (this.row!=null && row==this.row) {
				match+=1;
			}
			if (this.column!=null && column==this.column) {
				match+=2;
			}
			if (!classType.isAssignableFrom(value.getClass())) {
				match=0;
			} else {
				match++;
			}
			return match;
		}
	}
}
