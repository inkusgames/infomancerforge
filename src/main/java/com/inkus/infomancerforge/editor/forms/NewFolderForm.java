package com.inkus.infomancerforge.editor.forms;

import com.inkus.infomancerforge.beans.FolderName;

import snap.swing.Form;

public class NewFolderForm extends Form<FolderName> {
	private static final long serialVersionUID = 1L;

	public NewFolderForm() {
		super(new FolderName());
	}
	
	@Override
	protected void initForm() {
		var detailGroup=addFieldGroup("Folder Details", 0,0,1,1);
		detailGroup.addField("FolderName", "Folder Name", FieldType.STRING, 0, 0, 1,1);
	}

	@Override
	public void newBeanValues() {
	}

	@Override
	public void newScreenValues() {
	}


}
