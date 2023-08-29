package com.cinch.adventurebuilderstoolkit.editor;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.ErrorUtilities;
import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.FileGameObject;
import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.cinch.adventurebuilderstoolkit.beans.Project;
import com.cinch.adventurebuilderstoolkit.beans.ProjectConfig;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOB;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBInstance;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBPropertyDefinition;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBPropertyDefinition.Type;
import com.cinch.adventurebuilderstoolkit.beans.sourcecode.SourceCode;
import com.cinch.adventurebuilderstoolkit.data.DataInstance;
import com.cinch.adventurebuilderstoolkit.data.DataModel;
import com.cinch.adventurebuilderstoolkit.data.hsql.DataModelHSQL;
import com.cinch.adventurebuilderstoolkit.editor.gob.GOBDataTableModel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNamedResourceNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectGobTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectSourceCodeTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectViewTreeNode;
import com.cinch.adventurebuilderstoolkit.lua.AdventureLuaEnviroment;
import com.cinch.adventurebuilderstoolkit.lua.LuaGobInstance;
import com.cinch.adventurebuilderstoolkit.plugins.PluginModel;
import com.cinch.adventurebuilderstoolkit.settings.SettingsModel;
import com.cinch.adventurebuilderstoolkit.utils.WeakList;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class AdventureProjectModel {
	static private final Logger log=LogManager.getLogger(AdventureProjectModel.class);
	
	private Project project;
	private ProjectConfig projectConfig;
	private DataModel dataModel;

	private AdventureProjectTreeModel adventureProjectTreeModel;
	private AdventureLuaEnviroment adventureLuaEnviroment;
	private AnalyticsController analyticsController;
	private SettingsModel settingsModel;
	private PluginModel pluginModel;
	
	private Map<Class<? extends NamedResource>,EventList<NamedResource>> namedResources=new HashMap<>();
	private Map<String,GOBDataTableModel> gobTableModels=new HashMap<>();

	private Map<File,ProjectFileTreeNode> fileNodesMap=null;

	private List<FileGameObjectChangeListener> fileGameObjectChangeListeners=new WeakList<>();
	private List<FileGameObjectDeletedListener> fileGameObjectDeletedListeners=new WeakList<>();	
	private List<DataInstanceChangeListener> dataInstanceChangeListeners=new WeakList<>();
	
	public AdventureProjectModel(Project project) {
		super();
		this.project = project;
		this.projectConfig=StorageUtilities.getProjectConfig(project);
		adventureProjectTreeModel=new AdventureProjectTreeModel(this);
		adventureLuaEnviroment=new AdventureLuaEnviroment(this);
		analyticsController=AnalyticsController.getAnalyticsController();
		settingsModel=new SettingsModel(this);
		pluginModel=new PluginModel(this);
		
		// For now we ONLY support HSQL this can be replaced latter with a project data factory when that changes
		try {
			dataModel=new DataModelHSQL(new File(project.getPath()+File.separator+".data/projectDb"),this);
		} catch (SQLException | ClassNotFoundException e) {
			ErrorUtilities.showFatalException(e);
		}
	}

	public Map<File,ProjectFileTreeNode> getFileNodesMap(){
		if (fileNodesMap==null) {
			fileNodesMap=new HashMap<>();
		}
		return fileNodesMap;
	}

	public AnalyticsController getAnalyticsController() {
		return analyticsController;
	}

	public SettingsModel getSettingsModel() {
		return settingsModel;
	}
	
	public PluginModel getPluginModel() {
		return pluginModel;
	}

	public AdventureProjectTreeModel getAdventureProjectTreeModel() {
		return adventureProjectTreeModel;
	}

	public void refreshFiles() {
		System.out.println("******************** REFRESH");
		ProjectTreeNode root=(ProjectTreeNode)adventureProjectTreeModel.getRoot();
		for (var list:namedResources.values()) {
			list.clear();
		}
		root.refresh();
		System.out.println("******************** DONE");
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO: Preserver open and closed nodes
				adventureProjectTreeModel.nodeStructureChanged(root);
			}
		});
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public DataModel getDataModel() {
		return dataModel;
	}

	// Make these listeners weak references and perform clean up
	public void addDataInstanceChangeListener(DataInstanceChangeListener dataInstanceChangeListener) {
		removeDataInstanceChangeListener(dataInstanceChangeListener);
		dataInstanceChangeListeners.add(dataInstanceChangeListener);
	}
	
	public void removeDataInstanceChangeListener(DataInstanceChangeListener dataInstanceChangeListener) {
		dataInstanceChangeListeners.remove(dataInstanceChangeListener);
	}
	
	public void fireDataInstanceChange(Object source,DataInstance dataInstance) {
		System.out.println("fireDataInstanceChange from "+source.getClass().getName());
		for (var l:dataInstanceChangeListeners) {
			l.dataInstanceObjectChanged(source,dataInstance);
		}
		// Is there a way to pass a change through the eventlist listeners?
		if (dataInstance instanceof NamedResource namedResource && namedResource.isNamed()) {
			@SuppressWarnings("unchecked")
			BasicEventList<NamedResource> eventList=(BasicEventList<NamedResource>)getNamedResourceModel(namedResource.getClass());
			int pos=eventList.indexOf(namedResource);
			if (pos!=-1) {
				eventList.set(pos,namedResource);
			}
		}
	}

	// Make these listeners weak references and perform clean up
	public void addFileGameObjectChangeListener(FileGameObjectChangeListener fileGameObjectChangeListener) {
		removeFileGameObjectChangeListener(fileGameObjectChangeListener);
		fileGameObjectChangeListeners.add(fileGameObjectChangeListener);
	}
	
	public void removeFileGameObjectChangeListener(FileGameObjectChangeListener fileGameObjectChangeListener) {
		fileGameObjectChangeListeners.remove(fileGameObjectChangeListener);
	}
	
	// Make these listeners weak references and perform clean up
	public void addFileGameObjectDeletedListener(FileGameObjectDeletedListener fileGameObjectDeletedListener) {
		removeFileGameObjectDeletedListener(fileGameObjectDeletedListener);
		fileGameObjectDeletedListeners.add(fileGameObjectDeletedListener);
	}
	
	public void removeFileGameObjectDeletedListener(FileGameObjectDeletedListener fileGameObjectDeletedListener) {
		fileGameObjectDeletedListeners.remove(fileGameObjectDeletedListener);
	}

	public void fireFileGameObjectChange(Object source,FileGameObject fileGameObject) {
		//new Exception("fireFileGameObjectChange from "+source.getClass().getName()).printStackTrace();;

		for (var l:new ArrayList<>(fileGameObjectChangeListeners)) {
			l.fileGameObjectChanged(source,fileGameObject);
		}
		// Is there a way to pass a change through the eventlist listeners?
		if (fileGameObject instanceof NamedResource namedResource) {
			@SuppressWarnings("unchecked")
			BasicEventList<NamedResource> eventList=(BasicEventList<NamedResource>)getNamedResourceModel(namedResource.getClass());
			int pos=eventList.indexOf(namedResource);
			if (pos!=-1) {
				eventList.set(pos,namedResource);
			}
		}
	}

	public void fireFileGameObjectDeleted(FileGameObject fileGameObject) {
		for (var l:new ArrayList<>(fileGameObjectDeletedListeners)) {
			l.fileGameObjectDeleted(fileGameObject);
		}
	}
	
	public AdventureProjectTreeModel getProjectTreeModel() {
		return adventureProjectTreeModel;
	}		
	
	public AdventureLuaEnviroment getAdventureLuaEnviroment() {
		return adventureLuaEnviroment;
	}
	
	public String embedLuaTokens(String source,GOBInstance gobInstance) {
		return embedLuaTokens(source,gobInstance,0);
	}
	
	private int findEndMarker(String source, int nextMarker) {
		int opens = 1;
		int endpos = nextMarker + 1;

		do {
			endpos = source.indexOf("}", endpos + 1);
			opens--;
			while (endpos != -1 && source.indexOf("${", nextMarker + 2) != -1
					&& source.indexOf("${", nextMarker + 2) < endpos) {
				opens++;
				nextMarker = source.indexOf("${", nextMarker + 2);
			}
		} while (endpos != -1 && opens > 0);

		return endpos;
	}

	public String embedLuaTokens(String source,GOBInstance gobInstance,int deapth) {
		LuaGobInstance luaGobInstance=null;
		if (gobInstance!=null) {
			luaGobInstance=new LuaGobInstance(this, gobInstance);
		}
		int nextMarker = source.indexOf("${");
		if (deapth < 50 && nextMarker != -1) {
			StringBuffer sb = new StringBuffer();
			Formatter formatter = new Formatter(sb, Locale.US);
			for (int pos = 0; pos < source.length();) {
				if (nextMarker == -1) {
					sb.append(source.substring(pos));
					pos = source.length();
				} else {
					int endpos = findEndMarker(source, nextMarker);
					if (endpos == -1) {
						sb.append(source.substring(pos));
						pos = source.length();
					} else {
						if (pos != nextMarker) {
							sb.append(source.substring(pos, nextMarker));
						}
						
						String lua = source.substring(nextMarker + 2, endpos);
						if (gobInstance!=null) {
							sb.append(adventureLuaEnviroment.evaluate(lua,luaGobInstance));
						} else {
							sb.append(adventureLuaEnviroment.evaluate(lua));
						}

						//						String key = source.substring(nextMarker + 2, endpos);
//						if (pos != nextMarker) {
//							sb.append(source.substring(pos, nextMarker));
//						}
//						if (key.indexOf("${") != -1) {
//							key = replaceWithModelStrings(key, deapth + 1, keys);
//						}
//						String format = null;
//						if (key.lastIndexOf(":") != -1) {
//							format = key.substring(key.lastIndexOf(":") + 1);
//							key = key.substring(0, key.lastIndexOf(":"));
//						}
//						if (keys != null) {
//							keys.add(key);
//						}
//						String value = null;
//						if (format != null) {
//							try {
//								Object o = getModelDataAsType(key);
//								if (o instanceof Boolean) {
//									formatter.format(format, ((Boolean) o).booleanValue());
//								} else if (o instanceof Double) {
//									formatter.format(format, ((Double) o).doubleValue());
//								} else if (o instanceof String) {
//									formatter.format(format, o);
//								}
//							} catch (Exception e) {
//								log.error("Unable to format '" + format + "' for model.'" + key + "'", e);
//								sb.append(e.getMessage());
//							}
//						} else {
//							value = getModelStringData(key);
//							if (value != null) {
//								sb.append(value);
//							} else {
//								sb.append("...");
//							}
//						}
						pos = endpos + 1;
					}
				}
				nextMarker = source.indexOf("${", pos);
			}
			formatter.close();
			return sb.toString();
		} else {
			return source;
		}
	}
	
	public boolean isParentOf(GOB parent,GOB child) {
		boolean isParent=false;
		if (child.getParent()!=null && child.getParent().length()>0) {
			if (child.getParent().equals(parent.getUuid())) {
				isParent=true;
			} else {
				GOB childsParent=getNamedResourceByUuid(GOB.class,child.getParent());
				if (childsParent!=null) {
					isParent=isParentOf(parent,childsParent);
				}
			}
		}
		return isParent;
	}
	
	public List<GOB> getGOBChildren(GOB gob){
		// TODO: Find a way to do this without the while loops it's n^n and that's just kak
		List<GOB> children=new ArrayList<>();
		
		EventList<GOB> allGobs = getNamedResourceModel(GOB.class);
		Set<String> parents = new HashSet<>();
		parents.add(gob.getUuid());
		boolean newFound=false;
		do {
			newFound=false;
			for (var g : allGobs) {
				// If we have not found this GOB and it's parent is one of the ones we have found already.
				if (!parents.contains(g.getUuid()) && parents.contains(g.getParent())) {
					parents.add(g.getUuid());
					children.add(g);
					newFound=true;
				}
			}
		} while (newFound==true);		
		return children;
	}
	
	public List<GOB> getGOBParents(GOB gob){
		// TODO: Find a way to do this without the while loops it's n^n and that's just kak
		List<GOB> parents=new ArrayList<>();
		for (GOB currentGob=getNamedResourceByUuid(GOB.class, gob.getParent());currentGob!=null;currentGob=getNamedResourceByUuid(GOB.class, currentGob.getParent())) {
			if (parents.contains(currentGob)) {
				break; // This is a recursive loop needs to be broken
			}
			parents.add(currentGob);
		}
		return parents;
	}

	// Find all Gobs that have a reference to this one.
	public List<GOB> getGOBReferances(GOB gob){
		// TODO: Find a way to do this without the while loops it's n^n and that's just kak
		List<GOB> gobs=new ArrayList<>();
		Set<String> types=new HashSet<>();
		types.add(gob.getUuid());
		for (GOB p:getGOBParents(gob)) {
			types.add(p.getUuid());
		}
//		
		EventList<GOB> allGobs = getNamedResourceModel(GOB.class);
		for (var g:allGobs) {
			for (var p:getAllGobProperties(g)) {
				if (p.getType()==Type.GOB && types.contains(p.getGobType())) {
					gobs.add(g);
					break;
				}
			}
		}
		return gobs;
	}

	public void markAllGOBChildrenAsTouched(Object source,GOB gob) {
		for (var child:getGOBChildren(gob)) {
			child.touch();
			fireFileGameObjectChange(source, child);
		}
	}


	public boolean hasChangesToSave() {
		// Calculate if there are changes to be saved here.
		return ((ProjectTreeNode)adventureProjectTreeModel.getRoot()).hasUnsavedChanges();
	}
	
	public void saveAll() {
		log.trace("Saving ALL");
		StorageUtilities.saveProjectConfig(project,projectConfig);
		
		ProjectTreeNode projectTreeNode=(ProjectTreeNode)adventureProjectTreeModel.getRoot();
		projectTreeNode.save(adventureProjectTreeModel);
		
		adventureProjectTreeModel.nodeChanged(projectTreeNode);
	}

	// This will return the script object that accompanies the provided fileGameObject if on exists in the project
	public SourceCode getResourceScriptFile(ProjectFileTreeNamedResourceNode fileGameObject) {
		if (fileGameObject instanceof ProjectGobTreeNode || fileGameObject instanceof ProjectViewTreeNode) {
			var all=fileGameObject.getParent().children();
			while (all.hasMoreElements()) {
				var tn=all.nextElement();
				if (tn instanceof ProjectSourceCodeTreeNode projectSourceCodeTreeNode) {
					SourceCode sourceCode=projectSourceCodeTreeNode.getSourceCode();
					if ("lua".equals(sourceCode.getExtension())) {
						String fileName=projectSourceCodeTreeNode.getFile().getName();
						if (fileName.substring(0, fileName.length()-4).equals(fileGameObject.getName())){
							return sourceCode;
						}
					}
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <type extends NamedResource> type getNamedResourceByUuid(Class<? extends NamedResource> forType,String uuid){
		if (uuid!=null && uuid.length()>0) {
			var l=getNamedResourceModel(forType);
			for (var n:l) {
				if (uuid.equals(n.getUuid())) {
					return (type) n;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <type extends NamedResource> type getNamedResourceByName(Class<? extends NamedResource> forType,String name){
		if (name!=null && name.length()>0) {
			var l=getNamedResourceModel(forType);
			for (var n:l) {
				if (name.equals(n.getName())) {
					return (type) n;
				}
			}
		}
		return null;
	}
	
	// TODO: This would be a lot better in a set of some sort
	public boolean isNamedResourceUsed(Class<? extends NamedResource> forType,String name){
		var l=getNamedResourceModel(forType);
		for (var n:l) {
			if (n.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public <type extends NamedResource> EventList<type> getNamedResourceModel(Class<type> forType){
		return getNamedResourceModel(forType,null);
	}
	
	@SuppressWarnings("unchecked")
	public <type extends NamedResource> EventList<type> getNamedResourceModel(Class<type> forType,String typeSet){
		// TODO: Some precautionary code is needed here for non named data instances.
		if (typeSet==null) {
			if (!namedResources.containsKey(forType)) {
				synchronized (namedResources) {
					if (!namedResources.containsKey(forType)) {
						namedResources.put(forType,new BasicEventList<NamedResource>());
					}				
				}
			}
			return (EventList<type>) namedResources.get(forType);
		} else {
			GOB forGob=getNamedResourceByUuid(GOB.class, typeSet);
			var allGobs=getGOBChildren(forGob);
			var list=new BasicEventList<GOBInstance>();
			allGobs.add(forGob);
			for (var g:allGobs) {
				var model=getGOBDataTableModel(g);
				list.addAll(model.getRows());
			}
			return (EventList<type>) list;
		}
	}
	
	public GOBDataTableModel getGOBDataTableModel(GOB gob) {
		if (!gobTableModels.containsKey(gob.getUuid())) {
			synchronized (gobTableModels) {
				if (!gobTableModels.containsKey(gob.getUuid())) {
					gobTableModels.put(gob.getUuid(),new GOBDataTableModel(this, gob));	
				}				
			}
		}
		return gobTableModels.get(gob.getUuid());
	}

	public void addFileNode(ProjectFileTreeNode parent,ProjectFileTreeNode child) {
		synchronized (this) {
			parent.addNode(child);
			if (child instanceof ProjectFileTreeNamedResourceNode namedResourceNode) {
				
				NamedResource namedResource=namedResourceNode.getNamedResource();
				@SuppressWarnings("unchecked")
				EventList<NamedResource> namedResourceModel=(EventList<NamedResource>) getNamedResourceModel(namedResource.getClass());
				namedResourceModel.add(namedResource);
			}
			adventureProjectTreeModel.nodesWereInserted(parent, new int[] {parent.getChildCount()-1});
		}
	}

	public void removeFileNode(ProjectFileTreeNode child) {
		removeFileNode(child, true);
		if (child instanceof ProjectFileTreeNamedResourceNode namedResourceNode) {
			NamedResource namedResource=namedResourceNode.getNamedResource();
			if (namedResource instanceof FileGameObject fileGameObject) {
				fireFileGameObjectDeleted(fileGameObject);
			}
		}
	}
	
	private void removeFileNode(ProjectFileTreeNode child, boolean updateTree) {
		if (child.getParent() instanceof ProjectFileTreeNode parent) {
			if (child.getChildCount()>0) {
				for (int t=child.getChildCount()-1;t>=0;t--) {
					if (child.getChildAt(t) instanceof ProjectFileTreeNode childFileNode) {
						removeFileNode(childFileNode,false);
					}
				}
			}
			
			
			int pos=parent.removeNode(child);
			if (child instanceof ProjectFileTreeNamedResourceNode namedResourceNode) {
				NamedResource namedResource=namedResourceNode.getNamedResource();
				@SuppressWarnings("unchecked")
				EventList<NamedResource> namedResourceModel=(EventList<NamedResource>) getNamedResourceModel(namedResource.getClass());
				namedResourceModel.remove(namedResource);
			}
			if (updateTree) {
				adventureProjectTreeModel.nodesWereRemoved(parent, new int[] {pos}, new Object[] {child});
			}
		}
	}
	
	public List<GOBPropertyDefinition> getAllGobProperties(GOB gob){
		List<GOBPropertyDefinition> properties=new ArrayList<>(gob.getPropertyDefinitions());
		List<String> checkForRecusion=new ArrayList<>();
		while (gob.getParent()!=null && gob.getParent().length()>0) {
			if (checkForRecusion.contains(gob.getParent())) {
				log.warn("Gob has recusion see parents list",checkForRecusion);
				break;
			}
			checkForRecusion.add(gob.getUuid());
			var gobs=getNamedResourceModel(GOB.class);
			GOB nextGob=null;
			for (var ng:gobs) {
				if (ng.getUuid().equals(gob.getParent())) {
					nextGob=ng;
					break;
				}
			}
			if (nextGob!=null) {
				gob=nextGob;
				properties.addAll(0, gob.getPropertyDefinitions());
			} else {
				log.warn("Unable to find parent GOB '"+gob.getParent()+"'");
			}
		}
		return properties;
	}


}
