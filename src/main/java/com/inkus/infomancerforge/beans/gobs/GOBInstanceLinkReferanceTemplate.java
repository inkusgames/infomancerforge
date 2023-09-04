package com.inkus.infomancerforge.beans.gobs;

import java.io.Serializable;

public class GOBInstanceLinkReferanceTemplate implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String type;
	private String uuid;
	
	public GOBInstanceLinkReferanceTemplate() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
}
