package com.inkus.infomancerforge.editor.property.gob;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBProperty;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.display.factories.cells.BooleanCellEditor;
import com.inkus.infomancerforge.display.factories.cells.BooleanCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.CellType;
import com.inkus.infomancerforge.display.factories.cells.ComboBoxNamedResourceCellEditor;
import com.inkus.infomancerforge.display.factories.cells.DoubleCellEditor;
import com.inkus.infomancerforge.display.factories.cells.DoubleCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellEditor;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.IntegerCellEditor;
import com.inkus.infomancerforge.display.factories.cells.IntegerCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.LabelCellNonEdit;
import com.inkus.infomancerforge.display.factories.cells.NamedResourceCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.StringCellEditor;
import com.inkus.infomancerforge.display.factories.cells.StringCellRenderer;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.property.PropertyValue;
import com.inkus.infomancerforge.editor.property.PropertyValues;

public class PropertyValuesGobInstance extends PropertyValues{
	static private final Logger log=LogManager.getLogger(PropertyValuesGobInstance.class);

	private AdventureProjectModel adventureProjectModel;
	private GOBInstance gobInstance;
	private GOB gob;
	private List<GOBPropertyDefinition> allProperties;
//	private boolean showFull=false;
	private List<PropertyValue> propertyValues=new ArrayList<>(); 

	public PropertyValuesGobInstance(AdventureProjectModel adventureProjectModel,GOBInstance gobInstance) {
		this.adventureProjectModel=adventureProjectModel;
//		this.showFull=showFull;
		this.gobInstance=gobInstance;
		gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstance.getGobType());
		allProperties=adventureProjectModel.getAllGobProperties(gob);
		
		buildValues();
	}
	
	private void changed() {
		adventureProjectModel.fireDataInstanceChange(this, gobInstance);
	}
	
	private void buildValues() {
		for (var p:allProperties) {
			System.out.println("Property for "+p.getName());
			GOBProperty<?> value=gobInstance.getProperty(p);
			// Add header array for list.


			boolean add=true;
			GOB typeof;
			if (p.getType()==Type.GOB && p.getGobType()!=null && p.getGobType().length()>0) {
				System.out.println("Get Type "+p.getGobType());
				typeof=adventureProjectModel.getNamedResourceByUuid(GOB.class, p.getGobType());
				add=typeof!=null && !typeof.isDefinitionOnly() && typeof.getType().isBase();
			}
			
			if (add) {

				if (p.isArray()) {
					List<?> values=(value.getValue() instanceof List<?>)?(List<?>)value.getValue():null;
					if (values==null) {
						values=new ArrayList<>();
						value.setValue(values);
					}

				
					propertyValues.add(new GobInstanceArraySize(p,values));
					for (int t=0;t<values.size() && t<1000;t++) {
						switch (p.getType()) {
	//						case Audio:
	//							break;
						case Boolean:
							propertyValues.add(new GobInstanceArrayPropertyValue<Boolean>(p,t,values,Boolean.class));
							break;
						case Float:
							propertyValues.add(new GobInstanceArrayPropertyValue<Double>(p,t,values,Double.class));
							break;
						case GOB:
//							if (p.getGobType()!=null && p.getGobType().length()>0) {
//								System.out.println("Get Type "+p.getGobType());
//								GOB typeof=adventureProjectModel.getNamedResourceByUuid(GOB.class, p.getGobType());
//								if (typeof!=null && !typeof.isDefinitionOnly() && typeof.getType().isBase()) {
							propertyValues.add(new GobInstanceArrayPropertyValue<GOBInstance>(p,t,values,GOBInstance.class));
//								}
//							}
							break;
						case ID:
							break;
	//						case Image:
	//							break;
						case Integer:
							propertyValues.add(new GobInstanceArrayPropertyValue<Integer>(p,t,values,Integer.class));
							break;
						case String:
							propertyValues.add(new GobInstanceArrayPropertyValue<String>(p,t,values,String.class));
							break;
						default:
							log.warn("Type "+p.getType()+" not catered for in properties editor.");
							break;
						}
					}
				} else {
					switch (p.getType()) {
	//					case Audio:
	//						break;
					case Boolean:
						propertyValues.add(new GobInstancePropertyValue<Boolean>(p,value,Boolean.class));
						break;
					case Float:
						propertyValues.add(new GobInstancePropertyValue<Double>(p,value,Double.class));
						break;
					case GOB:
//						if (p.getGobType()!=null && p.getGobType().length()>0) {
//							GOB typeof=adventureProjectModel.getNamedResourceByUuid(GOB.class, p.getGobType());
//							if (typeof!=null && !typeof.isDefinitionOnly() && typeof.getType().isBase()) {
						propertyValues.add(new GobInstancePropertyValue<GOBInstance>(p,value,GOBInstance.class));
//							}
//						}
	
						break;
					case ID:
						propertyValues.add(new GobInstancePropertyValue<String>(p,value,String.class));
						break;
	//					case Image:
	//						break;
					case Integer:
						propertyValues.add(new GobInstancePropertyValue<Integer>(p,value,Integer.class));
						break;
					case String:
						propertyValues.add(new GobInstancePropertyValue<String>(p,value,String.class));
						break;
					default:
						log.warn("Type "+p.getType()+" not catered for in properties editor.");
						break;
					}
				}
			}
		}
	}
	
	private GeneralCellEditor getGeneralCellEditor(GOBPropertyDefinition gobPropertyDefinition) {
		CellType type=gobPropertyDefinition.isArray()?CellType.ArrayItem:CellType.Normal;
		GeneralCellEditor generalCellEditor=new LabelCellNonEdit(type);
		switch (gobPropertyDefinition.getType()) {
//		case Audio:
//			break;
		case Boolean:
			generalCellEditor=new BooleanCellEditor(type);
			break;
		case Float:
			generalCellEditor=new DoubleCellEditor(type,gobPropertyDefinition.getPrecision());
			break;
		case GOB:
			generalCellEditor=new ComboBoxNamedResourceCellEditor<GOBInstance>(adventureProjectModel,GOBInstance.class,gobPropertyDefinition.getGobType(),type);
			break;
		case ID:
			generalCellEditor=new StringCellEditor(type);
			break;
//		case Image:
//			break;
		case Integer:
			generalCellEditor=new IntegerCellEditor(type);
			break;
		case String:
			generalCellEditor=new StringCellEditor(type);
			break;
		default:
			log.warn("Type "+gobPropertyDefinition.getType()+" not catered for in properties editor.");
			break;
		}
		if (gobPropertyDefinition.isArray()) {
			//generalCellEditor=new ArrayCellEditorWrapper(generalCellEditor);
		}
		return generalCellEditor;
	}

	private GeneralCellRenderer getGeneralCellRenderer(GOBPropertyDefinition gobPropertyDefinition) {
		CellType type=gobPropertyDefinition.isArray()?CellType.ArrayItem:CellType.Normal;
		GeneralCellRenderer generalCellRenderer=new StringCellRenderer(type);

		switch (gobPropertyDefinition.getType()) {
//		case Audio:
//			break;
		case Boolean:
			generalCellRenderer=new BooleanCellRenderer(type);
			break;
		case Float:
			generalCellRenderer= new DoubleCellRenderer(type,gobPropertyDefinition.getPrecision());
			break;
		case GOB:
			generalCellRenderer= new NamedResourceCellRenderer<>(type);
			break;
		case ID:
			generalCellRenderer= new StringCellRenderer(type);
			break;
//		case Image:
//			break;
		case Integer:
			generalCellRenderer= new IntegerCellRenderer(type);
			break;
		case String:
			generalCellRenderer= new StringCellRenderer(type);
			break;
		default:
			log.warn("Type "+gobPropertyDefinition.getType()+" not catered for in properties editor.");
			break;
		}
		if (gobPropertyDefinition.isArray()) {
			//generalCellRenderer=new ArrayCellRendererWrapper(generalCellRenderer);
		}
		return generalCellRenderer; 
	}
	@Override
	public List<PropertyValue> getValues() {
		return propertyValues;
	}
	
	class GobInstancePropertyValue<type> extends PropertyValue {
		@SuppressWarnings("unused")
		private GOBPropertyDefinition gobPropertyDefinition;
		private GOBProperty<type> value;
		
		@SuppressWarnings("unchecked")
		public GobInstancePropertyValue(GOBPropertyDefinition gobPropertyDefinition,GOBProperty<?> value, Class<type> forClass) {
			super(gobPropertyDefinition.getName(),gobPropertyDefinition.getName(), getGeneralCellEditor(gobPropertyDefinition),getGeneralCellRenderer(gobPropertyDefinition),forClass);
			this.gobPropertyDefinition=gobPropertyDefinition;
			this.value=(GOBProperty<type>)value;
		}

		@Override
		public Object getValue() {
			return value.getValue();
		}

		@Override
		public void setValue(Object value) {
			this.value.setValue(value);
			changed();
		}
	}

	class GobInstanceArraySize extends PropertyValue {
		@SuppressWarnings("unused")
		private GOBPropertyDefinition gobPropertyDefinition;
		private List<?> values;
		
		public GobInstanceArraySize(GOBPropertyDefinition gobPropertyDefinition,List<?> values) {
			super(gobPropertyDefinition.getName(),gobPropertyDefinition.getName(), new IntegerCellEditor(CellType.ArrayStart),new IntegerCellRenderer(CellType.ArrayStart),Double.class);
			this.gobPropertyDefinition=gobPropertyDefinition;
			this.values=(List<?>)values;
		}

		@Override
		public Object getValue() {
			return values.size();
		}

		@Override
		public void setValue(Object value) {
			if (value instanceof Number number) {
				int newSize=number.intValue();
				if (newSize!=this.values.size()) {
					if (newSize<this.values.size()) {
						while (this.values.size()>newSize && this.values.size()>0) {
							this.values.remove(this.values.size()-1);
						}
					} else if (newSize>this.values.size()) {
						while (this.values.size()<newSize) {
							this.values.add(null);
						}
					}
					buildValues();
					changed();
				}
			}
		}
	}
	
	public class GobInstanceArrayPropertyValue<type> extends PropertyValue {
		@SuppressWarnings("unused")
		private GOBPropertyDefinition gobPropertyDefinition;
		private List<type> values;
		private int pos;
		
		@SuppressWarnings("unchecked")
		public GobInstanceArrayPropertyValue(GOBPropertyDefinition gobPropertyDefinition,int pos,List<?> values, Class<type> forClass) {
			super(gobPropertyDefinition.getName()+"["+ImageUtilities.formatNumberWithLead(pos,values!=null?((List<?>)values).size():0)+"]",gobPropertyDefinition.getName(), getGeneralCellEditor(gobPropertyDefinition),getGeneralCellRenderer(gobPropertyDefinition),forClass);
			this.gobPropertyDefinition=gobPropertyDefinition;
			this.values=(List<type>)values;
			this.pos=pos;
		}

		@Override
		public Object getValue() {
			return values.get(pos);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setValue(Object value) {
			this.values.set(pos, (type)value);
			changed();
		}
		
		public List<type> getArray(){
			return values;
		}
		
		public void changed(){
			PropertyValuesGobInstance.this.changed();
		}
	}
}
