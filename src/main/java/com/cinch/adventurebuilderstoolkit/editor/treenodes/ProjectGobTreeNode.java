package com.cinch.adventurebuilderstoolkit.editor.treenodes;

import java.io.File;

import javax.swing.tree.TreeNode;

import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.FileGameObject;
import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOB;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectTreeModel;
import com.cinch.adventurebuilderstoolkit.editor.gob.GOBDataTableModel;

public class ProjectGobTreeNode extends ProjectFileTreeNamedResourceNode {

	private GOB gob=null;
	
	public ProjectGobTreeNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file,GOB gob) {
		super(adventureProjectModel, parent, file);
		this.gob=gob;
	}
	
	public ProjectGobTreeNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
		super(adventureProjectModel, parent, file);
		load();
	}

	public boolean holdsFileGameObject(FileGameObject fileGameObject) {
		return fileGameObject==gob;
	}
	
	public NamedResource getNamedResource() {
		return gob;
	}
	
	private void load() {
		gob=StorageUtilities.loadGOB(getFile().getAbsolutePath());
	}
	
	public String getName() {
		return gob.getName();
	}
	
	public GOB getGob() {
		return gob;
	}
	
	public boolean hasUnsavedChanges() {
		return gob.hasChanges()||countDataChanges()>0;
	}
	
	public int countDataChanges() {
		// Return number of GOB Instances changed
		return adventureProjectModel.getGOBDataTableModel(gob).getChangedCount();
	}

	public void save(AdventureProjectTreeModel adventureProjectTreeModel) {
		if (hasUnsavedChanges()) {
			GOBDataTableModel gobDataTableModel=adventureProjectTreeModel.getAdventureProjectModel().getGOBDataTableModel(gob);
			var dataModel=adventureProjectTreeModel.getAdventureProjectModel().getDataModel();
			var allProperties=adventureProjectTreeModel.getAdventureProjectModel().getAllGobProperties(gob);
			
			dataModel.updateStructure(gob,allProperties);
			
			// Save all the objects we have already edited first.
			for (String u:gobDataTableModel.getDeletedRecords()) {
				dataModel.delete(gob, u);
			}
			for (String u:gobDataTableModel.getNewRecords()) {
				dataModel.update(gob,allProperties, gobDataTableModel.getRowByUUID(u));
			}
			for (String u:gobDataTableModel.getChangedRecords()) {
				dataModel.update(gob,allProperties, gobDataTableModel.getRowByUUID(u));
			}
			gobDataTableModel.saved();
			
			var newPath=StorageUtilities.saveGOB(gob,getFile().getAbsolutePath());
			setFile(new File(newPath));
			gob.saved();
			
			dataModel.save();
		}
	}
	
	public static class ProjectGobFileTreeNodeFactory implements ProjectFileTreeNodeFactory {
		@Override
		public ProjectFileTreeNode createNewNode(AdventureProjectModel adventureProjectModel, TreeNode parent, File file) {
			return new ProjectGobTreeNode(adventureProjectModel, parent, file);
		}
	}	
}
