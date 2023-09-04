package com.inkus.infomancerforge.display.factories.cells;

import com.inkus.infomancerforge.editor.swing.NullNumberFormatter;

public class DoubleCellRenderer extends AbstractNumberCellRenderer {

	public DoubleCellRenderer(CellType type) {
		super(NullNumberFormatter.createDoubleFormatter(),type);
	}
	
	public DoubleCellRenderer(CellType type,int precision) {
		super(NullNumberFormatter.createDoubleFormatter(precision),type);
	}
}
