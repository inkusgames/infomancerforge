package com.inkus.infomancerforge.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;

import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.swing.NamedResourceFilterComboBox;

import ca.odell.glazedlists.EventList;

public class ComboBoxNamedResourceCellEditor<type extends NamedResource> extends GeneralCellEditor{
	@SuppressWarnings("unused")
	private AdventureProjectModel adventureProjectModel;

	private NamedResourceFilterComboBox<type> field = null;
	private EventList<type> values;
	private ListCellRenderer<type> renderer = null;
	
	public ComboBoxNamedResourceCellEditor(AdventureProjectModel adventureProjectModel,Class<type> typeClass,CellType type) {
		this(adventureProjectModel,typeClass,null,type);
	}

	public ComboBoxNamedResourceCellEditor(AdventureProjectModel adventureProjectModel,Class<type> typeClass,String typeSet,CellType type) {
		super(true,true,type);
		this.adventureProjectModel=adventureProjectModel;
		values=adventureProjectModel.getNamedResourceModel(typeClass,typeSet);
		setRenderer(new NamedResourceListCellRenderer());
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
			field = new NamedResourceFilterComboBox<>(values);
			if (renderer != null) {
				field.setRenderer(renderer);
			}
		}
		field.setSelectedItem(value);
		return field;
	}
	
}
