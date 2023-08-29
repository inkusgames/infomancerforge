package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import com.cinch.adventurebuilderstoolkit.editor.swing.NullNumberFormatter;

public class DoubleCellRenderer extends AbstractNumberCellRenderer {

	public DoubleCellRenderer(CellType type) {
		super(NullNumberFormatter.createDoubleFormatter(),type);
	}
	
	public DoubleCellRenderer(CellType type,int precision) {
		super(NullNumberFormatter.createDoubleFormatter(precision),type);
	}
}
