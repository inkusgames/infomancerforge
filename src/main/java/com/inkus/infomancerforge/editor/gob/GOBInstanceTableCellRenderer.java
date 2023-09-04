package com.inkus.infomancerforge.editor.gob;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.display.factories.cells.BooleanCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.CellType;
import com.inkus.infomancerforge.display.factories.cells.DoubleCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.IntegerCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.NamedResourceCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.StringCellRenderer;

public class GOBInstanceTableCellRenderer extends DefaultTableCellRenderer {
	static private final Logger log=LogManager.getLogger(GOBInstanceTableCellRenderer.class);
	private static final long serialVersionUID = 1L;
	
	private Map<GOBPropertyDefinition.Type, GeneralCellRenderer> knowRenderers=new HashMap<>();
	private Map<Integer, GeneralCellRenderer> knowPrecisionRenderers=new HashMap<>();
	private GeneralCellRenderer defaultEditor=new StringCellRenderer(CellType.Normal);

	private GeneralCellRenderer renderers;
	private GOBDataTableModel gobDataTableModel;

	public GOBInstanceTableCellRenderer(GOBDataTableModel gobDataTableModel) {
		this.gobDataTableModel=gobDataTableModel;
		
		knowRenderers.put(GOBPropertyDefinition.Type.ID, new StringCellRenderer(CellType.Normal));
		knowRenderers.put(GOBPropertyDefinition.Type.String, new StringCellRenderer(CellType.Normal));
		knowRenderers.put(GOBPropertyDefinition.Type.Boolean, new BooleanCellRenderer(CellType.Normal));
		knowRenderers.put(GOBPropertyDefinition.Type.Integer, new IntegerCellRenderer(CellType.Normal));
		knowRenderers.put(GOBPropertyDefinition.Type.Float, new DoubleCellRenderer(CellType.Normal));
		knowRenderers.put(GOBPropertyDefinition.Type.GOB, new NamedResourceCellRenderer<GOBInstance>(CellType.Normal));
	}


	public void install(JTable table) {
		table.setDefaultRenderer(Object.class, this);
		table.setDefaultRenderer(Boolean.class, this);
		table.setDefaultRenderer(Integer.class, this);
		table.setDefaultRenderer(int.class, this);
		table.setDefaultRenderer(Double.class, this);
		table.setDefaultRenderer(double.class, this);
		table.setDefaultRenderer(String.class, this);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		var cProperty=gobDataTableModel.getColumnGOBPropertyDefinition(column);

		if (cProperty.getType()==Type.Float) {
			GOBPropertyDefinition d=gobDataTableModel.getColumnGOBPropertyDefinition(column);
			int p=d.getPrecision();
			if (!knowPrecisionRenderers.containsKey(p)) {
				knowPrecisionRenderers.put(p,new DoubleCellRenderer(CellType.Normal,p));
			}
			renderers=knowPrecisionRenderers.get(p);
		} else {
			renderers=knowRenderers.get(cProperty.getType());
		}
		
		if (renderers==null) {
			log.warn("Unable to find rendered for type "+cProperty.getType().name());
			renderers=defaultEditor;
		}
		JComponent component=renderers.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		ImageUtilities.setTableCellBackgroundColor(component,isSelected, hasFocus, row, column);

		return component;
	}

}
