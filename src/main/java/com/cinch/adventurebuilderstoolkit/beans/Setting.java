package com.cinch.adventurebuilderstoolkit.beans;

import java.io.Serializable;

import org.luaj.vm2.LuaFunction;

public class Setting implements Serializable{
	private static final long serialVersionUID = 1L;

	public enum SettingType {
		Boolean,
		Choice,
		String,
		Function,
		Code
	};
	
	private String group;
	private String field;
	private String label;
	private String choices;
	private SettingType settingType;
	private String codeType;
	private Integer codeHeight=200;
	private Object defaultValue;
	private Object value;
	private LuaFunction action;
	
	public Setting() {
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public SettingType getSettingType() {
		return settingType;
	}

	public void setSettingType(SettingType settingType) {
		this.settingType = settingType;
	}
	
	private Object convertType(Object value) {
		switch (settingType) {
		case Boolean:
			if (value instanceof Boolean bool) {
				return bool;
			}
			return false;
		case Choice:
		case Code:
		case String:
		default:
			if (value instanceof String stringValue) {
				return stringValue;
			}
			return "";
		}
//		return null;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = convertType(defaultValue);
	}

	public Object getValue() {
		if (value==null) {
			return defaultValue;
		}
		return value;
	}

	public void setValue(Object value) {
		this.value = convertType(value);
	}

	public String getChoices() {
		return choices;
	}

	public void setChoices(String choices) {
		this.choices = choices;
	}

	public LuaFunction getAction() {
		return action;
	}

	public void setAction(LuaFunction action) {
		this.action = action;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public Integer getCodeHeight() {
		return codeHeight;
	}

	public void setCodeHeight(Integer codeHeight) {
		this.codeHeight = codeHeight;
	}
	
	
}
