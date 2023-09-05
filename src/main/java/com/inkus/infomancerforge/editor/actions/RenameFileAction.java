package com.inkus.infomancerforge.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.FilenameUtils;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNamedResourceNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;

public class RenameFileAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;

	
	public RenameFileAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Rename File");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.RENAME_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
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
		if (adventureProjectModel.hasChangesToSave()) {
			JOptionPane.showMessageDialog((Component)e.getSource(), "Your project has to be saved before you can rename files.", "Unable to rename file.", JOptionPane.WARNING_MESSAGE);
		} else if (treeNode instanceof ProjectFileTreeNamedResourceNode projectFileTreeNode) {
			String newName=projectFileTreeNode.getFileResourceName();
			
			do {
				newName=JOptionPane.showInputDialog((Component)e.getSource(), "New Name", newName);
				if (newName!=null) {
					if (newName.matches("[-_. A-Za-z0-9]+")) {
						if (newName.equals(projectFileTreeNode.getName())) {
							newName=null;
						} else {
							// Rename file
							String ext=FilenameUtils.getExtension(projectFileTreeNode.getFile().getName());
							File oldFile=projectFileTreeNode.getFile();
							File destFile=new File(projectFileTreeNode.getFile().getParentFile().getAbsolutePath()+"/"+newName+"."+ext);
							if (destFile.exists()) {
								JOptionPane.showMessageDialog((Component)e.getSource(), "There is already a file matching the new name.", "Unable to rename file.", JOptionPane.WARNING_MESSAGE);
							} else {
								if (projectFileTreeNode.renameFileResource(destFile)) {
									projectFileTreeNode.setFile(destFile);
									// Refresh tree
									if (projectFileTreeNode.getNamedResource() instanceof FileGameObject fileGameObject) {
										SwingUtilities.invokeLater(new Runnable() {
											@Override
											public void run() {
												adventureProjectModel.getFileNodesMap().remove(oldFile.getAbsolutePath());
												adventureProjectModel.getFileNodesMap().put(destFile.getAbsolutePath(),projectFileTreeNode);
												adventureProjectModel.fireFileGameObjectChange(this, fileGameObject);
												adventureProjectModel.refreshFiles();
											}
										});
									}
									newName=null;
								} else {
									JOptionPane.showMessageDialog((Component)e.getSource(), "There was a unexpected issue renaming the file.", "Unable to rename file.", JOptionPane.WARNING_MESSAGE);
								}
							}
							
//							projectFileTreeNode.getFile().renameTo(null)
							// Update filetree
							// Update any views that had the old file
						}
					} else {
						JOptionPane.showMessageDialog((Component)e.getSource(), "Please enter a valid filename.", "Unable to rename file.", JOptionPane.WARNING_MESSAGE);
					}
				}
				
			} while (newName!=null);
//			String newName=JOptionPane.showInputDialog((Component)e.getSource(), "Enter the new name for "+projectFileTreeNode.getName() , "New Name", JOptionPane.OK_CANCEL_OPTION);
//			if (JOptionPane.showConfirmDialog((Component)e.getSource(), "Are you sure you want to delete '"+projectFileTreeNode.getName()+"'. This can not be undone.", "Confirm Delete!", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
//				if (projectFileTreeNode.getFile().delete()) {
//					if (projectFileTreeNode instanceof ProjectGobTreeNode) {
//						adventureProjectModel.getAnalyticsController().sendEvent("GOB", "Delete", null);
//					}
//					if (projectFileTreeNode instanceof ProjectViewTreeNode) {
//						adventureProjectModel.getAnalyticsController().sendEvent("View", "Delete", null);
//					}
//					if (projectFileTreeNode instanceof ProjectSourceCodeTreeNode) {
//						adventureProjectModel.getAnalyticsController().sendEvent("Script", "Delete", null);
//					}
//					
//					adventureProjectModel.removeFileNode(projectFileTreeNode);
//				} else {
//					JOptionPane.showMessageDialog((Component)e.getSource(), "Unable to delete this file.", "Unable to delete file.", JOptionPane.WARNING_MESSAGE);
//				}
//			}
		}
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return true;
	}
}
