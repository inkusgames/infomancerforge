package com.inkus.infomancerforge.display.factories.builders;

import javax.swing.Icon;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.treenodes.ProjectViewTreeNode;

public class TreeDisplayBuilderView extends TreeDisplayBuilderBase<ProjectViewTreeNode> {

	@Override
	public String getName(ProjectViewTreeNode node) {
		return node.getName();
	}

	@Override
	public Icon getIcon(ProjectViewTreeNode node,boolean expanded) {
		return ImageUtilities.getIcon(FluentUiRegularMZ.WHITEBOARD_24, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
	}

}
