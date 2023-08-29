package com.cinch.adventurebuilderstoolkit.editor.gob;

import javax.swing.tree.TreeNode;

import com.cinch.adventurebuilderstoolkit.beans.FileGameObject;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOB;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.ProjectItemEditorFactory.ProjectItemEditorFactoryInterface;
import com.cinch.adventurebuilderstoolkit.editor.swing.DockablePanel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectGobTreeNode;

public class ProjectGobEditorFactory implements ProjectItemEditorFactoryInterface {
	
	public ProjectGobEditorFactory() {
	}

	@Override
	public DockablePanel buildNewEditor(TreeNode treeNode) {
		DockablePanel editor=null;
		
		if (treeNode instanceof ProjectGobTreeNode) {
			editor=buildNewEditor(((ProjectGobTreeNode)treeNode).getAdventureProjectModel(),((ProjectGobTreeNode)treeNode).getGob());
		}
		
		return editor;
	}

	@Override
	public DockablePanel buildNewEditor(AdventureProjectModel adventureProjectModel, FileGameObject treeNode) {
		return new GobEditor((GOB)treeNode,adventureProjectModel); 
	}
	
	@Override
	public String getEditorId(TreeNode treeNode) {
		if (treeNode instanceof ProjectGobTreeNode) {
			GOB gob=((ProjectGobTreeNode)treeNode).getGob();
			return gob.getUuid();
		}
		return null;
	}

	@Override
	public String getEditorId(AdventureProjectModel adventureProjectModel, FileGameObject treeNode) {
		return treeNode.getUuid();
	}
}
