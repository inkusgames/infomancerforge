package com.inkus.infomancerforge.editor.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;

public class OnlineHelpAction extends AbstractAction {
	static private final Logger log = LogManager.getLogger(OnlineHelpAction.class);

	private static final long serialVersionUID = 1L;

	public OnlineHelpAction() {
		putValue(NAME, "Online Help");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.QUESTION_CIRCLE_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_H);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		    try {
				Desktop.getDesktop().browse(new URI("https://inkusgames.com/how-to-use-infomancer-forge"));
			} catch (Exception e1) {
				log.error(e1.getMessage(),e1);
			}
		}
	}
}
