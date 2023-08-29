package com.cinch.adventurebuilderstoolkit.editor.treenodes;

import java.io.File;

import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.FileGameObject;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectTreeModel;

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
