package com.inkus.infomancerforge.beans.gobs;

import java.awt.Color;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class GOBPropertyDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		ID(String.class,true),
		String(String.class,true),
		Integer(Integer.class,true),
		Float(Double.class,true),
		Boolean(Boolean.class,false),
		GOB(String.class,false);
//		Image(String.class,false),
//		Audio(String.class,false);
		
		private Class<?> myClass;
		private boolean canBeUsedForDisplay;
		
		private Type(Class<?> myClass,boolean canBeUsedForDisplay){
			this.myClass=myClass;
			this.canBeUsedForDisplay=canBeUsedForDisplay;
		}
		
		public Class<?> getMyClass(){
			return myClass;
		}
		
		public boolean canBeUsedForDisplay() {
			return canBeUsedForDisplay;
		}
		
	}

	private String name="";
	private Type type=Type.String;
	private boolean array=false;
	private boolean isRequired=false;

	private Integer minInt=null;
	private Integer maxInt=null;
	private Double minFloat=null;
	private Double maxFloat=null;
	private Integer precision=null;
	private String gobType;
	
	private String gobFieldName;
	private Color color;

	private boolean showInTable=true;
	private Integer displayWidth=100;
	
	public GOBPropertyDefinition() {
	}
	
	public GOBPropertyDefinition(String gobFieldName) {
		this.gobFieldName=gobFieldName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Integer getMinInt() {
		return minInt;
	}

	public void setMinInt(Integer minInt) {
		this.minInt = minInt;
	}

	public Integer getMaxInt() {
		return maxInt;
	}

	public void setMaxInt(Integer maxInt) {
		this.maxInt = maxInt;
	}

	public Double getMinFloat() {
		return minFloat;
	}

	public void setMinFloat(Double minFloat) {
		this.minFloat = minFloat;
	}

	public Double getMaxFloat() {
		return maxFloat;
	}

	public void setMaxFloat(Double maxFloat) {
		this.maxFloat = maxFloat;
	}

	public String getGobFieldName() {
		return gobFieldName;
	}

	public void setGobFieldName(String gobFieldName) {
		this.gobFieldName = gobFieldName;
	}

	public String getGobType() {
		return gobType;
	}

	public void setGobType(String gobTypeName) {
		this.gobType = gobTypeName;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Integer getDisplayWidth() {
		return displayWidth;
	}

	public void setDisplayWidth(Integer displayWidth) {
		this.displayWidth = displayWidth;
	}

	public boolean isShowInTable() {
		return showInTable;
	}

	public void setShowInTable(boolean showInTable) {
		this.showInTable = showInTable;
	}

	public Integer getPrecision() {
		if (precision==null) {
			precision=2;
		}
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	@Override
	public int hashCode() {
		return Objects.hash(array, color, displayWidth, gobFieldName, gobType, isRequired, maxFloat, maxInt, minFloat,
				minInt, name, showInTable, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GOBPropertyDefinition other = (GOBPropertyDefinition) obj;
		return array == other.array && Objects.equals(color, other.color)
				&& Objects.equals(displayWidth, other.displayWidth) && Objects.equals(gobFieldName, other.gobFieldName)
				&& Objects.equals(gobType, other.gobType) && isRequired == other.isRequired
				&& Objects.equals(maxFloat, other.maxFloat) && Objects.equals(maxInt, other.maxInt)
				&& Objects.equals(minFloat, other.minFloat) && Objects.equals(minInt, other.minInt)
				&& Objects.equals(name, other.name) && showInTable == other.showInTable && type == other.type;
	}

	public static class NameComparator implements Comparator<GOBPropertyDefinition> {
		@Override
		public int compare(GOBPropertyDefinition o1, GOBPropertyDefinition o2) {
			if (o1==null) {
				return -1;
			}
			if (o2==null) {
				return 1;
			}
			return o1.getName().compareTo(o2.getName());
		}
	}

	
}
