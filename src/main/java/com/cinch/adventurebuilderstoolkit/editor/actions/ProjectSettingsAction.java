package com.cinch.adventurebuilderstoolkit.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectTreeNode;
import com.cinch.adventurebuilderstoolkit.settings.ProjectSettingsDialog;

public class ProjectSettingsAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;
	
	private JFrame owner;

	public ProjectSettingsAction(JFrame owner, AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		this.owner=owner;
		putValue(NAME, "Project Settings");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.SETTINGS_16, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new ProjectSettingsDialog(owner,adventureProjectModel); 
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		return treeNode instanceof ProjectTreeNode;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return true;
	}

}
