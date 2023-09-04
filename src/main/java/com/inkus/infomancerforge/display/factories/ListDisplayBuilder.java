package com.inkus.infomancerforge.display.factories;

import java.io.Serializable;

import javax.swing.JPanel;

public abstract class ListDisplayBuilder<type extends Serializable > {

	@SuppressWarnings("unchecked")
	JPanel getDisplayRaw(Serializable t) {
		return getDisplay((type)t);
	}

	
	abstract protected JPanel getDisplay(type t);
}
