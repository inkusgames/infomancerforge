package com.inkus.infomancerforge.display.factories.cells;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataListener;

import com.formdev.flatlaf.extras.components.FlatComboBox;

public class ComboBoxCellEditor<type> extends GeneralCellEditor {

	private FlatComboBox<type> field = null;
	private type[] values;
	private ListCellRenderer<type> renderer = null;

	public ComboBoxCellEditor(type[] values,CellType type) {
		super(false, false,type);
		this.values = values;
	}

	protected ComboBoxCellEditor(CellType type) {
		super(false, false,type);
	}

	public void setValues(type[] values) {
		this.values = values;
	}

	public ListCellRenderer<type> getRenderer() {
		return renderer;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setRenderer(ListCellRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public Object getCellEditorValue() {
		return field == null ? null : field.getSelectedItem();
	}

	@Override
	public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		if (field == null) {
			field = new FlatComboBox<>();
			field.setModel(new MyComboBoxModel());
			if (renderer != null) {
				field.setRenderer(renderer);
			}
		}
		field.setSelectedItem(value);
		getType().adjustComponent(field);
		return field;
	}

	class MyComboBoxModel implements ComboBoxModel<type> {

		private List<ListDataListener> listeners = new ArrayList<>();
		private type selected;

		@Override
		public int getSize() {
			return values.length;
		}

		@Override
		public type getElementAt(int index) {
			return values[index];
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.remove(l);
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setSelectedItem(Object anItem) {
			selected = (type) anItem;
		}

		@Override
		public Object getSelectedItem() {
			return selected;
		}

	}

}
