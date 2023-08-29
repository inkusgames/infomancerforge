package com.cinch.adventurebuilderstoolkit.display.factories.builders;

import javax.swing.Icon;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectGobTreeNode;

public class TreeDisplayBuilderGOB extends TreeDisplayBuilderBase<ProjectGobTreeNode> {

	@Override
	public String getName(ProjectGobTreeNode node) {
		return node.getName();
	}

	@Override
	public Icon getIcon(ProjectGobTreeNode node,boolean expanded) {
		return ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_24, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
	}

}
