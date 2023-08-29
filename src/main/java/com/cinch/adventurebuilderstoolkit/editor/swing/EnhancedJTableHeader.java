package com.cinch.adventurebuilderstoolkit.editor.swing;

import java.awt.*; 
import java.awt.event.*; 
import javax.swing.JTable; 
import javax.swing.table.*;

import com.formdev.flatlaf.extras.components.FlatTableHeader; 

public class EnhancedJTableHeader extends FlatTableHeader { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table; 

	public EnhancedJTableHeader(TableColumnModel cm, JTable table) { 
		setColumnModel(cm);
		this.table = table; 
		addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) { 
				doMouseClicked(e); 
			} 
		}); 
	} 

	public void doMouseClicked(MouseEvent e) { 
		if (!getResizingAllowed()) 
			return; 
		if (e.getClickCount() != 2) { 
			return; 
		} 
		TableColumn column = getResizingColumn(e.getPoint(), 
				columnAtPoint(e.getPoint())); 
		if (column == null) { 
			return; 
		} 
		int oldMinWidth = column.getMinWidth(); 
		column.setMinWidth(getRequiredColumnWidth(column)); 
		setResizingColumn(column); 
		table.doLayout(); 
		column.setMinWidth(oldMinWidth); 
	} 

	private int getRequiredColumnWidth(TableColumn column) { 
		int modelIndex = column.getModelIndex(); 
		TableCellRenderer renderer; 
		Component component; 
		int requiredWidth = 0; 
		int rows = table.getRowCount(); 
		for (int i = 0; i < rows; i++) { 
			renderer = table.getCellRenderer(i, modelIndex); 
			Object valueAt = table.getValueAt(i, modelIndex); 
			component = 
					renderer.getTableCellRendererComponent(table, valueAt, 
							false, false, i, 
							modelIndex); 
			requiredWidth = Math.max(requiredWidth, 
					component.getPreferredSize().width + 2); 
		} 
		return requiredWidth; 
	} 

	private TableColumn getResizingColumn(Point p, int column) { 
		if (column == -1) { 
			return null; 
		} 
		Rectangle r = getHeaderRect(column); 
		r.grow(-3, 0); 
		if (r.contains(p)) { 
			return null; 
		} 
		int midPoint = r.x + r.width / 2; 
		int columnIndex; 
		if (getComponentOrientation().isLeftToRight()) { 
			columnIndex = (p.x < midPoint) ? column - 1 : column; 
		} else { 
			columnIndex = (p.x < midPoint) ? column : column - 1; 
		} 
		if (columnIndex == -1) { 
			return null; 
		} 
		return getColumnModel().getColumn(columnIndex); 
	} 
}