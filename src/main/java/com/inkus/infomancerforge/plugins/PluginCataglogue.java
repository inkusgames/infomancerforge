package com.inkus.infomancerforge.plugins;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PluginCataglogue  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Map<String,Plugin> plugin=new HashMap<>();
	private Map<String,PluginType> pluginType=new HashMap<>();
	
	public PluginCataglogue() {
	}

	public Map<String, Plugin> getPlugin() {
		return plugin;
	}

	public void setPlugin(Map<String, Plugin> plugin) {
		this.plugin = plugin;
	}

	public Map<String, PluginType> getPluginType() {
		return pluginType;
	}

	public void setPluginType(Map<String, PluginType> pluginType) {
		this.pluginType = pluginType;
	}

}
