package com.cinch.adventurebuilderstoolkit.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.wizards.NewGobWizard;

public class CreateGobAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;
	
	private String gobType;

	public CreateGobAction(AdventureProjectModel adventureProjectModel,String gobType) {
		super(adventureProjectModel);
		this.gobType=gobType;
		putValue(NAME, "Create GOB");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.CLASS_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_G);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK , true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		return treeNode instanceof ProjectFileTreeNode && ((ProjectFileTreeNode)treeNode).isDirectory();
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		NewGobWizard newGobWizard=new NewGobWizard(adventureProjectModel, (ProjectFileTreeNode)treeNode, gobType);
		newGobWizard.setLocationRelativeTo((Component)e.getSource());
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return true;
	}
}
