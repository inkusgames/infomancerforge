package com.cinch.adventurebuilderstoolkit.editor.wizards;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.cinch.adventurebuilderstoolkit.ErrorUtilities;
import com.cinch.adventurebuilderstoolkit.beans.FileName;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.forms.NewLuaSourceFileForm;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectFileTreeNode;
import com.cinch.adventurebuilderstoolkit.editor.treenodes.ProjectSourceCodeTreeNode;

import snap.swing.Form;

public class NewLuaScriptWizard extends AbstractWizard<FileName> {
	private static final long serialVersionUID = 1L;

	private FileName fileName;
	
	private AdventureProjectModel adventureProjectModel;
	private ProjectFileTreeNode fileLocation;

	@SuppressWarnings("unchecked")
	public NewLuaScriptWizard(AdventureProjectModel adventureProjectModel,ProjectFileTreeNode fileLocation){
		super("New Lua Script",400);
		this.adventureProjectModel=adventureProjectModel;
		this.fileLocation=fileLocation;
		
		var forms=(Form<FileName>[])new Form[1];
		forms[0]=new NewLuaSourceFileForm();
		
		fileName=new FileName();
		fileName.setFileName("");
		
		setCompleteButtonText("Create new Lua Script");
		setFormsAndStart(fileName, forms);
	}
	
	@Override
	protected boolean completeWizard() {
		// Must have a name
		if (fileName.getFileName()==null || fileName.getFileName().isBlank()) {
			JOptionPane.showMessageDialog(this, "You must supply a name for this new script.", "Unable to create new Lua Script", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// Test name for suitable characters
		if(!fileName.getFileName().matches("[a-zA-z_ ()0-9/.\\-+]*")) {
			JOptionPane.showMessageDialog(this, "The name supplied must be a valid file name.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		String name=fileName.getFileName();
		if (!name.toLowerCase().endsWith(".lua")) {
			name=name+".lua";
		}
		
		File newFile=new File(fileLocation.getFile().getAbsolutePath()+"/"+name);
		
		// Test if a file or folder with that name already exists
		if(newFile.exists()) {
			if (newFile.isDirectory()) {
				JOptionPane.showMessageDialog(this, "A folder with that name already exists.", "Unable to create new Lua Script File", JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "A file with that name already exists.", "Unable to create new Lua Script File", JOptionPane.WARNING_MESSAGE);
			}
			return false;
		}

		try {
			if (!newFile.createNewFile()) {
				JOptionPane.showMessageDialog(this, "Unable to make source file, system errror.", "Unable to create new Folder", JOptionPane.WARNING_MESSAGE);
			} else {
				
				// Add new node to model.
				ProjectSourceCodeTreeNode treeNode=new ProjectSourceCodeTreeNode(adventureProjectModel, fileLocation, newFile);
				
				// Add to model
				adventureProjectModel.addFileNode(fileLocation,treeNode);
			}				
		} catch (HeadlessException|IOException e) {
			ErrorUtilities.showSeriousException(e);
			return false;
		}
		return true;
	}

}
