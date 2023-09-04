package com.inkus.infomancerforge.display.factories;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.inkus.infomancerforge.beans.Project;
import com.inkus.infomancerforge.display.factories.builders.ListDisplayBuilderProject;

public class ListDisplayFactory {

	private Map<Class<? extends Serializable>, ListDisplayBuilder<?>> builders=new HashMap<>();
	
	public ListDisplayFactory(){
		builders.put(Project.class,new ListDisplayBuilderProject());
	}

	public JPanel getListDisplay(Serializable bean) {
		if (builders.containsKey(bean.getClass())) {
			return builders.get(bean.getClass()).getDisplayRaw(bean);
		}
		return null;
	}

}
