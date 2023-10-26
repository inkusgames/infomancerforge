package com.inkus.infomancerforge.beans.views;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.actions.BaseViewAction;
import com.inkus.infomancerforge.editor.gob.ViewEditor;

public interface ViewDrawable extends Serializable {
	String getUuid();
	Rectangle bounds();
	boolean isVisable(Rectangle r);
	ViewDrawable findOver(Point p);
	void moveTo(int x,int y);
	void paintDrawable(AdventureProjectModel adventureProjectModel, Graphics2D g2,boolean isSelected,boolean isHighlighted,ViewEditor viewEditor);
	void recalcSize(AdventureProjectModel adventureProjectModel);
	public List<BaseViewAction> getActions(ViewEditor viewEditor, AdventureProjectModel adventureProjectModel);
	
	public RightDragHandler getRightDragHandler();
	
	Collection<? extends ViewDrawable> getChildren();
	
	public int getSortOrder();
	public int hashCode();
	public boolean equals(Object obj);
}
