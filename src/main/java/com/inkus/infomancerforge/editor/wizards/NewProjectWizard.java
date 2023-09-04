package com.inkus.infomancerforge.editor.wizards;

import java.io.File;

import javax.swing.JOptionPane;

import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.Project;
import com.inkus.infomancerforge.editor.forms.ProjectForm;
import com.inkus.infomancerforge.editor.forms.ProjectFormFile;

import snap.swing.Form;

public class NewProjectWizard extends AbstractWizard<Project> {
	private static final long serialVersionUID = 1L;

	private Project project;
	private Project newProject=null;
	
	@SuppressWarnings("unchecked")
	public NewProjectWizard() {
		super("New Project",600);
		var forms=(Form<Project>[])new Form[2];
		forms[0]=new ProjectForm();
		forms[1]=new ProjectFormFile();
		
		project=new Project();
		project.setName("My Super Awesome Adventure Game");
		project.setDescription("It's all in the title folks. No Really.");
		
		setCompleteButtonText("Create new project");
		setFormsAndStart(project, forms);
	}

	@Override
	protected boolean completeWizard() {
		// We must have a path
		if (project.getPath()==null || project.getPath().trim().length()==0) {
			JOptionPane.showMessageDialog(this, "You need to select a valid project folder path first.", "Unable to create new project", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// Path must be a folder
		String filePath=project.getPath();
		File projectFile=new File(filePath);
		if (!projectFile.isDirectory()) {
			JOptionPane.showMessageDialog(this, "The selected path is not a folder.", "Unable to create new project", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		// Folder must be empty
		File[] files=projectFile.listFiles();
		if (files.length>0) {
			JOptionPane.showMessageDialog(this, "The selected folder must be empty to start a new project.", "Unable to create new project", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		// Save project file.
		StorageUtilities.saveProjectFile(project);
		project.setPath(filePath);
		
		newProject=project;
		
		return true;
	}
	
	public Project getProject() {
		return newProject;
	}
}
