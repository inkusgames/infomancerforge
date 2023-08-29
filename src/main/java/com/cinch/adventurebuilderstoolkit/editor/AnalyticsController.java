package com.cinch.adventurebuilderstoolkit.editor;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.Config;

import ninja.egg82.analytics.GameAnalytics;
import ninja.egg82.analytics.common.ConnectionType;
import ninja.egg82.analytics.events.GADesign;
import ninja.egg82.analytics.events.GAError;
import ninja.egg82.analytics.events.GASessionEnd;
import ninja.egg82.analytics.events.GASessionStart;
import ninja.egg82.analytics.events.base.GAEventBase;

public class AnalyticsController {
	static private final Logger log=LogManager.getLogger(AnalyticsController.class);
	
	private GameAnalytics gameAnalytics;
	private GAEventBase base;
	
	private long startTime;
	
	private static AnalyticsController instance=null;
	private Config config;
	
	private AnalyticsController() {
		config=StorageUtilities.getConfig();
		startTime=System.currentTimeMillis();
		start();
	}
	
	private GameAnalytics getGameAnalytics() {
		if (gameAnalytics==null) {
			synchronized (this) {
				if (gameAnalytics==null) {
					try {
						System.out.println("Analytics on");
						gameAnalytics=new GameAnalytics("0ffdca17867a34a0df76ffbb58652aa1", "b0e081e84c7e80dd9c4cd53622ca685075d2193f", 1);
						base=GAEventBase.builder(gameAnalytics, config.getUserId(), config.getSessionNumber()).buildVersion(Config.version).connectionType(ConnectionType.UNKNOWN).limitAdTracking(true).build();
					} catch (InvalidKeyException | NoSuchAlgorithmException | IOException | ParseException e1) {
						log.error(e1.getMessage(),e1);
					}
				}		
			}
		}
		return config.getUseAnalytics()!=null && config.getUseAnalytics()?gameAnalytics:null;
	}

	private void start() {
		if (getGameAnalytics()!=null) {
			try {
				System.out.println("Analytics start");
				gameAnalytics.queueEvent(GASessionStart.builder(base).build());
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						end();
					}
				});
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
	
	public void sendError(Throwable t) {
		if (getGameAnalytics()!=null) {
			try {
				System.out.println("Analytics start");
				gameAnalytics.queueEvent(GAError.builder(base,t).build());
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						end();
					}
				});
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
	
	public void sendEvent(String group,String event,String details) {
		if (getGameAnalytics()!=null) {
			try {
				System.out.println("Analytics start");
				gameAnalytics.queueEvent(GADesign.builder(base,group).part2(event).part3(details).build());
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						end();
					}
				});
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}


	private void end() {
		if (getGameAnalytics()!=null) {
			try {
				System.out.println("Analytics end");
				gameAnalytics.queueEvent(GASessionEnd.builder(base,(System.currentTimeMillis()-startTime)/1000).build());
				gameAnalytics.close();
				gameAnalytics=null;
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
	
	public static AnalyticsController getAnalyticsController() {
		if (instance==null) {
			synchronized (AnalyticsController.class) {
				if (instance==null) {
					instance=new AnalyticsController();
				}				
			}
		}
		return instance;
	}
	
}
