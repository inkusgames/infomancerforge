package com.inkus.infomancerforge.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectTreeNode;

public class SaveProjectAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;

	public SaveProjectAction(AdventureProjectModel adventureProjectModel) {
		super(adventureProjectModel);
		putValue(NAME, "Save");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.SAVE_20, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adventureProjectModel.saveAll();
	}

	@Override
	public boolean canProcess(TreeNode treeNode) {
		return treeNode instanceof ProjectTreeNode;
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		adventureProjectModel.saveAll();
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return false;
	}

}
