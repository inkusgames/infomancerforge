package com.cinch.adventurebuilderstoolkit.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.TreeNode;

public interface TreeActionInterface {
	boolean canProcess(TreeNode treeNode);
	void executeOn(TreeNode treeNode,ActionEvent e);
	AbstractAction getAction();
	boolean onlySuitableForSingleSelections();
}
