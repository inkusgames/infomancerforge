package com.cinch.adventurebuilderstoolkit.editor.swing;

import java.awt.Component;
import java.util.List;

import javax.swing.plaf.basic.BasicComboBoxEditor;

import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.formdev.flatlaf.extras.components.FlatTextField;

import ca.odell.glazedlists.EventList;

public class NamedResourceComboBoxEditor extends BasicComboBoxEditor {
	
	private List<NamedResource> items;
	private FlatTextField textField;
	
	public NamedResourceComboBoxEditor(EventList<NamedResource> entries) {
		textField=new FlatTextField();
		this.items=entries;
	}
	
    @Override
	public Component getEditorComponent() {
    	return textField;
	}

    @Override
	public void setItem(Object anObject) {
    	int pos=textField.getCaretPosition();
    	if (anObject==null) {
    		textField.setText(null);
    	} else if (anObject instanceof String) {
    		textField.setText((String)null);
    	} else {
    		textField.setText(((NamedResource)anObject).getName());
    	}
    	textField.setCaretPosition(Math.min(textField.getText().length(), pos));
	}

	@Override
	public Object getItem() {
		if (textField.getText()!=null && textField.getText().length()>0) {
			for (var n:items) {
				if (n.getName().equals(textField.getText())) {
					return n;
				}
			}
			for (var n:items) {
				if (n.getName().toLowerCase().equals(textField.getText().toLowerCase())) {
					textField.setText(n.getName());
					return n;
				}
			}
		}
		return null;
	}
}
