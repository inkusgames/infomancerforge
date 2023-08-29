package com.cinch.adventurebuilderstoolkit.editor.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.formdev.flatlaf.extras.components.FlatLabel;

public class CountLabel extends FlatLabel {
	private static final long serialVersionUID = 1L;

	private Color color;
    private RenderingHints hints;
	
	public CountLabel(Color color) {
		super.setLabelType(LabelType.small);
        this.hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.color=color;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (getText()!=null && getText().length()>0) {
			int x=0;
			int y=0;
			int width=getWidth();
			int height=getHeight();
	        Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHints(hints);
	        g2.setColor(color);
	        g2.fillRoundRect(x,
	                         y+2,
	                         width,
	                         height-3,
	                         height-2,
	                         height-2);
		}
		super.paintComponent(g);
	}

}
