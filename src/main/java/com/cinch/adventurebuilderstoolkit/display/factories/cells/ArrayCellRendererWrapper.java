package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.kordamp.ikonli.fluentui.FluentUiFilledAL;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;

public final class ArrayCellRendererWrapper extends GeneralCellRenderer {
	private GeneralCellRenderer wrappedRenderer;

	private JButton upButton;
	private JButton downButton;
	private JPanel panel;
	private JPanel centerPanel;
	 
	public ArrayCellRendererWrapper(GeneralCellRenderer wrappedRenderer) {
		super(wrappedRenderer.type);
		this.wrappedRenderer=wrappedRenderer;
		build();
	}

	private void build() {
		centerPanel=new JPanel(new BorderLayout());
		
		upButton=new JButton(new MoveItemUp());
		upButton.setFocusable(false);
		downButton=new JButton(new MoveItemDown());
		downButton.setFocusable(false);
		
		panel=new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc=new GridBagConstraints();
		
		gbc.weightx=0;
		gbc.weighty=100;
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.fill=GridBagConstraints.BOTH;

		panel.add(upButton,gbc);
		gbc.weightx=100;
		gbc.gridx++;
		panel.add(centerPanel,gbc);
		gbc.weightx=0;
		gbc.gridx++;
		panel.add(downButton,gbc);
	}

	@Override
	public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		centerPanel.removeAll();
		centerPanel.add(wrappedRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column),BorderLayout.CENTER);
		return panel;
	}

	class MoveItemUp extends AbstractAction {
		private static final long serialVersionUID = 1L;

		MoveItemUp(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiFilledAL.CARET_UP_12, ImageUtilities.BUTTON_ICON_DISABLED_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
		}
		
	}

	class MoveItemDown extends AbstractAction {
		private static final long serialVersionUID = 1L;

		MoveItemDown(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiFilledAL.CARET_DOWN_12, ImageUtilities.BUTTON_ICON_DISABLED_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
		}
		
	}
	
}
