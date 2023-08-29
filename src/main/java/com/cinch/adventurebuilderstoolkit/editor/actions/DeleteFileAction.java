package com.cinch.adventurebuilderstoolkit.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectGobTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectSourceCodeTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectViewTreeNode;

public class DeleteFileAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;

	
	public DeleteFileAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Delete File");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DELETE_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
//		putValue(MNEMONIC_KEY, KeyEvent.VK_V);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK , true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		if (treeNode instanceof ProjectFileTreeNode projectFileTreeNode) {
			return !projectFileTreeNode.isDirectory();
		}
		return false;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		if (treeNode instanceof ProjectFileTreeNode projectFileTreeNode) {
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
		return true;
	}
}
