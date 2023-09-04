package com.inkus.infomancerforge.editor.gob;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer.SpecialValues;

public class GobTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	static final String[] names={"Property","Type","Array","Required","Width","Show"};
	static final Class<?>[] classes={String.class,Enum.class,Boolean.class,Boolean.class,Integer.class,Boolean.class};
	
	private GOB gob;
	
	public GobTableModel(GOB gob) {
		this.gob=gob;
	}

	@Override
	public int getRowCount() {
		return gob.getPropertyDefinitions().size();
	}

	@Override
	public int getColumnCount() {
		return 6;
	}
	
	@Override
	public String getColumnName(int column) {
		return names[column];
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return classes[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (gob.getType()==com.inkus.infomancerforge.beans.gobs.GOB.Type.Base && (columnIndex!=0 && columnIndex!=4 && gob.getPropertyDefinitions().get(rowIndex).getType()==Type.ID)) {
			return false;
		}
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		var property=gob.getPropertyDefinitions().get(rowIndex);
		
		switch (columnIndex) {
		case 0:
			if (aValue!=null && !aValue.equals(property.getName())) {
				if (aValue==SpecialValues.UnSet) {
					aValue=null;
				}
				property.setName((String)aValue);
				gob.touch();
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			break;
		case 1:
			if (aValue!=null && !aValue.equals(property.getType())) {
				if (aValue==SpecialValues.UnSet) {
					aValue=null;
				}
				property.setType((GOBPropertyDefinition.Type)aValue);
				gob.touch();
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			break;
		case 2:
			if (aValue!=null && !aValue.equals(property.isArray())) {
				if (aValue==SpecialValues.UnSet) {
					aValue=null;
				}
				property.setArray((Boolean)aValue);
				gob.touch();
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			break;
		case 3:
			if (aValue!=null && !aValue.equals(property.isRequired())) {
				if (aValue==SpecialValues.UnSet) {
					aValue=null;
				}
				property.setRequired((Boolean)aValue);
				gob.touch();
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			break;
		case 4:
			if (aValue!=null && !aValue.equals(property.getDisplayWidth())) {
				if (aValue==SpecialValues.UnSet) {
					aValue=null;
				}
				property.setDisplayWidth((Integer)aValue);
				gob.touch();
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			break;
		case 5:
			if (aValue!=null && !aValue.equals(property.isShowInTable())) {
				if (aValue==SpecialValues.UnSet) {
					aValue=null;
				}
				property.setShowInTable((Boolean)aValue);
				gob.touch();
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			break;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		var property=gob.getPropertyDefinitions().get(rowIndex);
		
		switch (columnIndex) {
		case 0:return property.getName();
		case 1:return property.getType();
		case 2:return property.isArray();
		case 3:return property.isRequired();
		case 4:return property.getDisplayWidth();
		case 5:return property.isShowInTable();
		}
		/*
		if (property.getType()==Type.Float) {
			switch (columnIndex) {
			case 4:return property.getMinFloat();
			case 5:return property.getMaxFloat();
			}
		}
		if (property.getType()==Type.Image) {
			switch (columnIndex) {
			case 4:return property.getMinInt();
			case 5:return property.getMaxInt();
			}
		}*/
		return 0;
	}

	public void removeRows(ArrayList<GOBInstance> rows) {
	}
	
}
