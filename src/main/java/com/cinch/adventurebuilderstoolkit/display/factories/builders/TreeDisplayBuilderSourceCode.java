package com.cinch.adventurebuilderstoolkit.display.factories.builders;

import javax.swing.Icon;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectSourceCodeTreeNode;

public class TreeDisplayBuilderSourceCode extends TreeDisplayBuilderBase<ProjectSourceCodeTreeNode> {

	@Override
	public String getName(ProjectSourceCodeTreeNode node) {
		return node.getName();
	}

	@Override
	public Icon getIcon(ProjectSourceCodeTreeNode node,boolean expanded) {
		return ImageUtilities.getSourceCodeIcon("lua", ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
	}

}
