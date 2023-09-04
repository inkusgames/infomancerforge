package com.inkus.infomancerforge.display.factories.cells;

import com.inkus.infomancerforge.editor.swing.NullNumberFormatter;

public class IntegerCellRenderer extends AbstractNumberCellRenderer {

	public IntegerCellRenderer(CellType type) {
		super(NullNumberFormatter.createIntegerFormatter(),type);
	}
}
