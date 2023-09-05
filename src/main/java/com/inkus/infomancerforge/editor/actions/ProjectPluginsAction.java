package com.inkus.infomancerforge.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.plugins.ProjectPluginsDialog;

public class ProjectPluginsAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;
	
	private JFrame owner;

	public ProjectPluginsAction(JFrame owner, AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		this.owner=owner;
		putValue(NAME, "Project Plugins");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.PLUG_DISCONNECTED_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_P);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (adventureProjectModel.hasChangesToSave()) {
			JOptionPane.showMessageDialog(owner, "You need to save the project before you manage plugins.","Save First",JOptionPane.INFORMATION_MESSAGE);
		} else {
			new ProjectPluginsDialog(owner,adventureProjectModel); 
		}
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		return true;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return false;
	}

}
