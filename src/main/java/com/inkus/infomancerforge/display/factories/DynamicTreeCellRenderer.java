package com.inkus.infomancerforge.display.factories;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

public class DynamicTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private static TreeDisplayFactory treeDisplayFactory=new TreeDisplayFactory();
	
	public DynamicTreeCellRenderer(){
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component baseComponent=super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
		Component component=treeDisplayFactory.getListDisplay((TreeNode) value, expanded, leaf);
		if (component!=null) {
			component.setBackground(baseComponent.getBackground());
		}else {
			component=baseComponent;
		}
		return component;
	}

}
