package com.inkus.infomancerforge.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.FileUtils;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ErrorUtilities;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;

public class DeleteFolderAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;
	
	public DeleteFolderAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Delete Folder");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DELETE_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		if (treeNode.getParent()!=null && treeNode instanceof ProjectFileTreeNode projectFileTreeNode) {
			return projectFileTreeNode.isDirectory();
		}
		return false;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		if (treeNode instanceof ProjectFileTreeNode projectFileTreeNode && projectFileTreeNode.isDirectory()) {
			if (JOptionPane.showConfirmDialog((Component)e.getSource(), "Are you sure you want to delete the folder '"+projectFileTreeNode.getName()+"'. This can not be undone.", "Confirm Folder Delete!", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
				try {
					FileUtils.deleteDirectory(projectFileTreeNode.getFile());
					adventureProjectModel.removeFileNode(projectFileTreeNode);
				} catch (IOException e1) {
					ErrorUtilities.showSeriousException(e1);
				}
			}
		}
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return false;
	}
}
