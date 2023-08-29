package com.cinch.adventurebuilderstoolkit.editor.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.Project;
import com.formdev.flatlaf.extras.components.FlatButton;

import snap.swing.CustomField;
import snap.swing.Form;

public class ProjectFormFile extends Form<Project> {
	private static final long serialVersionUID = 1L;

	public ProjectFormFile() {
		super(new Project());
	}
	
	@Override
	protected void initForm() {
		var detailGroup=addFieldGroup("Project File", 0,0,1,1);
		detailGroup.addField("Save As", FieldType.CUSTOM, 0, 0, 1, 1,new Object[] {new FileField()});
	}

	@Override
	public void newBeanValues() {
	}

	@Override
	public void newScreenValues() {
	}

	class FileField extends JPanel implements CustomField {
		private static final long serialVersionUID = 1L;
		
		private JTextField fileName;
		
		FileField(){
			super(new BorderLayout());
			fileName=new JTextField();

			FlatButton pickFileButton=new FlatButton();
			pickFileButton.setAction(new PickFileAction());
			
			add(fileName,BorderLayout.CENTER);
			add(pickFileButton,BorderLayout.EAST);
		}

		@Override
		public void intoGui(String field, Serializable bean, JComponent component) {
			Project project=(Project)bean;
			fileName.setText(project.getPath());
		}

		@Override
		public void intoBean(String field, Serializable bean, JComponent component) {
			Project project=(Project)bean;
			project.setPath(fileName.getText());
		}

		@Override
		public JComponent getComponent(String field, Serializable bean) {
			return this;
		}

		class PickFileAction extends AbstractAction{
			private static final long serialVersionUID = 1L;
			
			PickFileAction(){
				putValue(NAME, "Pick");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser=new JFileChooser(StorageUtilities.getConfig().getLastWorkingPath());
				jFileChooser.setDialogTitle("Pick new Project folder.");
				jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (jFileChooser.showDialog((Component)e.getSource(),"Select")==JFileChooser.APPROVE_OPTION) {
					String filename=jFileChooser.getSelectedFile().getAbsolutePath();
					fileName.setText(filename);
				}
			}
		}
	}
}

