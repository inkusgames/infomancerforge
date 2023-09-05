package com.inkus.infomancerforge.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNamedResourceNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectGobTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectSourceCodeTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectViewTreeNode;

public class DeleteFileAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;

	
	public DeleteFileAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Delete File");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DELETE_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
	
	@Override
	public boolean canProcess(TreeNode treeNode) {
		if (treeNode instanceof ProjectFileTreeNamedResourceNode projectFileTreeNode) {
			return !projectFileTreeNode.isDirectory();
		}
		return false;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		if (treeNode instanceof ProjectFileTreeNamedResourceNode projectFileTreeNode) {
			if (JOptionPane.showConfirmDialog((Component)e.getSource(), "Are you sure you want to delete '"+projectFileTreeNode.getName()+"'. This can not be undone.", "Confirm Delete!", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
				if (projectFileTreeNode.getFile().delete()) {
					if (projectFileTreeNode instanceof ProjectGobTreeNode) {
						adventureProjectModel.getAnalyticsController().sendEvent("GOB", "Delete", null);
					}
					if (projectFileTreeNode instanceof ProjectViewTreeNode) {
						adventureProjectModel.getAnalyticsController().sendEvent("View", "Delete", null);
					}
					if (projectFileTreeNode instanceof ProjectSourceCodeTreeNode) {
						adventureProjectModel.getAnalyticsController().sendEvent("Script", "Delete", null);
					}
					
					adventureProjectModel.removeFileNode(projectFileTreeNode);
				} else {
					JOptionPane.showMessageDialog((Component)e.getSource(), "Unable to delete this file.", "Unable to delete file.", JOptionPane.WARNING_MESSAGE);
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
