package com.inkus.infomancerforge.editor.gob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.SerializationUtils;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.data.DataInstance;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.DataInstanceChangeListener;
import com.inkus.infomancerforge.editor.FileGameObjectChangeListener;
import com.inkus.infomancerforge.editor.gob.search.SearchString;

public class GOBDataTableModel extends AbstractTableModel implements FileGameObjectChangeListener, DataInstanceChangeListener{
	private static final long serialVersionUID = 1L;
	
	private GOB gob;
	private GOB oldGOB;
	private AdventureProjectModel adventureProjectModel;
	private List<GOBPropertyDefinition> allProperties;
	private List<GOBPropertyDefinition> colProperties;
	private List<GOBPropertyDefinition> colTypeProperties;
	private Map<String, GOBInstance> data=new LinkedHashMap<>();
	private List<String> dataNaturalList=new ArrayList<>();
	private List<String> dataOrderedList=new ArrayList<>();
	
	private Set<String> changedRecords=new HashSet<>();
	private Set<String> deletedRecords=new HashSet<>();
	private Set<String> newRecords=new HashSet<>();
	
	private boolean dataLoaded=false;

	public GOBDataTableModel(AdventureProjectModel adventureProjectModel,GOB gob) {
		this.adventureProjectModel=adventureProjectModel;
		this.gob=gob;
		this.oldGOB=(GOB)SerializationUtils.clone(gob);
		adventureProjectModel.addDataInstanceChangeListener(this);
		adventureProjectModel.addFileGameObjectChangeListener(this);
	}
	
	private void init() {
		if (allProperties==null) {
			synchronized (this) {
				if (allProperties==null) {
					allProperties=adventureProjectModel.getAllGobProperties(gob);
					adventureProjectModel.addFileGameObjectChangeListener(this);
					adventureProjectModel.getDataModel().updateStructure(gob, allProperties);
					
					if (!dataLoaded) {
						for (var g:adventureProjectModel.getDataModel().selectAll(gob, allProperties)) {
							data.put(g.getUuid(), g);
							dataOrderedList.add(g.getUuid());
							dataNaturalList.add(g.getUuid());
						}
						dataLoaded=true;
					}
					
					colProperties=new ArrayList<>();
					colTypeProperties=new ArrayList<>();
					allProperties.forEach((a)->{
						boolean showCol=!a.isArray() && a.isShowInTable();
						System.out.println("Adding "+showCol+" 10 "+a.getName());
						if (showCol && a.getType()==Type.GOB) {
							System.out.println("Adding 20 "+a.getName());
							GOB propertyGob=adventureProjectModel.getNamedResourceByUuid(GOB.class, a.getGobType());
							if (propertyGob==null || !propertyGob.isNamed()) {
								System.out.println("Adding 30 "+a.getName());
								showCol=false;
							}
						}
						if (showCol) {
							System.out.println("Adding 40 "+a.getName());
							colProperties.add(a);
							colTypeProperties.add(a);
						}
					});
				}				
			}
		}
	}
	
	public void search(String search) {
		// Break search into components
		SearchString searchString=new SearchString(search);
		
		dataOrderedList.clear();
		if (search==null || search.trim().length()==0) {
			dataOrderedList.addAll(dataNaturalList);
		} else {
			for (var g:dataNaturalList) {
				var gi=data.get(g);
				if (searchString.testSearch(gi)) {
					dataOrderedList.add(g);
				}
			}
		}
		fireTableDataChanged();
	}
	
	public void refresh() {
		allProperties=null;
		init();
	}

	public void swapColums(int from,int to) {
		//colProperties.get(columnIndex).getType().getMyClass();
		if (from>to) {
			var o=colTypeProperties.remove(from);
			colTypeProperties.add(to,o);
		} else {
			var o=colTypeProperties.remove(from);
			colTypeProperties.add(to,o);
		}
	}
	
	@Override
	public int getRowCount() {
		init();
		return dataOrderedList.size();
	}

	@Override
	public int getColumnCount() {
		init();
		return colProperties!=null?colProperties.size():0;
	}

	public int getColumnWidth(int col) {
		return colProperties.get(col).getDisplayWidth();
	}

	public GOBPropertyDefinition getColumnGOBPropertyDefinition(int columnIndex) {
		return colTypeProperties.get(columnIndex);
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		init();
		return colProperties.get(columnIndex).getName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		init();
//		System.out.println("col["+columnIndex+"]("+colProperties.get(columnIndex).getType().getMyClass()+")");
		return colTypeProperties.get(columnIndex).getType().getMyClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		init();
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		init();
		var r=getRow(rowIndex).getProperties().get(colProperties.get(columnIndex).getGobFieldName());
		return r!=null?r.getValue():null;
	}

	public int addRow(int pos) {
		var gobInstance=new GOBInstance();
		gobInstance.setGobType(gob.getUuid());
		return addRow(pos,gobInstance);
	}
	
	public int addRow(GOBInstance g) {
		return addRow(getRowCount(),g);
	}
	
	public int addRow(int pos,GOBInstance g) {
		init();
		if (pos<0 || pos>=getRowCount()) {
			pos=getRowCount();
		}
		//var g=new GOBInstance();
		dataOrderedList.add(pos,g.getUuid());
		data.put(g.getUuid(), g);
		newRecords.add(g.getUuid());
		return pos;
	}

	public GOBInstance getRow(int pos) {
		init();
		if (pos>=0 && pos<getRowCount()) {
			return data.get(dataOrderedList.get(pos));
		}
		return null;
	}

	public List<GOBInstance> getRows() {
		init();
		List<GOBInstance> list=new ArrayList<>();
		for (int t=0;t<getRowCount();t++) {
			list.add(data.get(dataOrderedList.get(t)));
		}
		return list;
	}

	public GOBInstance getRowByUUID(String uuid) {
		init();
		return data.get(uuid);
	}

	public void changeRowByUUID(String uuid) {
		if (!newRecords.contains(uuid)) {
			changedRecords.add(uuid);
		}
	}
	
	public void removeRows(List<GOBInstance> rows) {
		init();
		for (var g:rows) {
			if (!newRecords.contains(g.getUuid())) {
				deletedRecords.add(g.getUuid());
				changedRecords.remove(g.getUuid());
			} else {
				newRecords.remove(g.getUuid());
			}
			data.remove(g.getUuid());
			dataOrderedList.remove(g.getUuid());
		}
	}
	
	public int getChangedCount() {
		return changedRecords.size()+deletedRecords.size()+newRecords.size();
	}

	public Set<String> getNewRecords() {
		return newRecords;
	}
	
	public Set<String> getDeletedRecords() {
		return deletedRecords;
	}
	
	public Set<String> getChangedRecords() {
		return changedRecords;
	}
	
	public void saved() {
		newRecords.clear();
		changedRecords.clear();
		deletedRecords.clear();
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		init();
		
		var g=getRow(rowIndex);
		var r=g.getProperty(colProperties.get(columnIndex));
		if (!Objects.equals(r.getValue(), aValue)) {
			r.setValue(aValue);
			if (!newRecords.contains(g.getUuid())) {
				changedRecords.add(g.getUuid());
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
		adventureProjectModel.fireDataInstanceChange(this, g);
	}

	@Override
	public void fileGameObjectChanged(Object source, FileGameObject fileGameObject) {
		System.out.println("fileGameObjectChanged 10");
		if (fileGameObject instanceof GOB eventGob && source!=this) {
			System.out.println("fileGameObjectChanged 20");
			if (eventGob==gob || adventureProjectModel.isParentOf(eventGob, gob)) {
				System.out.println("fileGameObjectChanged 30");
				if (!eventGob.equals(oldGOB)) {
					System.out.println("fileGameObjectChanged 40");
					this.oldGOB=(GOB)SerializationUtils.clone(gob);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							System.out.println("fileGameObjectChanged 50");
							refresh();
							fireTableStructureChanged();
						}
					});
				}
			}
		}
	}

	@Override
	public void dataInstanceObjectChanged(Object source, DataInstance dataInstance) {
		if (source!=this && dataInstance instanceof GOBInstance gobInstance) {
			if (gobInstance.getGobType().equals(gob.getUuid())) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (dataInstance instanceof GOBInstance g) {
							int rowIndex=getRows().indexOf(g);
							if (rowIndex!=-1) {
								if (!newRecords.contains(g.getUuid())) {
									changedRecords.add(g.getUuid());
								}
								fireTableRowsUpdated(rowIndex,rowIndex);
							}
						}
					}
				});
			}
		}
	}
//
//	public GOBDataTableColumnModel getColumnModel() {
//		return new GOBDataTableColumnModel();
//	}
//	
//	public class GOBDataTableColumnModel extends DefaultTableColumnModel {
//		public GOBDataTableColumnModel() {
//		}
//	}
//
//	public class GOBDataTableColumn extends TableColumn{
//		private int col;
//		
//		GOBDataTableColumn(int col){
//			this.col=col;
//		}
//		
//		
//		
//	}
	
}
