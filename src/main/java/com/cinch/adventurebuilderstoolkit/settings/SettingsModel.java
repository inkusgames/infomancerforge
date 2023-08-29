package com.cinch.adventurebuilderstoolkit.settings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.SettingsBag;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;

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
