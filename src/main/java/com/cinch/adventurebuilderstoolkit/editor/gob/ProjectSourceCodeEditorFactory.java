package com.cinch.adventurebuilderstoolkit.editor.gob;

import javax.swing.tree.TreeNode;

import com.cinch.adventurebuilderstoolkit.beans.FileGameObject;
import com.cinch.adventurebuilderstoolkit.beans.sourcecode.SourceCode;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.ProjectItemEditorFactory.ProjectItemEditorFactoryInterface;
import com.cinch.adventurebuilderstoolkit.editor.sourcecode.SourceCodeEditorLua;
import com.cinch.adventurebuilderstoolkit.editor.swing.DockablePanel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectSourceCodeTreeNode;

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