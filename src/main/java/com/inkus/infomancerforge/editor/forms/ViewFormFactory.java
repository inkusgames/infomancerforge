package com.inkus.infomancerforge.editor.forms;

import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

import snap.swing.Form;

public class ViewFormFactory {
	static private final Logger log = LogManager.getLogger(ViewFormFactory.class);
	private AdventureProjectModel adventureProjectModel;

	public ViewFormFactory(AdventureProjectModel adventureProjectModel) {
		this.adventureProjectModel = adventureProjectModel;
	}

	public ViewForm createViewForm(View view,ViewEditor viewEditor) {
		return new ViewForm(view,viewEditor);
	}

	public class ViewForm extends Form<View> {
		private static final long serialVersionUID = 1L;

		private View oldView = null;
		@SuppressWarnings("unused")
		private ViewEditor viewEditor;

		public ViewForm(View view,ViewEditor viewEditor) {
			super(view);
			oldView = (View) SerializationUtils.clone(view);
			this.viewEditor=viewEditor;
		}

		@Override
		protected void initForm() {
			var detailGroup = addFieldGroup("View Details", 0, 0, 1, 1);
			detailGroup.addField("Name", FieldType.STRING, 0, 2, 1, 1);
		}

		@Override
		public void newBeanValues() {
			View view = getCurrentBean();
			
			if (oldView != null && !oldView.equals(view)) {
				log.trace("Bean changed '"+view.getName()+"'");
				log.trace("Bean changed Old name '"+oldView.getName()+"'");
				view.touch();
				adventureProjectModel.fireFileGameObjectChange(view, view);
			}
			oldView = (View) SerializationUtils.clone(view);
		}

		@Override
		public void newScreenValues() {
			View view = getCurrentBean();
			log.trace("New View Screen values '"+view.getName()+"'");
			oldView = (View) SerializationUtils.clone(view);
		}
	
		public void refresh() {
			setCurrentBean(oldView);
		}

	}
}