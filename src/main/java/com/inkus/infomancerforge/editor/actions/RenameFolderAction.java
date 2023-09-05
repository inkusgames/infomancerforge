package com.inkus.infomancerforge.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.FileUtils;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ErrorUtilities;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectTreeNode;

public class RenameFolderAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;

	
	public RenameFolderAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Rename Folder");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.RENAME_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		if (treeNode instanceof ProjectFileTreeNode projectFileTreeNode) {
			if (projectFileTreeNode instanceof ProjectTreeNode) {
				return false;
			}
			String path=projectFileTreeNode.getFile().getAbsolutePath().substring(adventureProjectModel.getProject().getPath().length());
			if (path.matches("[\\\\\\/]Plugins[\\\\\\/]Modules") || path.matches("[\\\\\\/]Plugins")) {
				return false;
			}
			return projectFileTreeNode.isDirectory();
		}
		return false;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		if (adventureProjectModel.hasChangesToSave()) {
			JOptionPane.showMessageDialog((Component)e.getSource(), "Your project has to be saved before you can rename a folder.", "Unable to rename folder.", JOptionPane.WARNING_MESSAGE);
		} else if (treeNode instanceof ProjectFileTreeNode projectFileTreeNode) {
			String newName=projectFileTreeNode.getName();
			
			do {
				newName=JOptionPane.showInputDialog((Component)e.getSource(), "New Name", newName);
				if (newName!=null) {
					if (newName.matches("[-_. A-Za-z0-9]+")) {
						if (newName.equals(projectFileTreeNode.getName())) {
							newName=null;
						} else {
							// Rename file
//							String ext=FilenameUtils.getExtension(projectFileTreeNode.getFile().getName());
							File oldFile=projectFileTreeNode.getFile();
							File destFile=new File(projectFileTreeNode.getFile().getParentFile().getAbsolutePath()+"/"+newName);
							if (destFile.exists()) {
								JOptionPane.showMessageDialog((Component)e.getSource(), "There is already a file matching the new name.", "Unable to rename file.", JOptionPane.WARNING_MESSAGE);
							} else {
								try {
									FileUtils.moveDirectory(oldFile, destFile);
									newName=null;
									// For each resource node we need to change it
									Set<String> removeSet=new HashSet<>();
									Map<String,String> reAddSet=new HashMap<>();
									String oldSourcePath=oldFile.getAbsolutePath().substring(adventureProjectModel.getProject().getPath().length());
									String newSourcePath=destFile.getAbsolutePath().substring(adventureProjectModel.getProject().getPath().length());
									
									for (String p:adventureProjectModel.getProject().getResources().keySet()) {
										if (p.startsWith(oldSourcePath)) {
											removeSet.add(p);
											reAddSet.put(newSourcePath+p.substring(oldSourcePath.length()), adventureProjectModel.getProject().getResources().get(p));
										}
									}
									for (String p:removeSet) {
										adventureProjectModel.getProject().getResources().remove(p);
									}								
									for (String p:reAddSet.keySet()) {
										adventureProjectModel.getProject().getResources().put(p,reAddSet.get(p));
									}				
									projectFileTreeNode.setFile(destFile);
									
									SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											adventureProjectModel.refreshFiles();
										}
									});
								} catch (IOException e1) {
									ErrorUtilities.showSeriousException(e1);
								}
								
//									if (projectFileTreeNode.renameFileResource(destFile)) {
//									projectFileTreeNode.setFile(destFile);
//									// Refresh tree
//									if (projectFileTreeNode.getNamedResource() instanceof FileGameObject fileGameObject) {
//										SwingUtilities.invokeLater(new Runnable() {
//											@Override
//											public void run() {
//												adventureProjectModel.getFileNodesMap().remove(oldFile.getAbsolutePath());
//												adventureProjectModel.getFileNodesMap().put(destFile.getAbsolutePath(),projectFileTreeNode);
//												adventureProjectModel.fireFileGameObjectChange(this, fileGameObject);
//												adventureProjectModel.refreshFiles();
//											}
//										});
//									}
//									newName=null;
//								} else {
//									JOptionPane.showMessageDialog((Component)e.getSource(), "There was a unexpected issue renaming the file.", "Unable to rename file.", JOptionPane.WARNING_MESSAGE);
//								}
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
