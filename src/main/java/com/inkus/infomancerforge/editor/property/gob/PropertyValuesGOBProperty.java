package com.inkus.infomancerforge.editor.property.gob;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.display.factories.cells.CellType;
import com.inkus.infomancerforge.display.factories.cells.ColorCellEditor;
import com.inkus.infomancerforge.display.factories.cells.ColorCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.ComboBoxNamedResourceCellEditor;
import com.inkus.infomancerforge.display.factories.cells.DoubleCellEditor;
import com.inkus.infomancerforge.display.factories.cells.DoubleCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.IntegerCellEditor;
import com.inkus.infomancerforge.display.factories.cells.IntegerCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.NamedResourceCellRenderer;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.property.PropertyValue;
import com.inkus.infomancerforge.editor.property.PropertyValues;

public class PropertyValuesGOBProperty extends PropertyValues {
	private GOBPropertyDefinition gobPropertyDefinition;
	private AdventureProjectModel adventureProjectModel;

	public PropertyValuesGOBProperty(AdventureProjectModel adventureProjectModel,GOBPropertyDefinition gobPropertyDefinition) {
		this.gobPropertyDefinition=gobPropertyDefinition;
		this.adventureProjectModel=adventureProjectModel;
	}

	@Override
	public List<PropertyValue> getValues() {
		var values=new ArrayList<PropertyValue>();
		switch (gobPropertyDefinition.getType()) {
		case Float:
			values.add(new PropertyValueFloatMin());
			values.add(new PropertyValueFloatMax());
			values.add(new PropertyValuePrecision());
			break;
		case Integer:
			values.add(new PropertyValueIntMin());
			values.add(new PropertyValueIntMax());
			break; 
		case GOB:
			values.add(new PropertyValueGOB());
			break;
		default:
			break;
		}
		values.add(new PropertyValueColor());
		
		return values;
	}

	class PropertyValueGOB extends PropertyValue {
		public PropertyValueGOB() {
			super("Gob Type","Gob Type", new ComboBoxNamedResourceCellEditor<GOB>(adventureProjectModel,GOB.class,CellType.Normal),new NamedResourceCellRenderer<GOB>(CellType.Normal),GOB.class);
		}

		@Override
		public Object getValue() {
			return adventureProjectModel.getNamedResourceByUuid(GOB.class, gobPropertyDefinition.getGobType());
		}

		@Override
		public void setValue(Object value) {
			if (value==null) {
				gobPropertyDefinition.setGobType(null);
			}else {
				gobPropertyDefinition.setGobType(((GOB)value).getUuid());
			}
		}
	}

	class PropertyValueColor extends PropertyValue {
		public PropertyValueColor() {
			super("Display Color","Display Color", new ColorCellEditor("Pick a View display Color",CellType.Normal),new ColorCellRenderer(CellType.Normal),Color.class);
		}

		@Override
		public Object getValue() {
			return gobPropertyDefinition.getColor();
		}

		@Override
		public void setValue(Object value) {
			gobPropertyDefinition.setColor((Color)value);
		}
	}

	class PropertyValueIntMin extends PropertyValue {
		public PropertyValueIntMin() {
			super("Min Int", "Range Int", new IntegerCellEditor(CellType.Normal),new IntegerCellRenderer(CellType.Normal),Integer.class);
		}

		@Override
		public Object getValue() {
			return gobPropertyDefinition.getMinInt();
		}

		@Override
		public void setValue(Object value) {
			gobPropertyDefinition.setMinInt((Integer)value);
		}
	}

	class PropertyValuePrecision extends PropertyValue {
		public PropertyValuePrecision() {
			super("Precision", "Range Float", new IntegerCellEditor(CellType.Normal),new IntegerCellRenderer(CellType.Normal),Integer.class);
		}

		@Override
		public Object getValue() {
			return gobPropertyDefinition.getPrecision();
		}

		@Override
		public void setValue(Object value) {
			gobPropertyDefinition.setPrecision((Integer)value);
		}
	}

	class PropertyValueIntMax extends PropertyValue {
		public PropertyValueIntMax() {
			super("Max Int","Range Int", new IntegerCellEditor(CellType.Normal),new IntegerCellRenderer(CellType.Normal),Integer.class);
		}

		@Override
		public Object getValue() {
			return gobPropertyDefinition.getMaxInt();
		}

		@Override
		public void setValue(Object value) {
			gobPropertyDefinition.setMaxInt((Integer)value);
		}
	}


	class PropertyValueFloatMin extends PropertyValue {
		public PropertyValueFloatMin() {
			super("Min Float","Range Float", new DoubleCellEditor(CellType.Normal),new DoubleCellRenderer(CellType.Normal),Double.class);
		}

		@Override
		public Object getValue() {
			return gobPropertyDefinition.getMinFloat();
		}

		@Override
		public void setValue(Object value) {
			gobPropertyDefinition.setMinFloat((Double)value);
		}
	}

	class PropertyValueFloatMax extends PropertyValue {
		public PropertyValueFloatMax() {
			super("Max Float","Range Float", new DoubleCellEditor(CellType.Normal),new DoubleCellRenderer(CellType.Normal),Double.class);
		}

		@Override
		public Object getValue() {
			return gobPropertyDefinition.getMaxFloat();
		}

		@Override
		public void setValue(Object value) {
			gobPropertyDefinition.setMaxFloat((Double)value);
		}
	}
}
