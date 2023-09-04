package com.inkus.infomancerforge.editor.gob;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.kordamp.ikonli.fluentui.FluentUiFilledAL;

import com.formdev.flatlaf.extras.components.FlatLabel;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.ImageUtilities.FontType;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;

public class GOBInstanceTableHeaderRenderer extends DefaultTableCellRenderer{
//	static private final Logger log=LogManager.getLogger(GOBInstanceTableHeaderRenderer.class);
	private static final long serialVersionUID = 1L;

	private GOBPropertyDefinition headerProperty;
	private GOB gob;
	private JPanel base=null;
	private FlatLabel icon;
	private RowSorter<? extends TableModel> rowSorter;
	
	public GOBInstanceTableHeaderRenderer(GOB gob,GOBPropertyDefinition headerProperty,RowSorter<? extends TableModel> rowSorter) {
		this.headerProperty=headerProperty;
		this.gob=gob;
		this.rowSorter=rowSorter;
	}

	private void updateSortIcon(int column) {
		Color foreground=(Color)UIManager.get("TableHeader.foreground");
		for (var sk:rowSorter.getSortKeys()) {
			if (column==sk.getColumn()) {
				switch(sk.getSortOrder()) {
				case ASCENDING:
					icon.setIcon(ImageUtilities.getIcon(FluentUiFilledAL.CARET_UP_24, foreground, (Integer)UIManager.get("TableHeader.height")/2));
					return;
				case DESCENDING:
					icon.setIcon(ImageUtilities.getIcon(FluentUiFilledAL.CARET_DOWN_24, foreground, (Integer)UIManager.get("TableHeader.height")/2));
					return;
				case UNSORTED:
					icon.setIcon(null);
					return;
				}
			}
		}
		icon.setIcon(null);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (base==null) {
			Color background=(Color)UIManager.get("TableHeader.background");
			Color foreground=(Color)UIManager.get("TableHeader.foreground");
//			Color borderBelow=(Color)UIManager.get("TableHeader.bottomSeparatorColor");
			Insets insets=(Insets)UIManager.get("TableHeader.cellMargins");
			
			
			FlatLabel label=new FlatLabel();
			label.setText(headerProperty.getName());
			label.setMinimumSize(new Dimension(10,(Integer)UIManager.get("TableHeader.height")));
			label.setPreferredSize(new Dimension(10,(Integer)UIManager.get("TableHeader.height")));
			label.setFont((Font)UIManager.get("TableHeader.font"));
			if (!gob.getPropertyDefinitions().contains(headerProperty)) {
				label.setFont(ImageUtilities.getFont(FontType.LightItalic, label.getFont().getSize2D()));
			}
			label.setForeground(foreground);
			label.setBorder(BorderFactory.createEmptyBorder(insets.top,insets.left,insets.bottom,insets.right));
			switch(headerProperty.getType()) {
			case Boolean:
				label.setHorizontalAlignment(FlatLabel.CENTER);
				break;
			case Float:
			case Integer:
				label.setHorizontalAlignment(FlatLabel.RIGHT);
				break;
			case ID:
			case String:
			case GOB:
				label.setHorizontalAlignment(FlatLabel.LEFT);
				break;
			}

			icon=new FlatLabel();
			
			base=new JPanel(new BorderLayout());
			base.setBackground(background);
			base.add(label,BorderLayout.CENTER);
			base.add(icon,BorderLayout.EAST);
		}
		updateSortIcon(column);
		return base;
	}

}
