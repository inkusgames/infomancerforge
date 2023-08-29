package com.cinch.adventurebuilderstoolkit.editor.gob;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class GobTableColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = 1L;

	public GobTableColumnModel() {
		TableColumn name=new TableColumn(0, 200);
//		name.set
		
		addColumn(name);
		addColumn(new TableColumn(1, 100));
		addColumn(new TableColumn(2, 30));
		addColumn(new TableColumn(3, 30));
	}
}
