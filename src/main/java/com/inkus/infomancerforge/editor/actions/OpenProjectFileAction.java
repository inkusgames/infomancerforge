package com.inkus.infomancerforge.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.formdev.flatlaf.extras.components.FlatTree;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.ProjectControllerActionManager;

public class OpenProjectFileAction extends BaseAction implements TreeActionInterface {
	private static final long serialVersionUID = 1L;
	
	private FlatTree flatTree;
	private ProjectControllerActionManager projectControllerActionManager;

	public OpenProjectFileAction(FlatTree flatTree,AdventureProjectModel adventureProjectModel,ProjectControllerActionManager projectControllerActionManager) {
		super(adventureProjectModel);
		this.flatTree=flatTree;
		this.projectControllerActionManager=projectControllerActionManager;
		//this.work=work;
		putValue(NAME, "Open File...");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.OPEN_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		// TODO: Should we not perform this is enter is pressed on the tree. Perhaps that should be handled by the controller.
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// This will open the currently selected option(s) if it can
		TreePath[] paths = flatTree.getSelectionPaths();
		for (var path:paths) {
			executeOn((TreeNode)path.getLastPathComponent(),e);
		}
	}
	
	@Override
	public boolean canProcess(TreeNode treeNode) {
		return projectControllerActionManager.canOpen(treeNode);
	}

	@Override
	public void executeOn(TreeNode treeNode,ActionEvent e) {
		// Open treeNode supplied
		projectControllerActionManager.openOrShowEditor(treeNode);
	}
	
	public AbstractAction getAction() {
		return this;
	}

	public boolean onlySuitableForSingleSelections() {
		return false;
	}

}
