package com.inkus.infomancerforge.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBProperty;
import com.inkus.infomancerforge.editor.gob.ViewEditor;
import com.inkus.infomancerforge.editor.wizards.NewGobInstanceWizard;

public class NewGobInstanceToView extends BaseViewAction {
	private static final long serialVersionUID = 1L;
	
	private GOBInstance gobInstance;
	private GOBProperty<GOBInstance> linkedIntoProperty;

	public NewGobInstanceToView(ViewEditor viewEditor, GOBInstance gobInstance, GOBProperty<GOBInstance> linkedIntoProperty) {
		super(viewEditor);
		this.gobInstance=gobInstance;
		this.linkedIntoProperty=linkedIntoProperty;
		putValue(NAME, "New GOBInstance");
		putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.CALENDAR_ADD_24, ImageUtilities.MENU_ICON_COLOR, ImageUtilities.MENU_ICON_SIZE));
		putValue(MNEMONIC_KEY, KeyEvent.VK_G);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK , true));
	}

	@Override
	public void actionPerformedOnView(ActionEvent e, int x, int y, int sx, int sy) {
		NewGobInstanceWizard wizard=new NewGobInstanceWizard(adventureProjectModel,viewEditor.getView(),x,y,sx,sy,gobInstance,linkedIntoProperty);
		wizard.setLocation(sx,sy);
	}
}
