package com.inkus.infomancerforge.editor.gob;

import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.sourcecode.SourceCode;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.ProjectItemEditorFactory.ProjectItemEditorFactoryInterface;
import com.inkus.infomancerforge.editor.sourcecode.SourceCodeEditorLua;
import com.inkus.infomancerforge.editor.swing.DockablePanel;
import com.inkus.infomancerforge.editor.treenodes.ProjectSourceCodeTreeNode;

public class ProjectSourceCodeEditorFactory implements ProjectItemEditorFactoryInterface {
	
	public ProjectSourceCodeEditorFactory() {
	}

	@Override
	public DockablePanel buildNewEditor(TreeNode treeNode) {
		DockablePanel editor=null;
		
		if (treeNode instanceof ProjectSourceCodeTreeNode viewNode) {
			SourceCode sourceCode=viewNode.getSourceCode();
			editor=buildNewEditor(viewNode.getAdventureProjectModel(),sourceCode);
//			
//			AdventureProjectModel adventureProjectModel=viewNode.getAdventureProjectModel();
//			
//			if ("lua".equalsIgnoreCase(sourceCode.getExtension())) {
//				editor=new SourceCodeEditorLua(sourceCode, adventureProjectModel);
//			}
		}
		
		return editor;
	}

	@Override
	public DockablePanel buildNewEditor(AdventureProjectModel adventureProjectModel,FileGameObject fileGameObject) {
		SourceCode sourceCode=(SourceCode)fileGameObject;
		
		if ("lua".equalsIgnoreCase(sourceCode.getExtension())) {
			return new SourceCodeEditorLua(sourceCode, adventureProjectModel);
		}
		
		return null;
	}

	@Override
	public String getEditorId(TreeNode treeNode) {
		if (treeNode instanceof ProjectSourceCodeTreeNode viewNode) {
			SourceCode sourceCode=viewNode.getSourceCode();
			return sourceCode.getUuid();
		}
		return null;
	}

	@Override
	public String getEditorId(AdventureProjectModel adventureProjectModel,FileGameObject fileGameObject) {
		if (fileGameObject instanceof SourceCode sourceCode) {
			return sourceCode.getUuid();
		}
		return null;
	}
}