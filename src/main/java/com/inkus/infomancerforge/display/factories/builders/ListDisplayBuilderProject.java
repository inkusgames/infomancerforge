package com.inkus.infomancerforge.display.factories.builders;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatLabel.LabelType;
import com.inkus.infomancerforge.beans.Project;
import com.inkus.infomancerforge.display.factories.ListDisplayBuilder;
import com.formdev.flatlaf.extras.components.FlatTextArea;

public class ListDisplayBuilderProject extends ListDisplayBuilder<Project> {

	private JPanel panel=new JPanel(new BorderLayout()); 
	private FlatLabel name=new FlatLabel(); 
	private FlatTextArea description=new FlatTextArea();
	
	public ListDisplayBuilderProject(){
		name.setLabelType(LabelType.h1);
		description.setOpaque(false);
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setFocusable(false);
		description.setPreferredSize(new Dimension(10,50));
		
		panel.add(name,BorderLayout.NORTH);
		panel.add(description,BorderLayout.CENTER);
	}
	
	@Override
	protected JPanel getDisplay(Project t) {
		name.setText(t.getName());
		description.setText(t.getDescription());
		return panel;
	}

}
