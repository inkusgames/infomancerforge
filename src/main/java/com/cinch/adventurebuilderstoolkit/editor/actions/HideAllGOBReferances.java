package com.cinch.adventurebuilderstoolkit.editor.actions;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.views.ConnectorView;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.gob.ViewEditor;

public class HideAllGOBReferances extends BaseViewAction {
	private static final long serialVersionUID = 1L;
	
	private AdventureProjectModel adventureProjectModel;
	private ConnectorView connectorView;

	public HideAllGOBReferances(ViewEditor viewEditor,ConnectorView connectorView,AdventureProjectModel adventureProjectModel) {
		super(viewEditor);
		
		this.adventureProjectModel=adventureProjectModel;
		this.connectorView=connectorView;
		putValue(NAME, "Hide All");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.EYE_HIDE_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformedOnView(ActionEvent e, int x, int y, int sx, int sy) {
		connectorView.hideAll(viewEditor, adventureProjectModel);
	}
}
