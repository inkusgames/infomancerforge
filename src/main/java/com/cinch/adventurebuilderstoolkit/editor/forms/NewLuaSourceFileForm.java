package com.cinch.adventurebuilderstoolkit.editor.forms;

import com.cinch.adventurebuilderstoolkit.beans.FileName;

import snap.swing.Form;

public class NewLuaSourceFileForm extends Form<FileName> {
	private static final long serialVersionUID = 1L;

	public NewLuaSourceFileForm() {
		super(new FileName());
	}
	
	@Override
	protected void initForm() {
		var detailGroup=addFieldGroup("Lua Script Details", 0,0,1,1);
		detailGroup.addField("FileName", "Script Name", FieldType.STRING, 0, 0, 1,1);
	}

	@Override
	public void newBeanValues() {
	}

	@Override
	public void newScreenValues() {
	}


}
