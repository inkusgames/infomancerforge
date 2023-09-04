package com.inkus.infomancerforge.display.factories;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.display.factories.cells.BooleanCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.CellType;
import com.inkus.infomancerforge.display.factories.cells.DoubleCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.IntegerCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.StringCellRenderer;

public class GeneralTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private Map<Class<?>, GeneralCellRenderer> knowEditors=new HashMap<>();
	private GeneralCellRenderer defaultEditor=new StringCellRenderer(CellType.Normal);

	public GeneralTableCellRenderer() {
		knowEditors.put(String.class, new StringCellRenderer(CellType.Normal));
		knowEditors.put(Boolean.class, new BooleanCellRenderer(CellType.Normal));
		knowEditors.put(boolean.class, new BooleanCellRenderer(CellType.Normal));
		knowEditors.put(Integer.class, new IntegerCellRenderer(CellType.Normal));
		knowEditors.put(int.class, new IntegerCellRenderer(CellType.Normal));
		knowEditors.put(Double.class, new DoubleCellRenderer(CellType.Normal));
		knowEditors.put(double.class, new DoubleCellRenderer(CellType.Normal));
		knowEditors.put(Enum.class, new StringCellRenderer(CellType.Normal));
	}
	
	public void install(JTable table) {
		for (var c:knowEditors.keySet()) {
			table.setDefaultRenderer(c, this);
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		GeneralCellRenderer editor=null;
		if (value!=null) {
			editor=knowEditors.get(value.getClass());
		}
		if (editor==null) {
			editor=defaultEditor;
		}
		JComponent component=editor.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		ImageUtilities.setTableCellBackgroundColor(component,isSelected, hasFocus, row, column);

		return component;
	}
}
