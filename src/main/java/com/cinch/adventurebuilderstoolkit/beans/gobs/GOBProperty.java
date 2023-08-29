package com.cinch.adventurebuilderstoolkit.beans.gobs;

import java.io.Serializable;

import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBPropertyDefinition.Type;

public class GOBProperty<valuetype> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private GOBPropertyDefinition definition;
	private valuetype value;
	
	public GOBProperty(GOBPropertyDefinition definition) {
		super();
		this.definition=definition;
	}

	public GOBProperty(GOBPropertyDefinition definition, valuetype value) {
		this.value = value;
		this.definition=definition;
	}

	public valuetype getValue() {
		return value;
	}

	public GOBPropertyDefinition getGOBPropertyDefinition() {
		return definition;
	}

	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		this.value = (valuetype)value;
	}

	public Type getType() {
		return definition.getType();
	}
}
