package com.cinch.adventurebuilderstoolkit.editor.treenodes;

import java.io.File;

import javax.swing.tree.TreeNode;

import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;

public abstract class ProjectFileTreeNamedResourceNode extends ProjectFileTreeNode{

	public ProjectFileTreeNamedResourceNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
		super(adventureProjectModel, parent, file);
	}
	
	public abstract NamedResource getNamedResource();
}
