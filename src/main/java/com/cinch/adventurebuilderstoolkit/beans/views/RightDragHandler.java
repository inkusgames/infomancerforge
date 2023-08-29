package com.cinch.adventurebuilderstoolkit.beans.views;

import java.util.List;

import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.actions.BaseViewAction;
import com.cinch.adventurebuilderstoolkit.editor.gob.ViewEditor;

public interface RightDragHandler {
	public ViewDrawable getDrawable();
	public boolean canDragOnto(AdventureProjectModel adventureProjectModel,ViewDrawable viewDrawable);
	public void dragOnto(AdventureProjectModel adventureProjectModel,ViewDrawable viewDrawable);
	// Items returned will show in a menu
	public List<BaseViewAction> draggedTo(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel,int x,int y, int vx,int vy, int sx,int sy);
}
 