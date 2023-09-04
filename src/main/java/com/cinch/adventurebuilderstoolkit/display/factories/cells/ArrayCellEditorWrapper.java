package com.cinch.adventurebuilderstoolkit.display.factories.cells;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.kordamp.ikonli.fluentui.FluentUiFilledAL;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;

public final class ArrayCellEditorWrapper extends GeneralCellEditor {
	private GeneralCellEditor wrappedEditor;

	private JButton upButton;
	private JButton downButton;
	private JPanel panel;
	private JPanel centerPanel;
	
	public ArrayCellEditorWrapper(GeneralCellEditor wrappedEditor) {
		super(wrappedEditor.selectable, wrappedEditor.editable, wrappedEditor.type);
		this.wrappedEditor=wrappedEditor;
		build();
	}

	private void build() {
		centerPanel=new JPanel(new BorderLayout());
		
		panel=new JPanel(new BorderLayout());
		upButton=new JButton(new MoveItemUp());
		upButton.setFocusable(false);
		panel.add(upButton,BorderLayout.WEST);
		downButton=new JButton(new MoveItemDown());
		downButton.setFocusable(false);
		panel.add(downButton,BorderLayout.EAST);
		panel.add(centerPanel,BorderLayout.CENTER);
	}
	
	@Override
	public Object getCellEditorValue() {
		return wrappedEditor.getCellEditorValue();
	}

	@Override
	final public JComponent getTableCellEditorComponent(JTable table, Object value, boolean isSelected) {
		centerPanel.removeAll();
		centerPanel.add(wrappedEditor.getTableCellEditorComponent(table, value, isSelected),BorderLayout.CENTER);
		return panel;
	}

	class MoveItemUp extends AbstractAction {
		private static final long serialVersionUID = 1L;

		MoveItemUp(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiFilledAL.CARET_UP_12, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
		}
		
	}

	class MoveItemDown extends AbstractAction {
		private static final long serialVersionUID = 1L;

		MoveItemDown(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiFilledAL.CARET_DOWN_12, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
		}
		
	}
	
}
