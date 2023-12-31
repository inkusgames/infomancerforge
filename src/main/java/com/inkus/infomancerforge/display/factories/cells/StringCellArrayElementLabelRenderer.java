package com.inkus.infomancerforge.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.formdev.flatlaf.extras.components.FlatLabel;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.ImageUtilities.FontType;

public class StringCellArrayElementLabelRenderer extends GeneralCellRenderer{

	private FlatLabel label=null;
	private String text;

	public StringCellArrayElementLabelRenderer(String text,CellType type) {
		super(type);
		this.text=text;
		build();
	}

	private void build() {
		label=new FlatLabel();
		label.setFont(ImageUtilities.getFont(FontType.MonoRegular, 12));
		label.setOpaque(true);
		label.setHorizontalAlignment(FlatLabel.RIGHT);
	}
	
	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		label.setText(text);
		getType().adjustComponent(label);
		return label;
	}

}
