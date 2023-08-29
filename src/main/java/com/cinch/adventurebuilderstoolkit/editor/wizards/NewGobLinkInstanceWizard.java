package com.cinch.adventurebuilderstoolkit.editor.wizards;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cinch.adventurebuilderstoolkit.beans.gobs.GOB;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBInstance;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBInstanceLinkReferanceTemplate;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBProperty;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOBReferance;
import com.cinch.adventurebuilderstoolkit.beans.views.GobView;
import com.cinch.adventurebuilderstoolkit.beans.views.View;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.forms.GOBFormFactory;

import snap.swing.Form;

public class NewGobLinkInstanceWizard extends AbstractWizard<GOBInstanceLinkReferanceTemplate> {
	@SuppressWarnings("unused")
	static private final Logger log = LogManager.getLogger(NewGobLinkInstanceWizard.class);
	private static final long serialVersionUID = 1L;

	private GOBInstanceLinkReferanceTemplate gobInstanceReferanceTemplate;
	
	private AdventureProjectModel adventureProjectModel;
	private View view;
	private int viewX;
	private int viewY;
	
	private GOBProperty<GOBInstance> linkedIntoProperty;
	private GOBInstance gobInstanceChanged;

	@SuppressWarnings("unchecked")
	public NewGobLinkInstanceWizard(AdventureProjectModel adventureProjectModel,View view, int viewX, int viewY, int sx, int sy,GOBInstance gobInstanceChanged,GOBProperty<GOBInstance> linkedIntoProperty){
		super("Link GOB Instance",400);
		this.view=view;
		this.viewX=viewX;
		this.viewY=viewY;
		this.adventureProjectModel=adventureProjectModel;
		this.linkedIntoProperty=linkedIntoProperty;
		this.gobInstanceChanged=gobInstanceChanged;
		setScreenPos(new Point(sx,sy));
		
		var forms=(Form<GOBInstanceLinkReferanceTemplate>[])new Form[1];
		forms[0]=new GOBFormFactory(adventureProjectModel,linkedIntoProperty!=null?linkedIntoProperty.getGOBPropertyDefinition().getGobType():null,view).createGobInstanceLinkForm();
		
		gobInstanceReferanceTemplate=new GOBInstanceLinkReferanceTemplate();
		
		setCompleteButtonText("Link Instance");
		setFormsAndStart(gobInstanceReferanceTemplate, forms);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean completeWizard() {
		// Must be a named resource before you can continue.
		if (gobInstanceReferanceTemplate.getType()==null || gobInstanceReferanceTemplate.getType().isBlank() || gobInstanceReferanceTemplate.getUuid()==null || gobInstanceReferanceTemplate.getUuid().isBlank()) {
			JOptionPane.showMessageDialog(this, "No GOB Instance selected to add to this view.", "You need to pick a GOB Instance", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// If this instance is already in the view don't allow a second link to it.
		boolean found=false;
		for (var g:view.getGobs()) {
			if (g.getGobReferance().getUuid().equals(gobInstanceReferanceTemplate.getUuid())){
				found=true;
				break;
			}
		}
		if (found) {
			JOptionPane.showMessageDialog(this, "Each GOB Instance can only be linked to a view once.", "That GOB Instance is already on this view.", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// Add GobInstance to model
		GOBInstance gobInstance=null;
		GOB gob=null;
		for (var gi:adventureProjectModel.getNamedResourceModel(GOBInstance.class,gobInstanceReferanceTemplate.getType())) {
			if (gi.getUuid().equals(gobInstanceReferanceTemplate.getUuid())) {
				gobInstance=gi;
				gob=adventureProjectModel.getNamedResourceByUuid(GOB.class,gi.getGobType());
				break;
			}
		}
		
		// Unexpected error
		if (gobInstance==null) {
			JOptionPane.showMessageDialog(this, "We are unablke to fiund that GOB Instance in the model this is a BUG.", "UNEXPECTED ERROR", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// Add instance reference to view
		GOBReferance gobReferance=new GOBReferance();
		gobReferance.setTypeUuid(gobInstance.getGobType());
		gobReferance.setUuid(gobInstance.getUuid());
		
		GobView gobView=new GobView();
		gobView.setGobReferance(gobReferance);
		gobView.setBounds(new Rectangle(viewX, viewY, 200, 40));
		gobView.recalcSize(adventureProjectModel);
		gobView.setView(view);
		if (gob.getDefaultViewMode()!=null) {
			gobView.setViewMode(gob.getDefaultViewMode());
		}
		view.getGobs().add(gobView);
		
		if (linkedIntoProperty!=null) {
			if (linkedIntoProperty.getGOBPropertyDefinition().isArray()) {
				Object value=linkedIntoProperty.getValue();
				List<GOBInstance> list;
				if (value instanceof List<?> currentList) {
					list=(List<GOBInstance>) currentList;
				} else {
					list=new ArrayList<GOBInstance>();
					if (value instanceof GOBInstance currentGob) {
						list.add(currentGob);
					}
					linkedIntoProperty.setValue(list);
				}
				list.add(gobInstance);
			} else {
				linkedIntoProperty.setValue(gobReferance.getGobInstance(adventureProjectModel));
			}
			
			GOB changedGob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstanceChanged.getGobType());
			adventureProjectModel.fireDataInstanceChange(this,gobInstanceChanged);
			adventureProjectModel.getGOBDataTableModel(changedGob).changeRowByUUID(gobInstanceChanged.getUuid());
			adventureProjectModel.fireFileGameObjectChange(this, changedGob);
		}
		
		view.touch();
		adventureProjectModel.fireFileGameObjectChange(this, view);
		return true;
	}

}
