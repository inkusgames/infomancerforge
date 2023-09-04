package com.inkus.infomancerforge.beans.views;

import java.util.List;

import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.actions.BaseViewAction;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

public interface RightDragHandler {
	public ViewDrawable getDrawable();
	public boolean canDragOnto(AdventureProjectModel adventureProjectModel,ViewDrawable viewDrawable);
	public void dragOnto(AdventureProjectModel adventureProjectModel,ViewDrawable viewDrawable);
	// Items returned will show in a menu
	public List<BaseViewAction> draggedTo(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel,int x,int y, int vx,int vy, int sx,int sy);
}
 