package com.cinch.adventurebuilderstoolkit.beans.gobs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBPropertyDefinition.Type;
import com.cinch.adventurebuilderstoolkit.data.DataInstance;

public class GOBInstance implements DataInstance,NamedResource {
	@SuppressWarnings("unused")
	static private final Logger log=LogManager.getLogger(GOBInstance.class);
	private static final long serialVersionUID = 1L;

	private String gobType;
	
	private String uuid;
	private boolean changed=false;
	private boolean newInstance=true; // This has never been saved to the database.
	
	private Map<String,GOBProperty<?>> properties=new HashMap<>();
	
	public GOBInstance() {
		uuid=UUID.randomUUID().toString();
	}

	public String getGobType() {
		return gobType;
	}

	public void setGobType(String gobType) {
		this.gobType = gobType;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean hasChanges() {
		return changed;
	}

	@Override
	public void touch() {
		changed=true;
	}

	@Override
	public void saved() {
		changed=false;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public String getName() {
		for (var p:getProperties().values()) {
			if (p.getType()==Type.ID) {
				return (String)p.getValue();
			}
		}
		return null;
	}

	@Override
	public boolean isNamed() {
		for (var p:getProperties().values()) {
			if (p.getType()==Type.ID) {
				return true;
			}
		}
		return false;
	}

	public boolean isNewInstance() {
		return newInstance;
	}

	public void setNewInstance(boolean newInstance) {
		this.newInstance = newInstance;
	}
	
	public GOBProperty<?> getProperty(GOBPropertyDefinition gobPropertyDefinition){
		if (!properties.containsKey(gobPropertyDefinition.getGobFieldName())) {
			synchronized (properties) {
				if (!properties.containsKey(gobPropertyDefinition.getGobFieldName())) {
					properties.put(gobPropertyDefinition.getGobFieldName(), new GOBProperty<>(gobPropertyDefinition));
				}				
			}
		}
		return properties.get(gobPropertyDefinition.getGobFieldName());
	}

	public Map<String, GOBProperty<?>> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, GOBProperty<?>> properties) {
		this.properties = properties;
	}
	
	public boolean testSearch(String search) {
		search=search.toLowerCase();
		for (var p:properties.values()) {
			switch (p.getType()) {
			case ID:
			case String:
				String s=(String)(p.getValue());
				if (s!=null && s.toLowerCase().indexOf(search)!=-1) {
					return true;
				}
				break;
			case Boolean:
			case Float:
			case GOB:
			case Integer:
			default:
				break;
			}
		}
		return false;
	}
	
}
