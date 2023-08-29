package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.ImageUtilities.FontType;
import com.formdev.flatlaf.extras.components.FlatLabel;

public class StringCellArrayElementLabelRenderer extends GeneralCellRenderer{

	private FlatLabel label=null;
	private String text;

	public StringCellArrayElementLabelRenderer(String text,CellType type) {
		super(type);
		this.text=text;
	}

	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (label==null) {
			label=new FlatLabel();
			label.setFont(ImageUtilities.getFont(FontType.MonoRegular, 12));
			label.setOpaque(true);
			label.setHorizontalAlignment(FlatLabel.RIGHT);
		}
		label.setText(text);//value==null?null:value.toString());
		getType().adjustComponent(label);
		return label;
	}

}
