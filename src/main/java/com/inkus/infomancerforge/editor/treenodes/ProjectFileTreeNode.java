package com.inkus.infomancerforge.editor.treenodes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.tree.TreeNode;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.AdventureProjectTreeModel;

import ca.odell.glazedlists.EventList;

public class ProjectFileTreeNode implements TreeNode{
	static private final Logger log=LogManager.getLogger(ProjectFileTreeNode.class);

	protected AdventureProjectModel adventureProjectModel;
	private File file;
	private TreeNode parent;
	private List<ProjectFileTreeNode> files=new ArrayList<ProjectFileTreeNode>();
	
	private static Map<File,Long> lastModifiedMap=new HashMap<>();
	
	private static Map<String, ProjectFileTreeNodeFactory> filetypeNodeFactoryMap=new HashMap<>();
	
	static {
		filetypeNodeFactoryMap.put("gob",new ProjectGobTreeNode.ProjectGobFileTreeNodeFactory());
		filetypeNodeFactoryMap.put("view",new ProjectViewTreeNode.ProjectViewFileTreeNodeFactory());
		
		filetypeNodeFactoryMap.put("lua",new ProjectSourceCodeTreeNode.ProjectSourceCodeNodeFactory());
		filetypeNodeFactoryMap.put("c#",new ProjectSourceCodeTreeNode.ProjectSourceCodeNodeFactory());
		filetypeNodeFactoryMap.put("java",new ProjectSourceCodeTreeNode.ProjectSourceCodeNodeFactory());
		filetypeNodeFactoryMap.put("c",new ProjectSourceCodeTreeNode.ProjectSourceCodeNodeFactory());
		filetypeNodeFactoryMap.put("cpp",new ProjectSourceCodeTreeNode.ProjectSourceCodeNodeFactory());
		filetypeNodeFactoryMap.put("h",new ProjectSourceCodeTreeNode.ProjectSourceCodeNodeFactory());
		filetypeNodeFactoryMap.put("hpp",new ProjectSourceCodeTreeNode.ProjectSourceCodeNodeFactory());
	}
	
	public ProjectFileTreeNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
		this.adventureProjectModel=adventureProjectModel;
		this.parent=parent;
		this.file=file;
		if (!lastModifiedMap.containsKey(file)) {
			updateFileChangedDate();
		}
		refresh();
	}
	
	public boolean wasFileChanged() {
		return lastModifiedMap.get(file)!=file.lastModified();
	}
	
	static public boolean wasFileChanged(File file) {
		return lastModifiedMap.get(file)!=file.lastModified();
	}
	
	public void updateFileChangedDate() {
		lastModifiedMap.put(file, file.lastModified());
	}

	static public void updateFileChangedDate(File file) {
		lastModifiedMap.put(file, file.lastModified());
	}
	
	public void refresh() {
		if (file.isDirectory()) {
			files.clear();
			for (File f:file.listFiles()) {
				// Exclude files starting with a .
				if (!f.getName().startsWith(".")) {
					String type=FilenameUtils.getExtension(f.getAbsolutePath()).toLowerCase();
					ProjectFileTreeNode newNode=adventureProjectModel.getFileNodesMap().get(f);
					if (newNode==null) {
						if (filetypeNodeFactoryMap.containsKey(type)) {
							newNode=filetypeNodeFactoryMap.get(type).createNewNode(adventureProjectModel, this,f);
						} else {
							newNode=new ProjectFileTreeNode(adventureProjectModel, this,f);
						}
						adventureProjectModel.getFileNodesMap().put(f,newNode);
					} else {
						newNode.refresh();
					}
					if (newNode instanceof ProjectFileTreeNamedResourceNode resourceNode) {
						@SuppressWarnings("unchecked")
						EventList<NamedResource> list=(EventList<NamedResource>)adventureProjectModel.getNamedResourceModel(resourceNode.getNamedResource().getClass());
						if (!list.contains(resourceNode.getNamedResource())) {
							System.out.println("Add NN:"+resourceNode.getNamedResource().getName());
							list.add(resourceNode.getNamedResource());
						}
					}
					files.add(newNode);
				}
			}
		}
	}
	
	public AdventureProjectModel getAdventureProjectModel() {
		return adventureProjectModel;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public String getName() {
		return file.getName();
	}
	
	public boolean isDirectory() {
		return file.isDirectory();
	}
	
	public String fileType() {
		return FilenameUtils.getExtension(file.getAbsolutePath());
	}
	
	public boolean holdsFileGameObject(FileGameObject fileGameObject) {
		return false;
	}
	
	@Override
	public TreeNode getChildAt(int childIndex) {
		return files.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return files.size();
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		return files.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() {
		return file.isDirectory();
	}

	@Override
	public boolean isLeaf() {
		return files.size()==0;
	}

	@Override
	public Enumeration<? extends TreeNode> children() {
		return Collections.enumeration(files);
	}
	
	public void save(AdventureProjectTreeModel adventureProjectTreeModel) {
		if (isDirectory() || getClass()==ProjectFileTreeNode.class) {
			for (var node:files) {
				node.save(adventureProjectTreeModel);
				lastModifiedMap.put(file,file.lastModified());
			}
		} else {
			log.warn("Save missing for type "+getClass().getName());
		}
	}
	
	public boolean hasUnsavedChanges() {
		for (ProjectFileTreeNode f:files) {
			if (f.hasUnsavedChanges()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasErrors() {
		for (ProjectFileTreeNode f:files) {
			if (f.hasErrors()) {
				return true;
			}
		}
		return false;
	}
	
	public int countDataChanges() {
		return 0;
	}
	
	public void addNode(ProjectFileTreeNode projectFileTreeNode) {
		files.add(projectFileTreeNode);
	}
	
	public int removeNode(ProjectFileTreeNode projectFileTreeNode) {
		int pos=files.indexOf(projectFileTreeNode);
		files.remove(projectFileTreeNode);
		return pos;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(file);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectFileTreeNode other = (ProjectFileTreeNode) obj;
		return Objects.equals(file, other.file);
	}

	interface ProjectFileTreeNodeFactory {
		ProjectFileTreeNode createNewNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file);
	}
}
