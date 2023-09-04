package com.inkus.infomancerforge.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;
import com.inkus.infomancerforge.editor.wizards.NewViewWizard;

public class CreateViewAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;

	
	public CreateViewAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Create View");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.WHITEBOARD_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_V);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK , true));
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
		NewViewWizard newViewWizard=new NewViewWizard(adventureProjectModel, (ProjectFileTreeNode)treeNode);
		newViewWizard.setLocationRelativeTo((Component)e.getSource());
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return true;
	}
}
