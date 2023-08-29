package com.cinch.adventurebuilderstoolkit.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.commons.io.FileUtils;

import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNode;
import com.cinch.adventurebuilderstoolkit.storage.DownLoader;
import com.cinch.adventurebuilderstoolkit.storage.DownLoader.DownloadProgressListener;
import com.cinch.adventurebuilderstoolkit.storage.DownLoader.DownloadState;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PluginModel {
	private static final Gson gson=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
	public static final String PLUGIN_URL_BASE="https://infomancerforgeplugins.inkusgames.com/";

	private PluginCataglogue pluginCataglogue;
	
	private PluginTableModel pluginTableModel=new PluginTableModel();
	
	private AdventureProjectModel adventureProjectModel;
	
	private Date loaded;
	
	private DownLoader<String> listDownloader=null;

	private Map<Plugin,String> pluginStatusMap=new HashMap<>();
	
	public PluginModel(AdventureProjectModel adventureProjectModel) {
		this.adventureProjectModel=adventureProjectModel;
	}

	public PluginTableModel getPluginTableModel() {
		return pluginTableModel;
	}
	
	public Date getLastLoaded() {
		return loaded;
	}
	
	public Plugin getPluginByName(String pluginName) {
		if (pluginCataglogue!=null && pluginCataglogue.getPlugin()!=null) {
			return pluginCataglogue.getPlugin().get(pluginName);
		}
		return null;
	}
	
	public void setStatus(Plugin plugin,String status) {
		pluginStatusMap.put(plugin, status);
		pluginTableModel.fireChange(plugin);
	}

	public boolean arePlugingFilesFound(Plugin plugin) {
		if ("Module".equals(plugin.getType())){
			ProjectFileTreeNode moduleNode=adventureProjectModel.getAdventureProjectTreeModel().findModuleTreeNode(false);
			if (moduleNode!=null) {
				File file=new File(moduleNode.getFile().getAbsolutePath()+"/"+plugin.getFilename());
				return file.exists();
			}
		} else {
			ProjectFileTreeNode pluginNode=adventureProjectModel.getAdventureProjectTreeModel().findPluginTreeNode(false);
			if (pluginNode!=null) {
				File file=new File(pluginNode.getFile().getAbsolutePath()+"/"+plugin.getName());
				return file.exists() && file.isDirectory();
			}
		}
		return false;
	}

	public void deletePlugingFiles(Plugin plugin) {
		if ("Module".equals(plugin.getType())){
			ProjectFileTreeNode moduleNode=adventureProjectModel.getAdventureProjectTreeModel().findModuleTreeNode(false);
			if (moduleNode!=null) {
				File file=new File(moduleNode.getFile().getAbsolutePath()+"/"+plugin.getFilename());
				if (file.exists()) {
					file.delete();
				}
			}
		} else {
			ProjectFileTreeNode pluginNode=adventureProjectModel.getAdventureProjectTreeModel().findPluginTreeNode(false);
			if (pluginNode!=null) {
				File file=new File(pluginNode.getFile().getAbsolutePath()+"/"+plugin.getName());
				if (file.exists() && file.isDirectory()) {
					try {
						FileUtils.deleteDirectory(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void load(DownloadProgressListener<String> progressListener) {
		if (listDownloader==null) {
			synchronized (this) {
				if (listDownloader==null) {
					listDownloader=DownLoader.getStringDownloader(PLUGIN_URL_BASE+"data.json", new DownloadProgressListener<String>() {
						
						@Override
						public void downloadDone(DownLoader<String> downLoader, String data) {
							pluginCataglogue=gson.fromJson(data, PluginCataglogue.class);
							pluginTableModel.fireChange();
							if (progressListener!=null) {
								progressListener.downloadDone(downLoader, data);
							}
						}
			
						@Override
						public void downloadStateChanged(DownLoader<String> downLoader, DownloadState downloadState) {
							if (downloadState.isCompleted()) {
								listDownloader=null;
							}
							if (progressListener!=null) {
								progressListener.downloadStateChanged(downLoader, downloadState);
							}
						}
					});
				}				
			}
		}		
	}

	private static final String[] tableColNames=new String[] {"Name","Type","Version","Description","Dependancies","Installed","Status"};
	
	class PluginTableModel implements TableModel {
		private List<TableModelListener> listeners=new ArrayList<>();

		private List<String> orderedNames=new ArrayList<>();
		
		public void fireChange() {
			orderedNames.clear();
			if (pluginCataglogue!=null && pluginCataglogue.getPlugin()!=null) {
				orderedNames.addAll(pluginCataglogue.getPlugin().keySet());
				orderedNames.sort(new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});
			}
			TableModelEvent e=new TableModelEvent(PluginTableModel.this);
			for (var l:listeners) {
				l.tableChanged(e);
			}
		}
		
		public void fireChange(Plugin plugin) {
			int row=orderedNames.indexOf(plugin.getName());
			TableModelEvent e=new TableModelEvent(PluginTableModel.this,row,row,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE);
			for (var l:listeners) {
				l.tableChanged(e);
			}
		}
		
		public Plugin getPluginForRow(int rowIndex) {
			return pluginCataglogue.getPlugin().get(orderedNames.get(rowIndex));
		}
		
		@Override
		public int getRowCount() {
			return orderedNames.size();
		}

		@Override
		public int getColumnCount() {
			return tableColNames.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return tableColNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0:
				return getPluginForRow(rowIndex).getName();
			case 1:
				return getPluginForRow(rowIndex).getType();
			case 2:
				return getPluginForRow(rowIndex).getLatestVersion();
			case 3:
				return getPluginForRow(rowIndex).getDescription();
			case 4:
				if (getPluginForRow(rowIndex).getDependancies()!=null){
					String[] d=getPluginForRow(rowIndex).getDependancies();
					Arrays.sort(d);
					return String.join(",", d);
				}
				break;
			case 5:
				if (adventureProjectModel.getProject().getPlugins()!=null && adventureProjectModel.getProject().getPlugins().get(orderedNames.get(rowIndex))!=null) {
					return adventureProjectModel.getProject().getPlugins().get(orderedNames.get(rowIndex)).getLatestVersion();
				}
				return "";
			case 6:
				Plugin rowPlugin=getPluginForRow(rowIndex);
				if (pluginStatusMap.containsKey(rowPlugin)) {
					return pluginStatusMap.get(rowPlugin);
				}
				Plugin versionPlugin=adventureProjectModel.getProject().getPlugins().get(orderedNames.get(rowIndex));
				if (versionPlugin!=null) {
					if ("?".equals(versionPlugin.getLatestVersion())) {
						return "Failed";
					} else if(versionPlugin.getLatestVersion()!=null) {
						// If this was deleted on the drive then flag it as Deleted
						if (arePlugingFilesFound(versionPlugin)) {
							return "Installed";
						} else {
							return "Missing";
						}
					}
				}
				break;
			}
			
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			listeners.remove(l);
			listeners.add(l);
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}
	}
	
}
