package com.inkus.infomancerforge.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.inkus.infomancerforge.plugins.Plugin;

public class Project implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String[] authors;
	private transient String path; // this is not saves into the file it is the file path
	
	private int savedVersion;
	private Date created;
	private Date lastEdited;
	private Map<String,Plugin> plugins=new HashMap<>();
	private Map<String,String> resources=new HashMap<>();
		
	public Project() {
		super();
		created=new Date();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String[] getAuthors() {
		return authors;
	}
	
	public void setAuthors(String[] authors) {
		this.authors = authors;
	}
	
	public int getSavedVersion() {
		return savedVersion;
	}
	
	public void setSavedVersion(int savedVersion) {
		this.savedVersion = savedVersion;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getLastEdited() {
		return lastEdited;
	}
	
	public void setLastEdited(Date lastEdited) {
		this.lastEdited = lastEdited;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Map<String,Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(Map<String,Plugin> plugins) {
		this.plugins = plugins;
	}

	public Map<String, String> getResources() {
		return resources;
	}
	
	public String getResourcePathUUID(String uuid) {
		for (var k:resources.keySet()) {
			if (resources.get(k).equals(uuid)) {
				return k;
			}
		}
		return null;
	}

	public void setResources(Map<String, String> resources) {
		this.resources = resources;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(authors);
		result = prime * result
				+ Objects.hash(created, description, lastEdited, name, plugins, resources, savedVersion);
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
		Project other = (Project) obj;
		return Arrays.equals(authors, other.authors) && Objects.equals(created, other.created)
				&& Objects.equals(description, other.description) && Objects.equals(lastEdited, other.lastEdited)
				&& Objects.equals(name, other.name) && Objects.equals(plugins, other.plugins)
				&& Objects.equals(resources, other.resources) && savedVersion == other.savedVersion;
	}

}
