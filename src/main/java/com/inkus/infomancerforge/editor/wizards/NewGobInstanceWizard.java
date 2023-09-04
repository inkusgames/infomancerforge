package com.inkus.infomancerforge.editor.wizards;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBInstanceReferanceTemplate;
import com.inkus.infomancerforge.beans.gobs.GOBProperty;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBReferance;
import com.inkus.infomancerforge.beans.views.GobView;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.forms.GOBFormFactory;

import snap.swing.Form;

public class NewGobInstanceWizard extends AbstractWizard<GOBInstanceReferanceTemplate> {
	static private final Logger log = LogManager.getLogger(NewGobInstanceWizard.class);
	private static final long serialVersionUID = 1L;

	private GOBInstanceReferanceTemplate gobInstanceReferanceTemplate;
	
	private AdventureProjectModel adventureProjectModel;
	private View view;
	private int viewX;
	private int viewY;

	private GOBProperty<GOBInstance> linkedIntoProperty;
	private GOBInstance gobInstanceChanged;
	
	@SuppressWarnings("unchecked")
	public NewGobInstanceWizard(AdventureProjectModel adventureProjectModel,View view, int viewX, int viewY, int sx, int sy,GOBInstance gobInstanceChanged,GOBProperty<GOBInstance> linkedIntoProperty){
		super("New GOB Instance",400);
		this.adventureProjectModel=adventureProjectModel;
		this.view=view;
		this.viewX=viewX;
		this.viewY=viewY;
		this.gobInstanceChanged=gobInstanceChanged;
		this.linkedIntoProperty=linkedIntoProperty;
		setScreenPos(new Point(sx,sy));
		
		var forms=(Form<GOBInstanceReferanceTemplate>[])new Form[1];
		forms[0]=new GOBFormFactory(adventureProjectModel,linkedIntoProperty!=null?linkedIntoProperty.getGOBPropertyDefinition().getGobType():null).createGobInstanceForm();
		
		gobInstanceReferanceTemplate=new GOBInstanceReferanceTemplate();
		
		setCompleteButtonText("Create new Instance");
		setFormsAndStart(gobInstanceReferanceTemplate, forms);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean completeWizard() {
		// Must have a name if the GOB is named
		GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstanceReferanceTemplate.getType());
		if (gob.isNamed()) {
			if (gobInstanceReferanceTemplate.getName()==null || gobInstanceReferanceTemplate.getName().isBlank()) {
				JOptionPane.showMessageDialog(this, "Each GOB Instance needs to have a unique name.", "Unable to create new GOB Instance", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			// Make sure this GOB name is unique
			if (adventureProjectModel.isNamedResourceUsed(GOBInstance.class,gobInstanceReferanceTemplate.getName())) {
				JOptionPane.showMessageDialog(this, "Each GOB Instance needs to have a unique name.\nThe name '"+gobInstanceReferanceTemplate.getName()+"' already exists.", "Unable to create new GOB Instance", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		// Add GobInstance to model
		//GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstanceReferanceTemplate.getType());
		
		GOBInstance gobInstance=new GOBInstance();
		gobInstance.setGobType(gobInstanceReferanceTemplate.getType());
		for (var p:adventureProjectModel.getAllGobProperties(gob)) {
			if (p.getType()==GOBPropertyDefinition.Type.ID) {
				GOBProperty<String> property=new GOBProperty<>(p,gobInstanceReferanceTemplate.getName());
				gobInstance.getProperties().put(p.getGobFieldName(), property);
				break;
			}
		}
		if (gobInstance.getProperties().size()<1) {
			log.error("New GOB Instance should have a name field, but one was not found.");
		}
		if (gobInstance.getProperties().size()>1) {
			log.error("New GOB Instance should only have a name field when first created, but more then one field was set.");
		}
		
		var gobDataTableModel=adventureProjectModel.getGOBDataTableModel(gob);
		int pos=gobDataTableModel.addRow(gobInstance);
		gobDataTableModel.fireTableRowsInserted(pos, pos);

		// Add instance reference to view
		GOBReferance gobReferance=new GOBReferance();
		gobReferance.setTypeUuid(gob.getUuid());
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
		adventureProjectModel.fireDataInstanceChange(this,gobInstance);
		adventureProjectModel.fireFileGameObjectChange(this, gob);
		return true;
	}

}
