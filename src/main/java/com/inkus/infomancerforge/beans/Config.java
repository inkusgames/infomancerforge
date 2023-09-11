package com.inkus.infomancerforge.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Config implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final String version="1.0.0.1-Beta";

	private List<String> knownProjects=new ArrayList<>();
	private String lastWorkingPath;
	private Boolean useAnalytics=null;
	private Boolean acceptedTerms=false;
	private UUID userId=UUID.randomUUID();
	private long sessionNumber=1;

	public List<String> getKnownProjects() {
		if (knownProjects==null) {
			knownProjects=new ArrayList<>();
		}
		return knownProjects;
	}

	public void setKnownProjects(List<String> knownProjects) {
		this.knownProjects = knownProjects;
	}

	public String getLastWorkingPath() {
		return lastWorkingPath;
	}

	public void setLastWorkingPath(String lastWorkingPath) {
		this.lastWorkingPath = lastWorkingPath;
	}

	public Boolean getUseAnalytics() {
		return useAnalytics;
	}

	public void setUseAnalytics(Boolean useAnalytics) {
		this.useAnalytics = useAnalytics;
	}

	public Boolean getAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(Boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}

	public static String getVersion() {
		return version;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public long getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(long sessionNumber) {
		this.sessionNumber = sessionNumber;
	}
	
}
