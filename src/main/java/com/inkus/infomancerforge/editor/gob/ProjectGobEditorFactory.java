package com.inkus.infomancerforge.editor.gob;

import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.ProjectItemEditorFactory.ProjectItemEditorFactoryInterface;
import com.inkus.infomancerforge.editor.swing.DockablePanel;
import com.inkus.infomancerforge.editor.treenodes.ProjectGobTreeNode;

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
