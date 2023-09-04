package com.inkus.infomancerforge.beans;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

public class ProjectConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Dimension editorWindowSize=null;
	private Point editorWindowPosition=null;
	private boolean maximized=false;
	
	public ProjectConfig() {
	}

	public Dimension getEditorWindowSize() {
		return editorWindowSize;
	}

	public void setEditorWindowSize(Dimension editorWindowSize) {
		this.editorWindowSize = editorWindowSize;
	}

	public Point getEditorWindowPosition() {
		return editorWindowPosition;
	}

	public void setEditorWindowPosition(Point editorWindowPosition) {
		this.editorWindowPosition = editorWindowPosition;
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}
	
}
