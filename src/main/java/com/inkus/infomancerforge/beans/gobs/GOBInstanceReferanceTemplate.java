package com.inkus.infomancerforge.beans.gobs;

import java.io.Serializable;

public class GOBInstanceReferanceTemplate implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String type;
	private String name;
	
	public GOBInstanceReferanceTemplate() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
