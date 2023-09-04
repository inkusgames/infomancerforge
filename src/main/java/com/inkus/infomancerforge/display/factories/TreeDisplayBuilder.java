package com.inkus.infomancerforge.display.factories;

import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

public abstract class TreeDisplayBuilder<type extends TreeNode> {

	@SuppressWarnings("unchecked")
	JPanel getDisplayRaw(TreeNode t,boolean expanded,boolean leaf,boolean changed, int changedCount, boolean errors) {
		return getDisplay((type)t,expanded,leaf,changed,changedCount,errors);
	}

	
	abstract protected JPanel getDisplay(type t,boolean expanded,boolean leaf,boolean changed, int changedCount, boolean errors);
}
