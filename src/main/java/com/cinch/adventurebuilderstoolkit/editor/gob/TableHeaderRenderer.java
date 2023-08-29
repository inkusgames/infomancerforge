package com.cinch.adventurebuilderstoolkit.editor.gob;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class TableHeaderRenderer implements TableCellRenderer {
	private JLabel header=new JLabel();
	private TableModel tableModel;
	
	public TableHeaderRenderer(TableModel tableModel) {
		this.tableModel=tableModel;
		header.setOpaque(true);
		header.setForeground((Color)UIManager.get("TableHeader.foreground"));
		header.setBackground((Color)UIManager.get("TableHeader.background"));
		header.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 1, 0, 0, ((Color)UIManager.get("TableHeader.background")).brighter()), 
						BorderFactory.createMatteBorder(0, 0, 0, 1, ((Color)UIManager.get("TableHeader.background")).darker())
						),
				BorderFactory.createEmptyBorder(0,4,0,4)));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Class<?> columnClass=tableModel.getColumnClass(column);
		if (Boolean.class.isAssignableFrom(columnClass)) {
			header.setHorizontalAlignment(SwingConstants.CENTER);
		} else if (Number.class.isAssignableFrom(columnClass)) {
			header.setHorizontalAlignment(SwingConstants.RIGHT);
		}else {
			header.setHorizontalAlignment(SwingConstants.LEFT);
		}
		
		header.setText(value.toString());
		return header;
	}

}
