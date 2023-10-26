package com.inkus.infomancerforge.editor.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBInstanceLinkReferanceTemplate;
import com.inkus.infomancerforge.beans.gobs.GOBInstanceReferanceTemplate;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.gob.GobEditor;
import com.inkus.infomancerforge.editor.swing.FormColorCustomEditor;
import com.inkus.infomancerforge.editor.swing.NamedResourceFilterComboBox;
import com.inkus.infomancerforge.editor.swing.NamedResourceFilterComboBox.ShowType;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.Matcher;
import snap.swing.Form;

public class GOBFormFactory {
	static private final Logger log = LogManager.getLogger(GOBFormFactory.class);
	private AdventureProjectModel adventureProjectModel;

	private String gobType=null;
	private View view=null;

	public GOBFormFactory(AdventureProjectModel adventureProjectModel,String gobType) {
		this(adventureProjectModel,gobType,null);
	}

	public GOBFormFactory(AdventureProjectModel adventureProjectModel,String gobType,View view) {
		this.adventureProjectModel = adventureProjectModel;
		this.gobType=gobType;
		this.view=view;
	}

	public GOBForm createGobForm(GOB gob,GobEditor gobEditor) {
		return new GOBForm(gob,gobEditor);
	}

	public GOBForm createGobWizardForm(GOB gob,GobEditor gobEditor) {
		return new GOBWizardForm(gob,gobEditor);
	}

	public GOBInstanceForm createGobInstanceForm() {
		return new GOBInstanceForm();
	}
	
	public GOBInstanceLinkForm createGobInstanceLinkForm() {
		return new GOBInstanceLinkForm();
	}

	public class GOBInstanceForm extends Form<GOBInstanceReferanceTemplate> {
		private static final long serialVersionUID = 1L;
		
		private NamedResourceFilterComboBox<GOB> parent;
		private EditorSet namedEdit;
		
		private GOBInstanceForm(){
			super(new GOBInstanceReferanceTemplate());
			GOBInstanceReferanceTemplate gobInstanceReferanceTemplate=new GOBInstanceReferanceTemplate();
			if (parent.getItemCount()==1) {
				gobInstanceReferanceTemplate.setType(parent.getItemAt(0).getUuid());
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						namedEdit.getEditor().requestFocus();
					}
				});
			}

			setCurrentBean(gobInstanceReferanceTemplate);
		}
		
		@Override
		public void newBeanValues() {
		}

		@Override
		public void newScreenValues() {
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected void initForm() {
			boolean hasName=true;
			EventList<GOB> list;
			if (gobType==null) {
				list=adventureProjectModel.getNamedResourceModel(GOB.class);	
				parent = new NamedResourceFilterComboBox(list,ShowType.ShowNamedOnly,true);
			} else {
				list=new BasicEventList<GOB>();
				list.add(adventureProjectModel.getNamedResourceByUuid(GOB.class, gobType));
				list.addAll(adventureProjectModel.getGOBChildren(list.get(0)));
				if (list.get(0).isNamed()) {
					parent = new NamedResourceFilterComboBox(list,ShowType.ShowNamedOnly,true);
				}else {
					hasName=false;
					parent = new NamedResourceFilterComboBox(list,ShowType.ShowUnnamedOnly,true);
				}
			}
			
			
			var detailGroup = addFieldGroup("GOB Instance Details", 0, 0, 1, 1);
			detailGroup.addField("Type", FieldType.CUSTOM, 0, 1, 1, 1, new Object[] { parent });
			if (hasName) {
				namedEdit=detailGroup.addField("Name", FieldType.STRING, 0, 2, 1, 1);
			}
		}
	}

	public class GOBInstanceLinkForm extends Form<GOBInstanceLinkReferanceTemplate> {
		private static final long serialVersionUID = 1L;
		
		private NamedResourceFilterComboBox<GOB> parent;
		private NamedResourceFilterComboBox<GOBInstance> child;
		private EditorSet namedEdit;

		private GOBInstanceLinkForm(){
			super(new GOBInstanceLinkReferanceTemplate());
			GOBInstanceLinkReferanceTemplate gobInstanceLinkReferanceTemplate=new GOBInstanceLinkReferanceTemplate();
			if (parent.getItemCount()==1) {
				gobInstanceLinkReferanceTemplate.setType(parent.getItemAt(0).getUuid());
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						namedEdit.getEditor().requestFocus();
					}
				});
			}
			setCurrentBean(gobInstanceLinkReferanceTemplate);
		}
		
		@Override
		public void newBeanValues() {
		}

		@Override
		public void newScreenValues() {
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected void initForm() {
			child = new NamedResourceFilterComboBox(new BasicEventList<GOBInstance>());
			
			EventList<GOB> list;
			if (gobType==null) {
				list=adventureProjectModel.getNamedResourceModel(GOB.class,null);	
			} else {
				list=new BasicEventList<GOB>();
				list.add(adventureProjectModel.getNamedResourceByUuid(GOB.class, gobType));
				list.addAll(adventureProjectModel.getGOBChildren(list.get(0)));
			}

			parent = new NamedResourceFilterComboBox(list,ShowType.ShowNamedOnly,true);
			
			parent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GOB gob=(GOB)parent.getSelectedItem();
					if (gob!=null) {
						child.setEntries(adventureProjectModel.getNamedResourceModel(GOBInstance.class,gob.getUuid()));
					}else {
						child.setEntries(new BasicEventList<GOBInstance>());
					}
				}
			});
			
			if (view!=null) {
				Set<String> inView=new HashSet<>();
				for (var g:view.getGobs()) {
					inView.add(g.getGobReferance().getUuid());
				}
				child.setAdditionalFilter(new Matcher<NamedResource>() {
					@Override
					public boolean matches(NamedResource item) {
						return !inView.contains(item.getUuid());
					}
				});
			}

			var detailGroup = addFieldGroup("GOB Instance Details", 0, 0, 1, 1);
			detailGroup.addField("Type", FieldType.CUSTOM, 0, 1, 1, 1, new Object[] { parent });
			namedEdit=detailGroup.addField("Uuid","Instance", FieldType.CUSTOM, 0, 2, 1, 1,new Object[] { child });
		}
	}
	
	public class GOBForm extends Form<GOB> {
		private static final long serialVersionUID = 1L;

		protected NamedResourceFilterComboBox<GOB> parent;

		protected GOB oldGOB = null;
		protected GobEditor gobEditor;

		protected GOBForm(GOB gob,GobEditor gobEditor) {
			super(gob);
			oldGOB = (GOB) SerializationUtils.clone(gob);
			this.gobEditor=gobEditor;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void initForm() {
			parent = new NamedResourceFilterComboBox(adventureProjectModel.getNamedResourceModel(GOB.class));

			var detailGroup = addFieldGroup("GOB Details", 0, 0, 1, 1);
			detailGroup.addField("Name", FieldType.STRING_INFO, 0, 0, 1, 1);
			detailGroup.addField("Type", FieldType.DROP_DOWN, 0, 1, 1, 1);
			detailGroup.addField("Parent", FieldType.CUSTOM, 0, 2, 1, 1, new Object[] { parent });
			detailGroup.addField("DefinitionOnly", FieldType.CHECKBOX, 1, 3, 1, 1);
			
			var displayGroup = addFieldGroup("View Details", 1, 0, 1, 1);
			displayGroup.addField("ColorBackground", FieldType.CUSTOM, 0, 0, 1, 1, new Object[] { new FormColorCustomEditor("Pick View Color")});
			displayGroup.addField("DefaultViewMode", FieldType.DROP_DOWN, 0, 1, 1, 1);
			displayGroup.addField("SummaryAlignment", FieldType.DROP_DOWN, 0, 2, 1, 1);
			displayGroup.addField("Summary", FieldType.TEXT, 0, 3, 1, 3);
		}

		@Override
		public void newBeanValues() {
			GOB gob = getCurrentBean();
			
			// Make sure we have an ID field if we need one.
			boolean modifiedProperties=false;
			List<GOBPropertyDefinition> extraIds=null;
			if (gob.getType()==GOB.Type.Base && (gob.getParent()==null || gob.getParent().length()==0)) {
				boolean hasIdField=false;
				for (var p:gob.getPropertyDefinitions()) {
					if (p.getType()==Type.ID) {
						if (!hasIdField) {
							hasIdField=true;
							if (p.isArray() || !p.isRequired() || p.getName()==null || p.getName().length()==0) {
								modifiedProperties=true;
								p.setArray(false);
								p.setRequired(true);
								if (p.getName()==null || p.getName().length()==0) {
									p.setName("name");
								}
							}
						} else {
							if (extraIds==null) {
								extraIds=new ArrayList<>();
							}
							extraIds.add(p);
						}
					}
				}
				if (!hasIdField) {
					modifiedProperties=true;
					var id= new GOBPropertyDefinition();
					id.setGobFieldName("ID");
					id.setName("name");
					id.setType(GOBPropertyDefinition.Type.ID);
					id.setArray(false);
					id.setRequired(true);
					gob.getPropertyDefinitions().add(0,id);
				}
			}else {
				for (var p:gob.getPropertyDefinitions()) {
					if (p.getType()==Type.ID) {
						if (extraIds==null) {
							extraIds=new ArrayList<>();
						}
						extraIds.add(p);
					}
				}
			}
			if (extraIds!=null) {
				modifiedProperties=true;
				gob.getPropertyDefinitions().removeAll(extraIds);
			}
			if (modifiedProperties && gobEditor!=null) {
				log.trace("Modified GOB's properties to match it's base needs.");
				gobEditor.propertiedModelChanged();
				adventureProjectModel.markAllGOBChildrenAsTouched(gob,gob);
//				adventureProjectModel.fireFileGameObjectChange(this, gob);
			}
			
			if (oldGOB != null && !oldGOB.equals(gob)) {
				log.trace("Bean changed '"+gob.getName()+"'");
				log.trace("Bean changed Old name '"+oldGOB.getName()+"'");
				gob.touch();
				adventureProjectModel.fireFileGameObjectChange(gob, gob);
			}
			oldGOB = (GOB) SerializationUtils.clone(gob);
		}

		@Override
		public void newScreenValues() {
			GOB gob = getCurrentBean();
			log.trace("New GOB Screen values '"+gob.getName()+"'");
			oldGOB = (GOB) SerializationUtils.clone(gob);
			
			var excludeNames=adventureProjectModel.getGOBChildren(gob);
			excludeNames.add(gob);

			parent.setAdditionalFilter(new NamedResourceFilterComboBox.NamedFilter(excludeNames, null));
		}
	
		public void refresh() {
			//setCurrentBean(getCurrentBean());
			beanIntoGui();
		}

	}
	
	class GOBWizardForm extends GOBForm {
		private static final long serialVersionUID = 1L;

		protected GOBWizardForm(GOB gob, GobEditor gobEditor) {
			super(gob, gobEditor);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void initForm() {
			parent = new NamedResourceFilterComboBox(adventureProjectModel.getNamedResourceModel(GOB.class));

			var detailGroup = addFieldGroup("GOB Details", 0, 0, 1, 1);
			detailGroup.addField("Type", FieldType.DROP_DOWN, 0, 0, 1, 1);
			detailGroup.addField("Parent", FieldType.CUSTOM, 0, 1, 1, 1, new Object[] { parent });
			detailGroup.addField("Name", FieldType.STRING, 0, 2, 1, 1);
			detailGroup.addField("DefinitionOnly", FieldType.CHECKBOX, 1, 3, 1, 1);
			
			var displayGroup = addFieldGroup("View Details", 1, 0, 1, 1);
			displayGroup.addField("ColorBackground", FieldType.CUSTOM, 0, 0, 1, 1, new Object[] { new FormColorCustomEditor("Pick View Color")});
			displayGroup.addField("DefaultViewMode", FieldType.DROP_DOWN, 0, 1, 1, 1);
			displayGroup.addField("Summary", FieldType.TEXT, 0, 2, 1, 3);
		}
	}
}