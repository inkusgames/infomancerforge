package com.cinch.adventurebuilderstoolkit.editor.property;

import java.util.Comparator;
import java.util.List;

import com.cinch.adventurebuilderstoolkit.display.factories.cells.GeneralCellEditor;
import com.cinch.adventurebuilderstoolkit.display.factories.cells.GeneralCellRenderer;

public abstract class PropertyValue {

	private String name;
	private String group;
	private GeneralCellEditor editor;
	private GeneralCellRenderer labelRenderer;
	private GeneralCellRenderer renderer;
	private Class<?> type; 
	
	public PropertyValue(String name,String group,GeneralCellEditor editor,GeneralCellRenderer renderer,Class<?> type) {
		this.name=name;
		this.group=group;
		this.editor=editor;
		this.type=type;
		this.renderer=renderer;
	}
	
	public PropertyValue(String name,String group,GeneralCellEditor editor,GeneralCellRenderer renderer,GeneralCellRenderer labelRenderer,Class<?> type) {
		this(name,group,editor,renderer,type);
		this.labelRenderer=labelRenderer;
	}

	public String getKey() {
		return group+"."+((name+"").toUpperCase());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public GeneralCellEditor getEditor() {
		return editor;
	}

	public void setEditor(GeneralCellEditor editor) {
		this.editor = editor;
	}
	
	public GeneralCellRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(GeneralCellRenderer renderer) {
		this.renderer = renderer;
	}

	public GeneralCellRenderer getLabelRenderer() {
		return labelRenderer;
	}

	public void setLabelRenderer(GeneralCellRenderer labelRenderer) {
		this.labelRenderer = labelRenderer;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public abstract Object getValue();
	public abstract void setValue(Object value);
	
	public static class KeyComparator implements Comparator<List<PropertyValue>> {
		@Override
		public int compare(List<PropertyValue> o1, List<PropertyValue> o2) {
			return o1.get(0).getKey().compareTo(o2.get(0).getKey());
		}
		
	}
}
