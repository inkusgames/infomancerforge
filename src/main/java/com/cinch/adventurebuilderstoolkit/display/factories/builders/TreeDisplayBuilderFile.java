package com.cinch.adventurebuilderstoolkit.display.factories.builders;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNode;

public class TreeDisplayBuilderFile extends TreeDisplayBuilderBase<ProjectFileTreeNode> {

	private static Map<String, Icon> fileIcons=null; 
	
	public TreeDisplayBuilderFile(){
		if (fileIcons==null) {
			synchronized(TreeDisplayBuilderFile.class) {
				if (fileIcons==null) {
					fileIcons=new HashMap<>();
					fileIcons.put("png",ImageUtilities.getIcon(FluentUiRegularAL.IMAGE_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					fileIcons.put("jpg",ImageUtilities.getIcon(FluentUiRegularAL.IMAGE_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					fileIcons.put("jpeg",ImageUtilities.getIcon(FluentUiRegularAL.IMAGE_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					fileIcons.put("gif",ImageUtilities.getIcon(FluentUiRegularAL.IMAGE_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					
					fileIcons.put("js",ImageUtilities.getIcon(FluentUiRegularAL.JAVASCRIPT_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					
					fileIcons.put("html",ImageUtilities.getIcon(FluentUiRegularAL.CODE_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					
					fileIcons.put("pdf",ImageUtilities.getIcon(FluentUiRegularAL.DOCUMENT_PDF_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));

					fileIcons.put("txt",ImageUtilities.getIcon(FluentUiRegularMZ.NOTEPAD_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					fileIcons.put("md",ImageUtilities.getIcon(FluentUiRegularMZ.NOTEPAD_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));

					fileIcons.put("zip",ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_ZIP_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					fileIcons.put("rar",ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_ZIP_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
					fileIcons.put("gz",ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_ZIP_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE));
				}
			}
		}
	}
	
	@Override
	public String getName(ProjectFileTreeNode node) {
		return node.getName();
	}

	@Override
	public Icon getIcon(ProjectFileTreeNode node,boolean expanded) {
		if (node.isDirectory()) {
			if (expanded) {
				return ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_OPEN_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
			} else {
				return ImageUtilities.getIcon(FluentUiRegularAL.FOLDER_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
			}
		} else {
			if (fileIcons.containsKey(node.fileType().toLowerCase())) {
				return fileIcons.get(node.fileType().toLowerCase());
			}
			return ImageUtilities.getIcon(FluentUiRegularAL.DOCUMENT_20, ImageUtilities.TREE_ICON_COLOR, ImageUtilities.TREE_ICON_SIZE);
		}
	}

}
