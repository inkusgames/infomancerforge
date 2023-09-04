package com.inkus.infomancerforge.display.factories.builders;

import javax.swing.Icon;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.treenodes.ProjectGobTreeNode;

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
