package com.inkus.infomancerforge.editor.wizards;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.forms.GOBFormFactory;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectGobTreeNode;

import snap.swing.Form;

public class NewGobWizard extends AbstractWizard<GOB> {
	@SuppressWarnings("unused")
	static private final Logger log=LogManager.getLogger(NewGobWizard.class);
	private static final long serialVersionUID = 1L;

	private GOB gob;
	
	private AdventureProjectModel adventureProjectModel;
	private ProjectFileTreeNode fileLocation;

	@SuppressWarnings("unchecked")
	public NewGobWizard(AdventureProjectModel adventureProjectModel,ProjectFileTreeNode fileLocation,String gobType){
		super("New GOB",400);
		this.adventureProjectModel=adventureProjectModel;
		this.fileLocation=fileLocation;
		
		var forms=(Form<GOB>[])new Form[1];
		forms[0]=new GOBFormFactory(adventureProjectModel,gobType).createGobWizardForm(new GOB(),null);
		
		gob=new GOB();
		gob.setName("");
		
		setCompleteButtonText("Create new GOB");
		setFormsAndStart(gob, forms);
	}
	
	@Override
	protected boolean completeWizard() {
		// Must have a name
		if (gob.getName()==null || gob.getName().isBlank()) {
			JOptionPane.showMessageDialog(this, "Each GOB needs to have a unique name.", "Unable to create new GOB", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// Make sure this GOB name is unique
		if (adventureProjectModel.isNamedResourceUsed(GOB.class,gob.getName())) {
			JOptionPane.showMessageDialog(this, "Each GOB needs to have a unique name.\nThe name '"+gob.getName()+"' already exists.", "Unable to create new GOB", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		if (gob.getParent()!=null) {
			GOB parentGob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gob.getParent());
			if (parentGob.getType()!=gob.getType()) {
				JOptionPane.showMessageDialog(this, "Gob type must be the same as parent. Parent gob is of type "+parentGob.getType().name(), "Unable to create new GOB", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		Set<String> currentTables=new HashSet<>();
		adventureProjectModel.getNamedResourceModel(GOB.class).forEach((g)->{currentTables.add(g.getGobTableName());});
		gob.setGobTableName(StorageUtilities.getRandomName(10, currentTables));
		gob.setMyFile(new File(fileLocation.getFile().getAbsolutePath()+"/"+gob.getName()+".gob"));
		gob.touch();
		ProjectGobTreeNode treeNode=new ProjectGobTreeNode(adventureProjectModel, fileLocation, gob);
		
		// Add to model
		adventureProjectModel.addFileNode(fileLocation,treeNode);
		adventureProjectModel.fireFileGameObjectChange(gob,gob);
		adventureProjectModel.getAnalyticsController().sendEvent("GOB", "Create", null);

		return true;
	}

}
