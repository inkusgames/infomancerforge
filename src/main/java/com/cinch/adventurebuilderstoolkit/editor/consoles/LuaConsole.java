package com.cinch.adventurebuilderstoolkit.editor.consoles;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.swing.DockablePanel;

public class LuaConsole extends DockablePanel {
	private static final long serialVersionUID = 1L;

	private AdventureProjectModel adventureProjectModel;
	
	public LuaConsole(AdventureProjectModel adventureProjectModel) {
		super("Lua Console","console.lua",ImageUtilities.getSourceCodeIcon("lua", ImageUtilities.TAB_ICON_COLOR, ImageUtilities.TAB_ICON_SIZE));
		this.adventureProjectModel=adventureProjectModel;
		build();
	}
	
	private JComponent getConsoleDocument() {
		JTextArea console=new JTextArea(adventureProjectModel.getAdventureLuaEnviroment().getStdOutDocument());
		console.setEditable(false);
		JScrollPane js=new JScrollPane(console);
		
		return js;
	}
	
	private void build() {
		setLayout(new BorderLayout());
		
		add(getConsoleDocument(),BorderLayout.CENTER);

	}

}
