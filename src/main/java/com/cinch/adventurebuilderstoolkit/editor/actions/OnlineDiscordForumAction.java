package com.cinch.adventurebuilderstoolkit.editor.actions;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;

public class OnlineDiscordForumAction extends AbstractAction {
	static private final Logger log = LogManager.getLogger(OnlineDiscordForumAction.class);

	private static final long serialVersionUID = 1L;
	
	private static final String discordIcon="<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0,0,256,256\" width=\"50px\" height=\"50px\" fill-rule=\"nonzero\" fill=\"#ffffff\"><g fill=\"#ffffff\" fill-rule=\"nonzero\" stroke=\"none\" stroke-width=\"1\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-miterlimit=\"10\" stroke-dasharray=\"\" stroke-dashoffset=\"0\" font-family=\"none\" font-weight=\"none\" font-size=\"none\" text-anchor=\"none\" style=\"mix-blend-mode: normal\"><g transform=\"scale(5.12,5.12)\"><path d=\"M42.298,11.65c-0.676,-1.021 -1.633,-1.802 -2.768,-2.256c-2.464,-0.988 -4.583,-1.648 -6.479,-2.02c-1.33,-0.26 -2.647,0.394 -3.28,1.626l-0.158,0.308c-1.404,-0.155 -2.895,-0.207 -4.593,-0.164c-1.741,-0.042 -3.237,0.009 -4.643,0.164l-0.157,-0.308c-0.633,-1.232 -1.952,-1.885 -3.279,-1.625c-1.896,0.371 -4.016,1.031 -6.479,2.02c-1.134,0.454 -2.091,1.234 -2.768,2.256c-4.721,7.131 -6.571,14.823 -5.655,23.517c0.032,0.305 0.202,0.578 0.461,0.741c3.632,2.29 6.775,3.858 9.891,4.936c1.303,0.455 2.748,-0.054 3.517,-1.229l1.371,-2.101c-1.092,-0.412 -2.158,-0.9 -3.18,-1.483c-0.479,-0.273 -0.646,-0.884 -0.373,-1.363c0.273,-0.481 0.884,-0.65 1.364,-0.373c3.041,1.734 6.479,2.651 9.942,2.651c3.463,0 6.901,-0.917 9.942,-2.651c0.479,-0.277 1.09,-0.108 1.364,0.373c0.273,0.479 0.106,1.09 -0.373,1.363c-1.056,0.603 -2.16,1.105 -3.291,1.524l1.411,2.102c0.581,0.865 1.54,1.357 2.528,1.357c0.322,0 0.647,-0.053 0.963,-0.161c3.125,-1.079 6.274,-2.649 9.914,-4.944c0.259,-0.163 0.429,-0.437 0.461,-0.741c0.918,-8.695 -0.932,-16.388 -5.653,-23.519zM18.608,28.983c-1.926,0 -3.511,-2.029 -3.511,-4.495c0,-2.466 1.585,-4.495 3.511,-4.495c1.926,0 3.511,2.029 3.511,4.495c0,2.466 -1.585,4.495 -3.511,4.495zM31.601,28.957c-1.908,0 -3.478,-2.041 -3.478,-4.522c0,-2.481 1.57,-4.522 3.478,-4.522c1.908,0 3.478,2.041 3.478,4.522c0,2.481 -1.57,4.522 -3.478,4.522z\"></path></g></g></svg>";

	public OnlineDiscordForumAction() {
		putValue(NAME, "Discord Forums");
		putValue(SMALL_ICON, ImageUtilities.getIconSVG(discordIcon, Color.white, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_D);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		    try {
				Desktop.getDesktop().browse(new URI("https://discord.com/channels/977481865562841178/1105843987937505300"));
			} catch (Exception e1) {
				log.error(e1.getMessage(),e1);
			}
		}
	}
}
