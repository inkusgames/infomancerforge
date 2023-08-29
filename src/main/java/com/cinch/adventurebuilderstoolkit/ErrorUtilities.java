package com.cinch.adventurebuilderstoolkit;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.editor.AnalyticsController;

public class ErrorUtilities {
	static private final Logger log=LogManager.getLogger(ErrorUtilities.class);

	public static void showFatalException(Exception e) {
		JOptionPane.showMessageDialog(null, e, e.getMessage(),JOptionPane.ERROR_MESSAGE);
		log.fatal(e.getMessage(),e);
		AnalyticsController.getAnalyticsController().sendError(e);
		System.exit(-1);
	}
	
	public static void showSeriousException(Exception e) {
		JOptionPane.showMessageDialog(null, e, e.getMessage(),JOptionPane.ERROR_MESSAGE);
		AnalyticsController.getAnalyticsController().sendError(e);
		log.error(e.getMessage(),e);
	}
	
	public static void recordException(Exception e) {
		AnalyticsController.getAnalyticsController().sendError(e);
		log.error(e.getMessage(),e);
	}
}
