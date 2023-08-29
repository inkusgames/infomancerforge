package com.cinch.adventurebuilderstoolkit.editor.actions;

import javax.swing.AbstractAction;

import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;

public abstract class BaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	protected AdventureProjectModel adventureProjectModel;

	public BaseAction(AdventureProjectModel adventureProjectModel) {
		super();
		this.adventureProjectModel = adventureProjectModel;
	}

}
