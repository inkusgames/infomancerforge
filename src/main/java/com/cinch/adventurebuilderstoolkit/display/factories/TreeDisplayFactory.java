package com.cinch.adventurebuilderstoolkit.display.factories;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

import com.cinch.adventurebuilderstoolkit.display.factories.builders.TreeDisplayBuilderFile;
import com.cinch.adventurebuilderstoolkit.display.factories.builders.TreeDisplayBuilderGOB;
import com.cinch.adventurebuilderstoolkit.display.factories.builders.TreeDisplayBuilderProject;
import com.cinch.adventurebuilderstoolkit.display.factories.builders.TreeDisplayBuilderSourceCode;
import com.cinch.adventurebuilderstoolkit.display.factories.builders.TreeDisplayBuilderView;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectGobTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectSourceCodeTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectViewTreeNode;

public class TreeDisplayFactory {

	private Map<Class<? extends TreeNode>, TreeDisplayBuilder<?>> builders=new HashMap<>();
	
	public TreeDisplayFactory(){
		builders.put(ProjectTreeNode.class,new TreeDisplayBuilderProject());
		builders.put(ProjectGobTreeNode.class,new TreeDisplayBuilderGOB());
		builders.put(ProjectViewTreeNode.class,new TreeDisplayBuilderView());
		builders.put(ProjectFileTreeNode.class,new TreeDisplayBuilderFile());
		builders.put(ProjectSourceCodeTreeNode.class,new TreeDisplayBuilderSourceCode());
	}

	public JPanel getListDisplay(TreeNode bean,boolean expanded,boolean leaf) {
		if (builders.containsKey(bean.getClass())) {
			boolean changed=false;
			boolean errors=false;
			int changedCount=0;
			if (bean instanceof ProjectFileTreeNode projectFileTreenode) {
				errors=projectFileTreenode.hasErrors();
				changed=projectFileTreenode.hasUnsavedChanges();
				changedCount=projectFileTreenode.countDataChanges();
			}
			
			return builders.get(bean.getClass()).getDisplayRaw(bean,expanded,leaf,changed,changedCount,errors);
		}
		return null;
	}

}
