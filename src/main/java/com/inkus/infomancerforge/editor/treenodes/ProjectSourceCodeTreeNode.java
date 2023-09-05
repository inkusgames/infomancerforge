package com.inkus.infomancerforge.editor.treenodes;

import java.io.File;
import java.util.UUID;

import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.beans.sourcecode.SourceCode;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.AdventureProjectTreeModel;

public class ProjectSourceCodeTreeNode extends ProjectFileTreeNamedResourceNode {

	private SourceCode sourceCode=null;
	
	public ProjectSourceCodeTreeNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
		super(adventureProjectModel, parent, file);
		load();
	}
	
	public boolean renameFileResource(File tofile) {
		String oldProjectPath=getFile().getAbsolutePath().substring(adventureProjectModel.getProject().getPath().length());
		String newProjectPath=tofile.getAbsolutePath().substring(adventureProjectModel.getProject().getPath().length());
		adventureProjectModel.getProject().getResources().remove(oldProjectPath);
		adventureProjectModel.getProject().getResources().put(newProjectPath,sourceCode.getUuid());
		
		return sourceCode.renameFileResource(tofile);
	}
	
	public String getFileResourceName() {
		return sourceCode.getFileResourceName();
	}

	public boolean holdsFileGameObject(FileGameObject fileGameObject) {
		return fileGameObject==sourceCode;
	}
	
	public NamedResource getNamedResource() {
		return sourceCode;
	}
	
	private void load() {
		String projectPath=getFile().getAbsolutePath().substring(adventureProjectModel.getProject().getPath().length());
		String uuid=adventureProjectModel.getProject().getResources().get(projectPath);
		if (uuid==null || uuid.length()==0) {
			uuid=UUID.randomUUID().toString();
			adventureProjectModel.getProject().getResources().put(projectPath,uuid);
		}
		sourceCode=StorageUtilities.loadSourceCode(getFile().getAbsolutePath(),adventureProjectModel.getProject().getPath(),uuid);
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
