package com.inkus.infomancerforge.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.views.GobView;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

public class RemoveGobFromView extends BaseViewAction {
	private static final long serialVersionUID = 1L;
	
	private GobView gobView;
	@SuppressWarnings("unused")
	private AdventureProjectModel adventureProjectModel;

	public RemoveGobFromView(ViewEditor viewEditor,GobView gobReferance,AdventureProjectModel adventureProjectModel) {
		super(viewEditor);
		this.gobView=gobReferance;
		this.adventureProjectModel=adventureProjectModel;
		putValue(NAME, "Remove from View");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.ERASER_MEDIUM_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
	}

	@Override
	public void actionPerformedOnView(ActionEvent e, int x, int y, int sx, int sy) {
		viewEditor.getView().getGobs().remove(gobView);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				viewEditor.repaint();
			}
		});
	}
}
