package com.inkus.infomancerforge.settings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.SettingsBag;
import com.inkus.infomancerforge.editor.AdventureProjectModel;

public class SettingsModel {

	private Map<String,SettingsBag> settingsMap=new HashMap<>();
	private AdventureProjectModel adventureProjectModel;
	
	public SettingsModel(AdventureProjectModel adventureProjectModel) {
		this.adventureProjectModel=adventureProjectModel;
	}

	public void addSettings(String name,SettingsBag settings) {
		settingsMap.put(name, settings);
		settings.setValues(StorageUtilities.loadJSONMap(new File(adventureProjectModel.getProject().getPath()+"/.data/settings/"+name+".json")));
	}

	public Map<String,SettingsBag> getSettings() {
		return settingsMap;
	}
	
	public void saveSettings() {
		for (String name:settingsMap.keySet()) {
			Map<String,Object> map=settingsMap.get(name).getValues();
			StorageUtilities.saveJSONMap(new File(adventureProjectModel.getProject().getPath()+"/.data/settings/"+name+".json"),map);
		}
	}
	
}
