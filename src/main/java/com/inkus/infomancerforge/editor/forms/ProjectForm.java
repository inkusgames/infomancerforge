package com.inkus.infomancerforge.editor.forms;

import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.inkus.infomancerforge.beans.Project;

import snap.swing.CustomField;
import snap.swing.Form;

public class ProjectForm extends Form<Project> {
	private static final long serialVersionUID = 1L;

	public ProjectForm() {
		super(new Project());
	}
	
	@Override
	protected void initForm() {
		var detailGroup=addFieldGroup("Project Details", 0,0,1,1);
		detailGroup.addField("Name", FieldType.STRING, 0, 0, 1,1);
		detailGroup.addField("Authors", FieldType.CUSTOM, 0, 1, 1, 1,new Object[] {new AuthorField()});
		detailGroup.addField("Description", FieldType.TEXT, 0, 2, 1, 1);
	}

	@Override
	public void newBeanValues() {
	}

	@Override
	public void newScreenValues() {
	}
	
	
	class AuthorField extends JTextField implements CustomField {
		private static final long serialVersionUID = 1L;

		@Override
		public void intoGui(String field, Serializable bean, JComponent component) {
			Project project=(Project)bean;
			if (project.getAuthors()!=null){
				StringBuffer authors=new StringBuffer();
				for (var author:project.getAuthors()) {
					if (authors.length()>0) {
						authors.append(", ");
					}
					authors.append(author);
				}
				setText(authors.toString());
			}else {
				setText("");
			}
		}

		@Override
		public void intoBean(String field, Serializable bean, JComponent component) {
			Project project=(Project)bean;
			String authorsText=getText();
			if (authorsText!=null && authorsText.trim().length()>0) {
				String[] authors=getText().split(",");
				for (int t=0;t<authors.length;t++) {
					authors[t]=authors[t].trim();
				}
				project.setAuthors(authors);
			} else {
				project.setAuthors(null);
			}
		}

		@Override
		public JComponent getComponent(String field, Serializable bean) {
			return this;
		}
	}

}
