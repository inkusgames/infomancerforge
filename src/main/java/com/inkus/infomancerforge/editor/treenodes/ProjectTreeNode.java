package com.inkus.infomancerforge.editor.treenodes;

import java.io.File;

import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.AdventureProjectTreeModel;

public class ProjectTreeNode extends ProjectFileTreeNode {
	
	public ProjectTreeNode(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel, null, new File(adventureProjectModel.getProject().getPath()));
	}
	
	public String getName() {
		return adventureProjectModel.getProject().getName();
	}
	
	public void save(AdventureProjectTreeModel adventureProjectTreeModel) {
		StorageUtilities.saveProjectFile(adventureProjectModel.getProject());
		super.save(adventureProjectTreeModel);
	}
	
	public void findPathTo(FileGameObject fileGameObject) {
		
	}
}
