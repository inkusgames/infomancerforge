package com.inkus.infomancerforge.editor.gob;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.display.factories.cells.BooleanCellEditor;
import com.inkus.infomancerforge.display.factories.cells.CellType;
import com.inkus.infomancerforge.display.factories.cells.ComboBoxNamedResourceCellEditor;
import com.inkus.infomancerforge.display.factories.cells.DoubleCellEditor;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellEditor;
import com.inkus.infomancerforge.display.factories.cells.IntegerCellEditor;
import com.inkus.infomancerforge.display.factories.cells.LabelCellNonEdit;
import com.inkus.infomancerforge.display.factories.cells.StringCellEditor;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer.SpecialValues;
import com.inkus.infomancerforge.editor.AdventureProjectModel;

public class GOBInstanceTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	private Map<GOBPropertyDefinition.Type, GeneralCellEditor> knowEditors=new HashMap<>();
	private Map<Integer, GeneralCellEditor> knowPrecisionEditors=new HashMap<>();
	private GeneralCellEditor defaultEditor=new LabelCellNonEdit(com.inkus.infomancerforge.display.factories.cells.CellType.Normal);

	private GeneralCellEditor editor;
	private AdventureProjectModel adventureProjectModel;
	private GOBDataTableModel gobDataTableModel;
	
	public GOBInstanceTableCellEditor(AdventureProjectModel adventureProjectModel, GOBDataTableModel gobDataTableModel) {
		this.adventureProjectModel=adventureProjectModel;
		this.gobDataTableModel=gobDataTableModel;
		
		knowEditors.put(GOBPropertyDefinition.Type.ID, new StringCellEditor(com.inkus.infomancerforge.display.factories.cells.CellType.Normal));
		knowEditors.put(GOBPropertyDefinition.Type.String, new StringCellEditor(com.inkus.infomancerforge.display.factories.cells.CellType.Normal));
		knowEditors.put(GOBPropertyDefinition.Type.Boolean, new BooleanCellEditor(com.inkus.infomancerforge.display.factories.cells.CellType.Normal));
		knowEditors.put(GOBPropertyDefinition.Type.Integer, new IntegerCellEditor(com.inkus.infomancerforge.display.factories.cells.CellType.Normal));
		knowEditors.put(GOBPropertyDefinition.Type.Float, new DoubleCellEditor(com.inkus.infomancerforge.display.factories.cells.CellType.Normal));
	}
	
	public void install(JTable table) {
		table.setDefaultEditor(Object.class, this);
		table.setDefaultEditor(Boolean.class, this);
		table.setDefaultEditor(Integer.class, this);
		table.setDefaultEditor(int.class, this);
		table.setDefaultEditor(Double.class, this);
		table.setDefaultEditor(double.class, this);
		table.setDefaultEditor(String.class, this);
	}

	@Override
	public Object getCellEditorValue() {
		Object value=editor!=null?editor.getCellEditorValue():null;
		return value instanceof SpecialValues?null:value;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		var cProperty=gobDataTableModel.getColumnGOBPropertyDefinition(column);
		var cType=cProperty.getType();
		
		if (cType==Type.Float) {
			GOBPropertyDefinition d=gobDataTableModel.getColumnGOBPropertyDefinition(column);
			int p=d.getPrecision();
			if (!knowPrecisionEditors.containsKey(p)) {
				knowPrecisionEditors.put(p,new DoubleCellEditor(CellType.Normal,p));
			}
			editor=knowPrecisionEditors.get(p);
		} else if (cType==Type.GOB) {
			editor=new ComboBoxNamedResourceCellEditor<GOBInstance>(adventureProjectModel,GOBInstance.class,cProperty.getGobType(),com.inkus.infomancerforge.display.factories.cells.CellType.Normal);
		}else {
			editor=knowEditors.get(cType);
			if (editor==null) {
				editor=defaultEditor;
			}
		}
		JComponent component=editor.getTableCellEditorComponent(table, value, true);
		ImageUtilities.setTableCellBackgroundColor(component, isSelected, true, row, column);
		return component;
	}

}
