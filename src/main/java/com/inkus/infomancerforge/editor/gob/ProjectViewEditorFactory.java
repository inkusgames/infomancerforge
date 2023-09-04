package com.inkus.infomancerforge.editor.gob;

import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.ProjectItemEditorFactory.ProjectItemEditorFactoryInterface;
import com.inkus.infomancerforge.editor.swing.DockablePanel;
import com.inkus.infomancerforge.editor.treenodes.ProjectViewTreeNode;

public class ProjectViewEditorFactory implements ProjectItemEditorFactoryInterface {
	
	public ProjectViewEditorFactory() {
	}

	@Override
	public DockablePanel buildNewEditor(TreeNode treeNode) {
		DockablePanel editor=null;
		
		if (treeNode instanceof ProjectViewTreeNode viewNode) {
			editor=buildNewEditor(viewNode.getAdventureProjectModel(), viewNode.getView()); 
		}
		
		return editor;
	}

	@Override
	public DockablePanel buildNewEditor(AdventureProjectModel adventureProjectModel, FileGameObject fileGameObject) {
		if (fileGameObject instanceof View view) {
			return new ViewEditor(view,adventureProjectModel); 
		}
		return null;
	}

	@Override
	public String getEditorId(TreeNode treeNode) {
		if (treeNode instanceof ProjectViewTreeNode viewNode) {
			View view=viewNode.getView();
			return view.getUuid();
		}
		return null;
	}

	@Override
	public String getEditorId(AdventureProjectModel adventureProjectModel, FileGameObject fileGameObject) {
		if (fileGameObject instanceof View view) {
			return view.getUuid();
		}
		return null;
	}
}
