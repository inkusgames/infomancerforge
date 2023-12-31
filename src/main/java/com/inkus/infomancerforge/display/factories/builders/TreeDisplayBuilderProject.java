package com.inkus.infomancerforge.display.factories.builders;

import javax.swing.Icon;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.treenodes.ProjectTreeNode;

public class TreeDisplayBuilderProject extends TreeDisplayBuilderBase<ProjectTreeNode> {

	@Override
	public String getName(ProjectTreeNode node) {
		return node.getName();
	}

	@Override
	public Icon getIcon(ProjectTreeNode node,boolean expanded) {
		if (expanded) {
			return ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_OPEN_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
		} else {
			return ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
		}
	}
}
