package com.cinch.adventurebuilderstoolkit.editor;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.TreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.beans.FileGameObject;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOB;
import com.cinch.adventurebuilderstoolkit.beans.sourcecode.SourceCode;
import com.cinch.adventurebuilderstoolkit.beans.views.View;
import com.cinch.adventurebuilderstoolkit.editor.gob.ProjectGobEditorFactory;
import com.cinch.adventurebuilderstoolkit.editor.gob.ProjectSourceCodeEditorFactory;
import com.cinch.adventurebuilderstoolkit.editor.gob.ProjectViewEditorFactory;
import com.cinch.adventurebuilderstoolkit.editor.swing.DockablePanel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectGobTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectSourceCodeTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectViewTreeNode;

public class ProjectItemEditorFactory {
	static private final Logger log=LogManager.getLogger(ProjectItemEditorFactory.class);

	private Map<Class<? extends TreeNode>,ProjectItemEditorFactoryInterface> factories=new HashMap<>();
	private Map<Class<? extends FileGameObject>,ProjectItemEditorFactoryInterface> fileGameObjectFactories=new HashMap<>();
	
	private AdventureProjectModel adventureProjectModel;
	
	public ProjectItemEditorFactory(AdventureProjectModel adventureProjectModel) {
		this.adventureProjectModel=adventureProjectModel;
		factories.put(ProjectGobTreeNode.class,new ProjectGobEditorFactory());
		factories.put(ProjectViewTreeNode.class,new ProjectViewEditorFactory());
		factories.put(ProjectSourceCodeTreeNode.class,new ProjectSourceCodeEditorFactory());
		
		fileGameObjectFactories.put(GOB.class,new ProjectGobEditorFactory());
		fileGameObjectFactories.put(View.class,new ProjectViewEditorFactory());
		fileGameObjectFactories.put(SourceCode.class,new ProjectSourceCodeEditorFactory());
	}

	public boolean canBuildEditor(TreeNode treeNode) {
		return factories.containsKey(treeNode.getClass());
	}

	public String getEditorKey(TreeNode treeNode) {
		if (factories.containsKey(treeNode.getClass())) {
			return factories.get(treeNode.getClass()).getEditorId(treeNode);
		}
		return null;
	}
	
	public String getEditorKey(FileGameObject fileGameObject) {
		if (fileGameObjectFactories.containsKey(fileGameObject.getClass())) {
			return fileGameObjectFactories.get(fileGameObject.getClass()).getEditorId(adventureProjectModel, fileGameObject);
		}
		return null;
	}
	
	public DockablePanel buildNewEditor(TreeNode treeNode) {
		DockablePanel dockablePanel=null;
		if (factories.containsKey(treeNode.getClass())) {
			dockablePanel=factories.get(treeNode.getClass()).buildNewEditor(treeNode);
			if (dockablePanel == null) {
				// TODO: Should we show a user error here.
				log.warn("Factory to create an editor for the tree node "+treeNode.getClass().getName()+ " could not build editor.");
			}
		} else {
			// TODO: Should we show a user error here.
			log.warn("No defined factory to to create an editor for the tree node "+treeNode.getClass().getName());
		}
		return dockablePanel;
	}
	
	public interface ProjectItemEditorFactoryInterface{
		String getEditorId(TreeNode treeNode);
		String getEditorId(AdventureProjectModel adventureProjectModel,FileGameObject treeNode);
		DockablePanel buildNewEditor(TreeNode treeNode);
		DockablePanel buildNewEditor(AdventureProjectModel adventureProjectModel,FileGameObject treeNode);
	}
	
}
