package com.inkus.infomancerforge.editor.actions;

import java.awt.event.ActionEvent;

import com.inkus.infomancerforge.editor.gob.ViewEditor;

public abstract class BaseViewAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	
	protected ViewEditor viewEditor;

	public BaseViewAction(ViewEditor viewEditor) {
		super(viewEditor.getAdventureProjectModel());
		this.viewEditor=viewEditor;
	}

	@Override
	final public void actionPerformed(ActionEvent e) {
		actionPerformedOnView(e, viewEditor.getViewDesigner().getActiveMouseX(), viewEditor.getViewDesigner().getActiveMouseY(), viewEditor.getViewDesigner().getActiveMouseScreenX(), viewEditor.getViewDesigner().getActiveMouseScreenY());
	}
	
	public abstract void actionPerformedOnView(ActionEvent e, int x, int y,int screenX,int screenY);
}
