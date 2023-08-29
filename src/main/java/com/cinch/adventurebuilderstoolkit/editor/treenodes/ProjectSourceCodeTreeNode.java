package com.cinch.adventurebuilderstoolkit.editor.treenodes;

import java.io.File;

import javax.swing.tree.TreeNode;

import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.FileGameObject;
import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.cinch.adventurebuilderstoolkit.beans.sourcecode.SourceCode;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectTreeModel;

public class ProjectSourceCodeTreeNode extends ProjectFileTreeNamedResourceNode {

	private SourceCode sourceCode=null;
	
	public ProjectSourceCodeTreeNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
		super(adventureProjectModel, parent, file);
		load();
	}

	public boolean holdsFileGameObject(FileGameObject fileGameObject) {
		return fileGameObject==sourceCode;
	}
	
	public NamedResource getNamedResource() {
		return sourceCode;
	}
	
	private void load() {
		sourceCode=StorageUtilities.loadSourceCode(getFile().getAbsolutePath(),adventureProjectModel.getProject().getPath());
	}
	
	public String getName() {
		return sourceCode.getName();
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public boolean hasErrors() {
		return sourceCode.getErrors()!=null && sourceCode.getErrors().size()>0;
	}

	public boolean hasUnsavedChanges() {
		return sourceCode.hasChanges()||countDataChanges()>0;
	}
	
	public int countDataChanges() {
		return 0;
	}

	public void save(AdventureProjectTreeModel adventureProjectTreeModel) {
		if (hasUnsavedChanges()) {
			StorageUtilities.saveSourceCode(sourceCode,getFile().getAbsolutePath());
			sourceCode.saved();
		}
	}
	
	public static class ProjectSourceCodeNodeFactory implements ProjectFileTreeNodeFactory {
		@Override
		public ProjectFileTreeNode createNewNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
			return new ProjectSourceCodeTreeNode(adventureProjectModel, parent, file);
		}
	}	
}
