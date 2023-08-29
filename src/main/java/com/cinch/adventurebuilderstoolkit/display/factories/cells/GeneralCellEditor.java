package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

public abstract class GeneralCellEditor {
	private CellType type;
	private boolean selectable=true;
	private boolean editable=true;
	private List<CellEditorListener> listeners=new ArrayList<>();
	
	public GeneralCellEditor(boolean selectable,boolean editable,CellType type) {
		this.selectable=selectable;
		this.editable=editable;
		this.type=type;
	}

	public boolean isCellEditable(EventObject anEvent) {
		return editable;
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return selectable;
	}
	
	public void addCellEditorListener(CellEditorListener l) {
		listeners.add(l);
	}

	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}

	public boolean stopCellEditing() {
		return true;
	}
	
	public CellType getType() {
		return type;
	}

	public void setType(CellType type) {
		this.type = type;
	}

	public void cancelCellEditing() {
		ChangeEvent e=new ChangeEvent(this);
		for (var l:listeners) {
			l.editingCanceled(e);
		}
	}

	public abstract Object getCellEditorValue();
	public abstract JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected);
}
