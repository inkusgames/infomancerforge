package com.inkus.infomancerforge.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsBag implements Serializable{
	private static final long serialVersionUID = 1L;

	private String name;
	private List<Setting> settings=new ArrayList<>();
	
	public SettingsBag() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}

	public void setSetting(String field,String value) {
		for (var s:settings) {
			if (s.getField().equals(field)) {
				System.out.println("Field "+field+"="+value);
				
				s.setValue(value);
				break;
			}
		}
	}

	public void setSetting(String field,Boolean value) {
		for (var s:settings) {
			if (s.getField().equals(field)) {
				System.out.println("Field "+field+"="+value);
				s.setValue(value);
				break;
			}
		}
	}

	public void setValues(Map<String,Object> values) {
		if (values!=null) {
			for (var setting:settings) {
				if (values.containsKey(setting.getField())) {
					setting.setValue(values.get(setting.getField()));
				} 
				if (setting.getValue()==null){
					setting.setValue(setting.getDefaultValue());
				}
			}
		}
	}

	public Map<String, Object> getValues() {
		Map<String,Object> value=new HashMap<>();
		for (var setting:settings) {
			if (setting.getField()!=null) {
				value.put(setting.getField(), setting.getValue());
			}
		}
		return value;
	}

}
