package com.inkus.infomancerforge.editor.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ImageUtilities;

public class OnlineTermsAction extends AbstractAction {
	static private final Logger log = LogManager.getLogger(OnlineTermsAction.class);

	private static final long serialVersionUID = 1L;

	public OnlineTermsAction() {
		putValue(NAME, "Terms and Conditions");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DOCUMENT_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_T);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		    try {
				Desktop.getDesktop().browse(new URI("https://inkusgames.com/privacy-policy"));
			} catch (Exception e1) {
				log.error(e1.getMessage(),e1);
			}
		}
	}
}
