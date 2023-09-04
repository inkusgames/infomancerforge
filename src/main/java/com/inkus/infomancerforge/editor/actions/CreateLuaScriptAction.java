package com.inkus.infomancerforge.editor.actions;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.ErrorUtilities;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNamedResourceNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectGobTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectSourceCodeTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectViewTreeNode;
import com.inkus.infomancerforge.editor.wizards.NewLuaScriptWizard;

public class CreateLuaScriptAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;

	
	public CreateLuaScriptAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Create Lua Script");
		putValue(SMALL_ICON, ImageUtilities.getSourceCodeIcon("lua", ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_L);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK , true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		if (treeNode instanceof ProjectFileTreeNode && ((ProjectFileTreeNode)treeNode).isDirectory()) {
			return true;
		}
		if (treeNode instanceof ProjectFileTreeNamedResourceNode projectFileTreeNode) {
			return (projectFileTreeNode instanceof ProjectGobTreeNode || projectFileTreeNode instanceof ProjectViewTreeNode) &&
				adventureProjectModel.getResourceScriptFile(projectFileTreeNode)==null;
		}
		
		return false;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		if (treeNode instanceof ProjectFileTreeNamedResourceNode projectFileTreeNode) {
			try {
				ProjectFileTreeNode fileLocation=(ProjectFileTreeNode)treeNode.getParent();
				String newFileName=projectFileTreeNode.getFile().getAbsolutePath();
				File newFile=new File(newFileName.substring(0,newFileName.lastIndexOf("."))+".lua");
				if (!newFile.createNewFile()) {
					JOptionPane.showMessageDialog((Component) e.getSource(), "Unable to make source file, system errror.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
				} else {
					
					// Add new node to model.
					ProjectSourceCodeTreeNode newTreeNode=new ProjectSourceCodeTreeNode(adventureProjectModel, fileLocation, newFile);
					
					// Add to model
					adventureProjectModel.addFileNode(fileLocation,newTreeNode);
					
					adventureProjectModel.getAnalyticsController().sendEvent("Script", "Create", null);
				}				
			} catch (HeadlessException|IOException ex) {
				ErrorUtilities.showSeriousException(ex);
			}
		} else {
			NewLuaScriptWizard newWizard=new NewLuaScriptWizard(adventureProjectModel, (ProjectFileTreeNode)treeNode);
			newWizard.setLocationRelativeTo((Component)e.getSource());
		}
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return true;
	}
}
