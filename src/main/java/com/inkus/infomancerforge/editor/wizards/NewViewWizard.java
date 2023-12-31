package com.inkus.infomancerforge.editor.wizards;

import java.io.File;

import javax.swing.JOptionPane;

import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.forms.ViewFormFactory;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectViewTreeNode;

import snap.swing.Form;

public class NewViewWizard extends AbstractWizard<View> {
	private static final long serialVersionUID = 1L;

	private View view;
	
	private AdventureProjectModel adventureProjectModel;
	private ProjectFileTreeNode fileLocation;

	@SuppressWarnings("unchecked")
	public NewViewWizard(AdventureProjectModel adventureProjectModel,ProjectFileTreeNode fileLocation){
		super("New View",400);
		this.adventureProjectModel=adventureProjectModel;
		this.fileLocation=fileLocation;
		
		var forms=(Form<View>[])new Form[1];
		forms[0]=new ViewFormFactory(adventureProjectModel).createViewForm(new View(),null);
		
		view=new View();
		view.setName("");
		
		setCompleteButtonText("Create new View");
		setFormsAndStart(view, forms);
	}
	
	@Override
	protected boolean completeWizard() {
		// Must have a name
		if (view.getName()==null || view.getName().isBlank()) {
			JOptionPane.showMessageDialog(this, "Each View needs to have a unique name.", "Unable to create new View", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// Make sure this GOB name is unique
		if (adventureProjectModel.isNamedResourceUsed(View.class,view.getName())) {
			JOptionPane.showMessageDialog(this, "Each View needs to have a unique name.\nThe name '"+view.getName()+"' already exists.", "Unable to create new View", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		view.setMyFile(new File(fileLocation.getFile().getAbsolutePath()+"/"+view.getName()+".view"));
		view.touch();
		
		ProjectViewTreeNode treeNode=new ProjectViewTreeNode(adventureProjectModel, fileLocation, view);
		
		// Add to model
		adventureProjectModel.addFileNode(fileLocation,treeNode);
		adventureProjectModel.getAnalyticsController().sendEvent("View", "Create", null);

		return true;
	}

}
