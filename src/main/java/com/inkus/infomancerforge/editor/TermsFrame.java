package com.inkus.infomancerforge.editor;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatCheckBox;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatLabel.LabelType;
import com.inkus.infomancerforge.AdventureBuildersToolkitLauncher;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.Config;
import com.formdev.flatlaf.extras.components.FlatScrollPane;

public class TermsFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Config config = null;
	
	private FlatCheckBox analyticsCheckBox;

	public TermsFrame() throws HeadlessException {
		super("Informancer Forge");
		config = StorageUtilities.getConfig();
		setSize(800, 800);
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setIconImages(ImageUtilities.getApplicationIcons());
		
		build();

		setVisible(true);
	}
	
	private JComponent createTerms() {
		JEditorPane textArea=new JEditorPane();
		textArea.setEditable(false);
		textArea.setContentType("text/html");
		textArea.setText("<html>\r\n"
				+ "    <head>\r\n"
				+ "      <meta charset='utf-8'>\r\n"
				+ "      <meta name='viewport' content='width=device-width'>\r\n"
				+ "      <title>Terms &amp; Conditions</title>\r\n"
				+ "      <style> body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; padding:1em; } </style>\r\n"
				+ "    </head>\r\n"
				+ "    <body>\r\n"
				+ "    <strong>Terms &amp; Conditions</strong> <p>\r\n"
				+ "By downloading or using the app, these terms will\r\n"
				+ "automatically apply to you – you should make sure therefore\r\n"
				+ "that you read them carefully before using the app. You’re not\r\n"
				+ "allowed to copy or modify the app, any part of the app, or\r\n"
				+ "our trademarks in any way. You’re not allowed to attempt to\r\n"
				+ "extract the source code of the app, and you also shouldn’t try\r\n"
				+ "to translate the app into other languages or make derivative\r\n"
				+ "versions. The app itself, and all the trademarks, copyright,\r\n"
				+ "database rights, and other intellectual property rights related\r\n"
				+ "to it, still belong to Inkus Games.\r\n"
				+ "</p> <p>\r\n"
				+ "Inkus Games is committed to ensuring that the app is\r\n"
				+ "as useful and efficient as possible. For that reason, we\r\n"
				+ "reserve the right to make changes to the app or to charge for\r\n"
				+ "its services, at any time and for any reason. We will never\r\n"
				+ "charge you for the app or its services without making it very\r\n"
				+ "clear to you exactly what you’re paying for.\r\n"
				+ "</p> <p>\r\n"
				+ "The Infomancer Forge app stores and processes personal data that\r\n"
				+ "you have provided to us, to provide our\r\n"
				+ "Service. It’s your responsibility to keep your phone and\r\n"
				+ "access to the app secure. We therefore recommend that you do\r\n"
				+ "not jailbreak or root your phone, which is the process of\r\n"
				+ "removing software restrictions and limitations imposed by the\r\n"
				+ "official operating system of your device. It could make your\r\n"
				+ "phone vulnerable to malware/viruses/malicious programs,\r\n"
				+ "compromise your phone’s security features and it could mean\r\n"
				+ "that the Infomancer Forge app won’t work properly or at all.\r\n"
				+ "</p> <div><p>\r\n"
				+ "The app does use third-party services that declare their\r\n"
				+ "Terms and Conditions.\r\n"
				+ "</p> <p>\r\n"
				+ "Link to Terms and Conditions of third-party service\r\n"
				+ "providers used by the app\r\n"
				+ "</p> <ul><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><li><a href=\"https://gameanalytics.com/terms\" target=\"_blank\" rel=\"noopener noreferrer\">GameAnalytics</a></li><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----></ul></div> <p>\r\n"
				+ "You should be aware that there are certain things that\r\n"
				+ "Inkus Games will not take responsibility for. Certain\r\n"
				+ "functions of the app will require the app to have an active\r\n"
				+ "internet connection. The connection can be Wi-Fi or provided\r\n"
				+ "by your mobile network provider, but Inkus Games\r\n"
				+ "cannot take responsibility for the app not working at full\r\n"
				+ "functionality if you don’t have access to Wi-Fi, and you don’t\r\n"
				+ "have any of your data allowance left.\r\n"
				+ "</p> <p></p> <p>\r\n"
				+ "If you’re using the app outside of an area with Wi-Fi, you\r\n"
				+ "should remember that the terms of the agreement with your\r\n"
				+ "mobile network provider will still apply. As a result, you may\r\n"
				+ "be charged by your mobile provider for the cost of data for\r\n"
				+ "the duration of the connection while accessing the app, or\r\n"
				+ "other third-party charges. In using the app, you’re accepting\r\n"
				+ "responsibility for any such charges, including roaming data\r\n"
				+ "charges if you use the app outside of your home territory\r\n"
				+ "(i.e. region or country) without turning off data roaming. If\r\n"
				+ "you are not the bill payer for the device on which you’re\r\n"
				+ "using the app, please be aware that we assume that you have\r\n"
				+ "received permission from the bill payer for using the app.\r\n"
				+ "</p> <p>\r\n"
				+ "Along the same lines, Inkus Games cannot always take\r\n"
				+ "responsibility for the way you use the app i.e. You need to\r\n"
				+ "make sure that your device stays charged – if it runs out of\r\n"
				+ "battery and you can’t turn it on to avail the Service,\r\n"
				+ "Inkus Games cannot accept responsibility.\r\n"
				+ "</p> <p>\r\n"
				+ "With respect to Inkus Games’s responsibility for your\r\n"
				+ "use of the app, when you’re using the app, it’s important to\r\n"
				+ "bear in mind that although we endeavor to ensure that it is\r\n"
				+ "updated and correct at all times, we do rely on third parties\r\n"
				+ "to provide information to us so that we can make it available\r\n"
				+ "to you. Inkus Games accepts no liability for any\r\n"
				+ "loss, direct or indirect, you experience as a result of\r\n"
				+ "relying wholly on this functionality of the app.\r\n"
				+ "</p> <p>\r\n"
				+ "At some point, we may wish to update the app. The app is\r\n"
				+ "currently available on  – the requirements for the\r\n"
				+ "system(and for any additional systems we\r\n"
				+ "decide to extend the availability of the app to) may change,\r\n"
				+ "and you’ll need to download the updates if you want to keep\r\n"
				+ "using the app. Inkus Games does not promise that it\r\n"
				+ "will always update the app so that it is relevant to you\r\n"
				+ "and/or works with the  version that you have\r\n"
				+ "installed on your device. However, you promise to always\r\n"
				+ "accept updates to the application when offered to you, We may\r\n"
				+ "also wish to stop providing the app, and may terminate use of\r\n"
				+ "it at any time without giving notice of termination to you.\r\n"
				+ "Unless we tell you otherwise, upon any termination, (a) the\r\n"
				+ "rights and licenses granted to you in these terms will end;\r\n"
				+ "(b) you must stop using the app, and (if needed) delete it\r\n"
				+ "from your device.\r\n"
				+ "</p> <p><strong>Changes to This Terms and Conditions</strong></p> <p>\r\n"
				+ "We may update our Terms and Conditions\r\n"
				+ "from time to time. Thus, you are advised to review this page\r\n"
				+ "periodically for any changes. We will\r\n"
				+ "notify you of any changes by posting the new Terms and\r\n"
				+ "Conditions on this page.\r\n"
				+ "</p> <p>\r\n"
				+ "These terms and conditions are effective as of 2023-08-26\r\n"
				+ "</p> <p><strong>Contact Us</strong></p> <p>\r\n"
				+ "If you have any questions or suggestions about our\r\n"
				+ "Terms and Conditions, do not hesitate to contact us\r\n"
				+ "at info@inkusgames.com.\r\n"
				+ "</p> <p>This Terms and Conditions page was generated by <a href=\"https://app-privacy-policy-generator.nisrulz.com/\" target=\"_blank\" rel=\"noopener noreferrer\">App Privacy Policy Generator</a></p>\r\n"
				+ "</body>\r\n"
				+ "</html>");
		textArea.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		            if(Desktop.isDesktopSupported()) {
		                try {
		                    Desktop.getDesktop().browse(e.getURL().toURI());
		                }
		                catch (IOException | URISyntaxException e1) {
		                    e1.printStackTrace();
		                }
		            }
		        }
		    }
		});
		FlatScrollPane flatScrollPane=new FlatScrollPane();
		flatScrollPane.setHorizontalScrollBarPolicy(FlatScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		flatScrollPane.getViewport().setView(textArea);
		
		FlatLabel heading=new FlatLabel();
		heading.setLabelType(LabelType.h1);
		heading.setText("Informance Forge T&C's");
		heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		heading.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(heading,BorderLayout.NORTH);
		panel.add(flatScrollPane,BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent createButtons() {
		analyticsCheckBox=new FlatCheckBox();
		analyticsCheckBox.setFocusable(false);
		analyticsCheckBox.setText("Share usage information online.");
		analyticsCheckBox.setSelected(true);
		analyticsCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		FlatButton accept=new FlatButton();
		accept.setAction(new AcceptAction());
		accept.setFocusable(false);
		FlatButton close=new FlatButton();
		close.setAction(new CloseAction());
		close.setFocusable(false);
		
		JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(close);
		buttons.add(accept);
		
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(analyticsCheckBox,BorderLayout.WEST);
		panel.add(buttons,BorderLayout.CENTER);
		
		return panel;
	}
	
	private void build() {
		JPanel panel=new JPanel(new BorderLayout());
		
		panel.add(createTerms(),BorderLayout.CENTER);
		panel.add(createButtons(),BorderLayout.SOUTH);
		
		setContentPane(panel);
	}
	
	public static void main(String[] args) {
		AdventureBuildersToolkitLauncher.configureLAF();
		new TermsFrame();
	}
	
	public class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private CloseAction() {
			putValue(NAME, "Disagree");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DISMISS_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	
	public class AcceptAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private AcceptAction() {
			putValue(NAME, "Accept and Agree");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.CHECKMARK_CIRCLE_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			config.setUseAnalytics(analyticsCheckBox.isSelected());
			config.setAcceptedTerms(true);
			StorageUtilities.saveConfig();
			setVisible(false);
			new AdventureBuildersToolkitLauncher();
		}
	} 
	
}
