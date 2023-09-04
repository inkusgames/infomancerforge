package com.inkus.infomancerforge.editor.actions;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.views.ConnectorView;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

public class ShowAllGOBReferances extends BaseViewAction {
	private static final long serialVersionUID = 1L;
	
	private AdventureProjectModel adventureProjectModel;
	private ConnectorView connectorView;

	public ShowAllGOBReferances(ViewEditor viewEditor,ConnectorView connectorView,AdventureProjectModel adventureProjectModel) {
		super(viewEditor);
		
		this.adventureProjectModel=adventureProjectModel;
		this.connectorView=connectorView;
		putValue(NAME, "Show All");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.EYE_SHOW_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformedOnView(ActionEvent e, int x, int y, int sx, int sy) {
		connectorView.showAll(viewEditor, adventureProjectModel);

	}
}
