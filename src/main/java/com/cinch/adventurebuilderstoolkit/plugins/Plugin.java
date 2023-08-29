package com.cinch.adventurebuilderstoolkit.plugins;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Plugin implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private String filename;
	private String description;
	private String latestVersion;	
	private String[] dependancies;

	public Plugin() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getDependancies() {
		return dependancies;
	}

	public void setDependancies(String[] dependancies) {
		this.dependancies = dependancies;
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(dependancies);
		result = prime * result + Objects.hash(description, filename, latestVersion, name, type);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Plugin other = (Plugin) obj;
		return Arrays.equals(dependancies, other.dependancies) && Objects.equals(description, other.description)
				&& Objects.equals(filename, other.filename) && Objects.equals(latestVersion, other.latestVersion)
				&& Objects.equals(name, other.name) && Objects.equals(type, other.type);
	}
	
}
