package com.cinch.adventurebuilderstoolkit.plugins;

import java.io.Serializable;

public class PluginType implements Serializable{
	private static final long serialVersionUID = 1L;

	private String name;
	
	public PluginType() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
