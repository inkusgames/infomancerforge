package com.inkus.infomancerforge.editor.wizards;

import java.io.File;

import javax.swing.JOptionPane;

import com.inkus.infomancerforge.beans.FolderName;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.forms.NewFolderForm;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;

import snap.swing.Form;

public class NewFolderWizard extends AbstractWizard<FolderName> {
	private static final long serialVersionUID = 1L;

	private FolderName folderName;
	
	private AdventureProjectModel adventureProjectModel;
	private ProjectFileTreeNode fileLocation;

	@SuppressWarnings("unchecked")
	public NewFolderWizard(AdventureProjectModel adventureProjectModel,ProjectFileTreeNode fileLocation){
		super("New Folder",400);
		this.adventureProjectModel=adventureProjectModel;
		this.fileLocation=fileLocation;
		
		var forms=(Form<FolderName>[])new Form[1];
		forms[0]=new NewFolderForm();
		
		folderName=new FolderName();
		folderName.setFolderName("");
		
		setCompleteButtonText("Create new Folder");
		setFormsAndStart(folderName, forms);
	}
	
	@Override
	protected boolean completeWizard() {
		// Must have a name
		if (folderName.getFolderName()==null || folderName.getFolderName().isBlank()) {
			JOptionPane.showMessageDialog(this, "You must supply a name for this new folder.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// Test name for suitable characters
		if(!folderName.getFolderName().matches("[a-zA-z_ ()0-9/.\\-+]*")) {
			JOptionPane.showMessageDialog(this, "The name supplied must be a valid folder name.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		File newFolder=new File(fileLocation.getFile().getAbsolutePath()+"/"+folderName.getFolderName());
		
		// Test if a file or folder with that name already exists
		if(newFolder.exists()) {
			if (newFolder.isDirectory()) {
				JOptionPane.showMessageDialog(this, "A folder with that name already exists.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "A file with that name already exists.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
			}
			return false;
		}

		if (!newFolder.mkdir()) {
			JOptionPane.showMessageDialog(this, "Unable to make folder, system errror.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
		}

		// Add new node to model.
		ProjectFileTreeNode treeNode=new ProjectFileTreeNode(adventureProjectModel, fileLocation, newFolder);
		
		// Add to model
		adventureProjectModel.addFileNode(fileLocation,treeNode);
		
		return true;
	}

}
