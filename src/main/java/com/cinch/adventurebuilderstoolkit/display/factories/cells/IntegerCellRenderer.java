package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import com.cinch.adventurebuilderstoolkit.editor.swing.NullNumberFormatter;

public class IntegerCellRenderer extends AbstractNumberCellRenderer {

	public IntegerCellRenderer(CellType type) {
		super(NullNumberFormatter.createIntegerFormatter(),type);
	}
}
