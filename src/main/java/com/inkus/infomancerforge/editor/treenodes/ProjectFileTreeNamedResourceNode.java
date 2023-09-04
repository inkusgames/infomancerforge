package com.inkus.infomancerforge.editor.treenodes;

import java.io.File;

import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.editor.AdventureProjectModel;

public abstract class ProjectFileTreeNamedResourceNode extends ProjectFileTreeNode{

	public ProjectFileTreeNamedResourceNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
		super(adventureProjectModel, parent, file);
	}
	
	public abstract NamedResource getNamedResource();
}
