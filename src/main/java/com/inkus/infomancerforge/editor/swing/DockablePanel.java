package com.inkus.infomancerforge.editor.swing;

import javax.swing.Icon;
import javax.swing.JPanel;

public class DockablePanel extends JPanel  {
	private static final long serialVersionUID = 1L;

	private String title;
	private String id;
	private Icon icon;

	public DockablePanel(String title,String id,Icon icon) {
		super();
		this.title=title;
		this.id=id;
		this.icon=icon;
	}

	public String getPersistentId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Icon getIcon() {
		return icon;
	}
}
