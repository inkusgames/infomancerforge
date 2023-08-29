package com.cinch.adventurebuilderstoolkit.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.views.GobView.ViewMode;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.gob.ViewEditor;

public class SetViewTypeGobInstance extends BaseViewAction {
	private static final long serialVersionUID = 1L;
	
	private String gobType;  
	private String gobInstance;
	private ViewMode viewMode;
	private AdventureProjectModel adventureProjectModel;

	public SetViewTypeGobInstance(ViewEditor viewEditor,String gobType, String gobInstance, ViewMode viewMode,AdventureProjectModel adventureProjectModel) {
		super(viewEditor);
		
		this.gobType=gobType;
		this.viewMode=viewMode;
		this.gobInstance=gobInstance;
		this.adventureProjectModel=adventureProjectModel;
		if (gobInstance!=null) {
			putValue(NAME, "View as "+viewMode.name());
		} else {
			putValue(NAME, "View All as "+viewMode.name());
		}
		putValue(SMALL_ICON, ImageUtilities.getIcon(viewMode.getIkon(), ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformedOnView(ActionEvent e, int x, int y, int sx, int sy) {
		for (var g:viewEditor.getView().getGobs()) {
			if (g.getGobReferance().getTypeUuid().equals(gobType) && (gobInstance==null || g.getGobReferance().getUuid().equals(gobInstance))){
				g.setViewMode(viewMode);
				g.recalcSize(adventureProjectModel);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						viewEditor.repaint();
					}
				});
			}
		}
	}
}
