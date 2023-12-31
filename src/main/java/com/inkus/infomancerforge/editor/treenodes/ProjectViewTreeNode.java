package com.inkus.infomancerforge.editor.treenodes;

import java.io.File;

import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.AdventureProjectTreeModel;

public class ProjectViewTreeNode extends ProjectFileTreeNamedResourceNode {

	private View view=null;
	
	public ProjectViewTreeNode(AdventureProjectModel adventureProjectModel, TreeNode parent, View view) {
		super(adventureProjectModel, parent, view.getMyFile());
		this.view=view;
	}
	
	public ProjectViewTreeNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
		super(adventureProjectModel, parent, file);
		load();
	}
	
	public boolean renameFileResource(File tofile) {
		return view.renameFileResource(tofile);
	}
	
	public String getFileResourceName() {
		return view.getFileResourceName();
	}

	public boolean holdsFileGameObject(FileGameObject fileGameObject) {
		return fileGameObject==view;
	}
	
	public NamedResource getNamedResource() {
		return view;
	}
	
	private void load() {
		view=StorageUtilities.loadView(getFile().getAbsolutePath());
	}
	
	public String getName() {
		return view.getName();
	}
	
	public View getView() {
		return view;
	}
	
	public boolean hasUnsavedChanges() {
		return view.hasChanges()||countDataChanges()>0;
	}
	
	public int countDataChanges() {
		// Return number of GOB Instances changed
		return 0;//adventureProjectModel.getGOBDataTableModel(view).getChangedCount();
	}

	public void save(AdventureProjectTreeModel adventureProjectTreeModel) {
		if (hasUnsavedChanges()) {
			var newPath=StorageUtilities.saveView(view,getFile().getAbsolutePath());
			setFile(new File(newPath));
			view.saved();
		}
	}
	
	public static class ProjectViewFileTreeNodeFactory implements ProjectFileTreeNodeFactory {
		@Override
		public ProjectFileTreeNode createNewNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
			return new ProjectViewTreeNode(adventureProjectModel, parent, file);
		}
	}
}
