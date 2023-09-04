package com.inkus.infomancerforge.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;

public class ExitAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	public ExitAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Exit");
		putValue(MNEMONIC_KEY, KeyEvent.VK_X);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK, false));
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DISMISS_20, ImageUtilities.MENU_ICON_COLOR,
				ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (adventureProjectModel.hasChangesToSave()) {
			int result = JOptionPane.showConfirmDialog((Component) e.getSource(),
					"There are unsaved changes would you like to save before you exit.", "Save Before Exiting?",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				adventureProjectModel.saveAll();
			}
			if (result == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		System.exit(0);
	}

}
