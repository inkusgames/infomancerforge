package com.cinch.adventurebuilderstoolkit.editor.swing;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.formdev.flatlaf.extras.components.FlatButton;

public class ToolbarButton extends FlatButton {
	private static final long serialVersionUID = 1L;

	private String randomActionName="KeyBind"+Math.random();
	
	public ToolbarButton(Action a) {
		setAction(a);
	}

	public void addBindingTo(JComponent boundTo) {
		Action a=getAction();
		if (a!=null && a.getValue(Action.ACCELERATOR_KEY)!=null) {
			boundTo.getActionMap().put(randomActionName, a);
			boundTo.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), randomActionName);
		}
	}
}
