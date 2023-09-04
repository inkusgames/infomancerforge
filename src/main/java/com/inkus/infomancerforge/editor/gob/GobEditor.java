package com.inkus.infomancerforge.editor.gob;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiFilledAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.data.DataInstance;
import com.inkus.infomancerforge.display.factories.GeneralTableCellEditor;
import com.inkus.infomancerforge.display.factories.GeneralTableCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.CellType;
import com.inkus.infomancerforge.display.factories.cells.ComboBoxCellEditor;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.DataInstanceChangeListener;
import com.inkus.infomancerforge.editor.FileGameObjectChangeListener;
import com.inkus.infomancerforge.editor.forms.GOBFormFactory;
import com.inkus.infomancerforge.editor.forms.GOBFormFactory.GOBForm;
import com.inkus.infomancerforge.editor.property.PropertyEditor;
import com.inkus.infomancerforge.editor.property.PropertyValues;
import com.inkus.infomancerforge.editor.property.gob.PropertyValuesGOBProperty;
import com.inkus.infomancerforge.editor.property.gob.PropertyValuesGobInstance;
import com.inkus.infomancerforge.editor.swing.DockablePanel;
import com.inkus.infomancerforge.editor.swing.EditTable;
import com.inkus.infomancerforge.editor.swing.EnhancedJTableHeader;
import com.inkus.infomancerforge.editor.swing.ToolbarButton;
import com.formdev.flatlaf.extras.components.FlatScrollPane;
import com.formdev.flatlaf.extras.components.FlatTabbedPane;
import com.formdev.flatlaf.extras.components.FlatTable;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.formdev.flatlaf.extras.components.FlatToolBar;

public class GobEditor extends DockablePanel implements FileGameObjectChangeListener,DataInstanceChangeListener {
	@SuppressWarnings("unused")
	static private final Logger log=LogManager.getLogger(GobEditor.class);
	private static final long serialVersionUID = 1L;
	
	private GOB gob;
	private GOB oldGOB=null;

	private FlatTable designTable;
	private GobTableModel gobTableModel;
	private PropertyEditor propertyEditor;
	private PropertyEditor dataPropertyEditor;
	private GOBForm gobForm;
	private FlatTable dataTable;
	private GOBDataTableModel gobDataTableModel;
	private JSplitPane split;
	
	private boolean fixingColumWidths=false;
	
	private RemovePropertyAction removePropertyAction;
	private MovePropertyUpAction movePropertyUpAction;
	private MovePropertyDownAction movePropertyDownAction;
	
	private RemoveDataAction removeDataAction;
	private ToggleProperties toggleProperties;
	
	private FlatTextField searchField;
	private AdventureProjectModel adventureProjectModel;
	
	public GobEditor(GOB gob,AdventureProjectModel adventureProjectModel) {
		super(gob.getName(),"gob."+gob.getUuid(),ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_20, ImageUtilities.TAB_ICON_COLOR, ImageUtilities.TAB_ICON_SIZE));
		setLayout(new BorderLayout());
		this.adventureProjectModel=adventureProjectModel;
		this.gob=gob;
		oldGOB=(GOB)SerializationUtils.clone(gob);
		
		adventureProjectModel.addFileGameObjectChangeListener(this);
		adventureProjectModel.addDataInstanceChangeListener(this);
		gobTableModel=new GobTableModel(gob);
		System.out.println("GOB:"+gob.getName()+" #:"+gob.getPropertyDefinitions().size());
		for (var p:gob.getPropertyDefinitions()) {
			System.out.println("GOBProperty:"+p.getName()+":"+p.getType());
		}
		
		build();
	}

	public String getTitle() {
		return gob.getName();
	}

	public void propertiedModelChanged() {
		gobTableModel.fireTableDataChanged();
	}
	
	// If the properties view is showing this instance we need to refresh the properties list
	@Override
	public void dataInstanceObjectChanged(Object source, DataInstance dataInstance) {
		if (source!=this) {
			updateInstancePropertiesSelection();
		}
	}
	
	private JComponent buildDesignTable() {
		designTable=new EditTable();
		designTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		designTable.setModel(gobTableModel);
		designTable.setRowSelectionAllowed(true);
		designTable.setColumnSelectionAllowed(false);
		designTable.getSelectionModel().addListSelectionListener(new GobTableSelectionListener());
		gobTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						//System.out.println("Table changed");
						changed();
					}
				});
			}
		});
		
		var tableEditor=new GeneralTableCellEditor();
		var values=GOBPropertyDefinition.Type.values();
		var enumEditor=new ComboBoxCellEditor<GOBPropertyDefinition.Type>(Arrays.copyOfRange(values,1,values.length),CellType.Normal);
		tableEditor.addColumnEditor(1,GOBPropertyDefinition.Type.class, enumEditor);
		tableEditor.install(designTable);
		
		var tableRenderer=new GeneralTableCellRenderer();
		tableRenderer.install(designTable);
		
		designTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		designTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		designTable.getColumnModel().getColumn(2).setPreferredWidth(80);
		designTable.getColumnModel().getColumn(3).setPreferredWidth(80);
		
		FlatScrollPane pane=new FlatScrollPane();
		pane.getViewport().add(designTable);
		return pane;
	}
	
	private JComponent buildDesignToolBar() {
		FlatToolBar toolBar=new FlatToolBar();
		
		ToolbarButton addRow=new ToolbarButton(new AddPropertyAction());
		addRow.addBindingTo(designTable);
		toolBar.add(addRow);
		
		toolBar.addSeparator();

		movePropertyUpAction=new MovePropertyUpAction();
		ToolbarButton moveRowUp=new ToolbarButton(movePropertyUpAction);
		moveRowUp.addBindingTo(designTable);
		toolBar.add(moveRowUp);
		
		movePropertyDownAction=new MovePropertyDownAction();
		ToolbarButton moveRowDown=new ToolbarButton(movePropertyDownAction);
		moveRowDown.addBindingTo(designTable);
		toolBar.add(moveRowDown);

		toolBar.addSeparator();
		
		removePropertyAction=new RemovePropertyAction();
		ToolbarButton removeRow=new ToolbarButton(removePropertyAction);
		removeRow.addBindingTo(designTable);
		toolBar.add(removeRow);
		return toolBar;
	}

	private JComponent buildDesignProperties() {
		propertyEditor=new PropertyEditor(adventureProjectModel);
		
		JPanel panel=new JPanel(new BorderLayout());
		panel.setMinimumSize(new Dimension(300,300));
		panel.setPreferredSize(new Dimension(300,300));
		
		panel.add(propertyEditor,BorderLayout.CENTER);
		
		return panel;
	}

	private JComponent buildDesignMain() {
		JPanel mainTable=new JPanel(new BorderLayout());
		mainTable.add(buildDesignTable(),BorderLayout.CENTER);
		mainTable.add(buildDesignProperties(),BorderLayout.EAST);
		mainTable.add(buildDesignToolBar(),BorderLayout.NORTH);
		
		return mainTable;
	}
	
	private JComponent buildDesign() {
		gobForm=new GOBFormFactory(adventureProjectModel,null).createGobForm(new GOB(),this);
		gobForm.setCurrentBean(gob);
		gobForm.updateBeanOnLostFocus();
		
		JPanel designPanel=new JPanel(new BorderLayout());
		designPanel.add(gobForm,BorderLayout.NORTH);
		designPanel.add(buildDesignMain(),BorderLayout.CENTER);
		return designPanel;
	}
	
	private JComponent buildDataBar() {
		FlatToolBar buttonBar=new FlatToolBar();
		ToolbarButton addData=new ToolbarButton(new AddDataAction());
		addData.addBindingTo(dataTable);
		buttonBar.add(addData);

		removeDataAction=new RemoveDataAction();
		ToolbarButton removeData=new ToolbarButton(removeDataAction);
		removeData.addBindingTo(dataTable);
		buttonBar.add(removeData);
		
		FlatToolBar searchBar=new FlatToolBar();
		
		searchField=new FlatTextField();
		searchField.setMinimumWidth(200);
		searchBar.add(searchField);
		
		FlatButton searchData=new FlatButton();
		searchData.setAction(new SearchDataAction());
		searchBar.add(searchData);
		
		searchBar.add(new JLabel(" Properties"));
		toggleProperties=new ToggleProperties();
		searchBar.add(toggleProperties);
		
		JPanel barPanel=new JPanel(new BorderLayout());
		barPanel.add(buttonBar,BorderLayout.WEST);
		barPanel.add(searchBar,BorderLayout.EAST);
		return barPanel;
	}
	
	private JComponent buildEmbeddedDataBar() {
		FlatToolBar buttonBar=new FlatToolBar();
		FlatButton cleanData=new FlatButton();
		cleanData.setAction(new CleanDataAction());
		buttonBar.add(cleanData);

		FlatToolBar searchBar=new FlatToolBar();
		
		searchField=new FlatTextField();
		searchField.setMinimumWidth(200);
		searchBar.add(searchField);
		
		FlatButton searchData=new FlatButton();
		searchData.setAction(new SearchDataAction());
		searchBar.add(searchData);
		
		searchBar.add(new JLabel(" Properties"));
		toggleProperties=new ToggleProperties();
		searchBar.add(toggleProperties);
		
		JPanel barPanel=new JPanel(new BorderLayout());
		barPanel.add(buttonBar,BorderLayout.WEST);
		barPanel.add(searchBar,BorderLayout.EAST);
		return barPanel;
	}

	private JComponent buildDataProperties() {
		dataPropertyEditor=new PropertyEditor(adventureProjectModel);
		
		JPanel panel=new JPanel(new BorderLayout());
		panel.setMinimumSize(new Dimension(300,300));
		panel.setPreferredSize(new Dimension(300,300));
		
		panel.add(dataPropertyEditor,BorderLayout.CENTER);
		
		return panel;
	}

	private JComponent buildDataTable() {
		dataTable=new EditTable();
		gobDataTableModel=adventureProjectModel.getGOBDataTableModel(gob);
		
		dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(gobDataTableModel));

		DefaultTableColumnModel dataTableColumnModel=new DefaultTableColumnModel();
		dataTableColumnModel.setColumnMargin(0);
		dataTable.setTableHeader(new EnhancedJTableHeader(dataTableColumnModel, dataTable));

		dataTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dataTable.setModel(gobDataTableModel);
		
		dataTable.setRowSelectionAllowed(true);
		dataTable.setColumnSelectionAllowed(false);
		dataTable.setAutoCreateColumnsFromModel(false);
		dataTable.setAutoCreateRowSorter(true);
		dataTable.setColumnModel(dataTableColumnModel);
		RowSorter<? extends TableModel> rowSorter = dataTable.getRowSorter();
		
		gobDataTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType()==TableModelEvent.UPDATE) {
					for (int t=dataTableColumnModel.getColumnCount()-1;t>=0;t--){
						dataTableColumnModel.removeColumn(dataTableColumnModel.getColumn(t));
					}
					for (int t=0;t<gobDataTableModel.getColumnCount();t++) {
						TableColumn tableColumn=new TableColumn(t,gobDataTableModel.getColumnWidth(t));
						tableColumn.setHeaderRenderer(new GOBInstanceTableHeaderRenderer(gob,gobDataTableModel.getColumnGOBPropertyDefinition(t),rowSorter));
						tableColumn.setHeaderValue(gobDataTableModel.getColumnName(t));
						dataTableColumnModel.addColumn(tableColumn);
					}
				}
			}
		});
		
		for (int t=0;t<gobDataTableModel.getColumnCount();t++) {
			TableColumn tableColumn=new TableColumn(t,gobDataTableModel.getColumnWidth(t));
			tableColumn.setHeaderRenderer(new GOBInstanceTableHeaderRenderer(gob,gobDataTableModel.getColumnGOBPropertyDefinition(t),rowSorter));
			tableColumn.setHeaderValue(gobDataTableModel.getColumnName(t));
			dataTableColumnModel.addColumn(tableColumn);
		}
		
		dataTableColumnModel.addColumnModelListener(new TableColumnModelListener() {
			
			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
			}
			
			@Override
			public void columnRemoved(TableColumnModelEvent e) {
			}
			
			@Override
			public void columnMoved(TableColumnModelEvent e) {
				if (e.getFromIndex()!=e.getToIndex()) {
					System.out.println("Move from "+e.getFromIndex()+" to "+e.getToIndex());
					var fromprop=gobDataTableModel.getColumnGOBPropertyDefinition(e.getFromIndex());
					var toprop=gobDataTableModel.getColumnGOBPropertyDefinition(e.getToIndex());
					if (gob.getPropertyDefinitions().contains(fromprop) && gob.getPropertyDefinitions().contains(toprop)) {
						gob.getPropertyDefinitions().remove(fromprop);
						int to=gob.getPropertyDefinitions().indexOf(toprop);
						if (e.getFromIndex()>e.getToIndex()) {
							gob.getPropertyDefinitions().add(to,fromprop);
						} else {
							gob.getPropertyDefinitions().add(to+1,fromprop);
						}
					}
					gobDataTableModel.swapColums(e.getFromIndex(),e.getToIndex());
				}
			}
			
			@Override
			public void columnMarginChanged(ChangeEvent e) {
				if (!fixingColumWidths) {
					for (int t=0;t<gobDataTableModel.getColumnCount();t++) {
						gobDataTableModel.getColumnGOBPropertyDefinition(t).setDisplayWidth(dataTable.getColumnModel().getColumn(t).getPreferredWidth());
					}
				}
			}
			
			@Override
			public void columnAdded(TableColumnModelEvent e) {
			}
		});
		
		gobDataTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						dataChanged();
					}
				});
			}
		});
		
		if (!gob.isDefinitionOnly()) {
			dataTable.getSelectionModel().addListSelectionListener(new DataTableSelectionListener());
		}
		
		var tableEditor=new GOBInstanceTableCellEditor(adventureProjectModel, gobDataTableModel);
		tableEditor.install(dataTable);
		
		var tableRenderer=new GOBInstanceTableCellRenderer(gobDataTableModel);
		tableRenderer.install(dataTable);
		
		FlatScrollPane pane=new FlatScrollPane();
		pane.getViewport().add(dataTable);
		return pane;
	}
	
	private void updatePropertiesSeen() {
		if (toggleProperties.isSelected()) {
			split.setDividerLocation(split.getWidth()-300);
		} else {
			split.setDividerLocation(split.getWidth());
		}
	}
	
	private JComponent buildData() {

		JPanel leftPanel=new JPanel(new BorderLayout());
		leftPanel.add(buildDataTable(),BorderLayout.CENTER);
		if (gob.isNamed()) {
			leftPanel.add(buildDataBar(),BorderLayout.NORTH);
		} else {
			leftPanel.add(buildEmbeddedDataBar(),BorderLayout.NORTH);
		}
		
		split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, buildDataProperties());
		split.setResizeWeight(1.0);
		split.setEnabled(false);
		split.setDividerSize(0);
		
		split.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				updatePropertiesSeen();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});


		return split;
	}

	public void changed() {
		if ((oldGOB!=null && !oldGOB.equals(gob)) || adventureProjectModel.getGOBDataTableModel(gob).getChangedCount()!=0) {
			if (oldGOB!=null && !oldGOB.equals(gob)) {
				gob.touch();
				oldGOB=(GOB)SerializationUtils.clone(gob);
				adventureProjectModel.markAllGOBChildrenAsTouched(this, gob);
			}
			adventureProjectModel.fireFileGameObjectChange(this,gob);
		} else {
			oldGOB=(GOB)SerializationUtils.clone(gob);
		}
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//				fixingColumWidths=true;
//				for (int t=0;t<gobDataTableModel.getColumnCount();t++) {
//					dataTable.getColumnModel().getColumn(t).setPreferredWidth(gobDataTableModel.getColumnWidth(t));
//				}
//				fixingColumWidths=false;
//
//			}
//		}); 

	}

	public void dataChanged() {
	}

	public void fileGameObjectChanged(Object source,FileGameObject fileGameObject) {
		if (source!=this && source!=propertyEditor && source!=gobDataTableModel) {
			gobTableModel.fireTableDataChanged();
			updatePropertiesSelection();
			if (fileGameObject==gob) {
				gobForm.refresh();
			}
		}
	}

	private void build() {
		FlatTabbedPane tabs=new FlatTabbedPane();
		tabs.setTabPlacement(FlatTabbedPane.BOTTOM);
		tabs.addTab("", ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_SETTINGS_24, ImageUtilities.TAB_ICON_COLOR, ImageUtilities.TAB_ICON_SIZE), buildDesign());
		if (!gob.isDefinitionOnly()) {
			tabs.addTab("", ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_EDIT_24, ImageUtilities.TAB_ICON_COLOR, ImageUtilities.TAB_ICON_SIZE), buildData());
		}
		
		tabs.addChangeListener(new TabsChangeListener());
		
		add(tabs,BorderLayout.CENTER);
	}
	
	private void updatePropertiesSelection() {
		if (designTable!=null) {
			int[] rows=designTable.getSelectedRows();
			
			List<PropertyValues> allProperties=new ArrayList<>();
			for (int r:rows) {
				if (r>=0 && r<gob.getPropertyDefinitions().size()) {
					var gp=gob.getPropertyDefinitions().get(r);
					allProperties.add(new PropertyValuesGOBProperty(adventureProjectModel,gp));
				}
			}
					
			propertyEditor.setPropertyValues(gob,allProperties);
		}
	}

	private void updateInstancePropertiesSelection() {
		System.out.println("updateInstancePropertiesSelection Start");
		if (dataTable!=null) {
			int[] rows=dataTable.getSelectedRows();
			
			List<PropertyValues> allProperties=new ArrayList<>();
			for (int r:rows) {
				allProperties.add(new PropertyValuesGobInstance(adventureProjectModel,gobDataTableModel.getRow(r)));
			}
			
			dataPropertyEditor.setPropertyValues(null,allProperties);
		}
	}

	class AddPropertyAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		AddPropertyAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_ROW_INSERT_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (designTable.isEditing()) {
				designTable.getCellEditor().stopCellEditing();
			}
			
			Set<String> currentFields=new HashSet<>();
			for (var d:gob.getPropertyDefinitions()) {
				currentFields.add(d.getGobFieldName());
			}
			
			String fieldName=StorageUtilities.getRandomName(10, currentFields);
			gob.getPropertyDefinitions().add(new GOBPropertyDefinition(fieldName));
			gobTableModel.fireTableRowsInserted(gob.getPropertyDefinitions().size()-1,gob.getPropertyDefinitions().size()-1);
			designTable.getSelectionModel().setSelectionInterval(gob.getPropertyDefinitions().size()-1, gob.getPropertyDefinitions().size()-1);
//			log.trace("GOB Changed by AddPropertyAction");
//			System.out.println("OldSize="+oldGOB.getPropertyDefinitions().size());
//			System.out.println("NewSize="+gob.getPropertyDefinitions().size());
			changed();
		}
	}

	class RemovePropertyAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		RemovePropertyAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_ROW_DELETE_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK));
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int[] selectedRows=designTable.getSelectedRows();
			if (selectedRows!=null && selectedRows.length>0) {
				if (designTable.isEditing()) {
					designTable.getCellEditor().stopCellEditing();
				}
				int lowestRow=-1;
				var rows=new ArrayList<GOBPropertyDefinition>();
				for (var r:selectedRows) {
					if (lowestRow<0 || lowestRow>r) {
						lowestRow=r;
					}
					rows.add(gob.getPropertyDefinitions().get(r));
				}
				gob.getPropertyDefinitions().removeAll(rows);
				for (var r:selectedRows) {
					gobTableModel.fireTableRowsDeleted(r,r);
				}
				if (lowestRow>=1) {
					designTable.getSelectionModel().setSelectionInterval(lowestRow-1, lowestRow-1);
				} else {
					designTable.clearSelection();
				}
				changed();
			}
		}
	}

	class MovePropertyDownAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		MovePropertyDownAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_MOVE_DOWN_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK));
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int[] selectedRows=designTable.getSelectedRows();
			
			if (selectedRows!=null && selectedRows.length>0) {
				int[] newSelectedRows=new int[selectedRows.length];
				Arrays.sort(selectedRows);
				if (designTable.isEditing()) {
					designTable.getCellEditor().stopCellEditing();
				}
				int last=gob.getPropertyDefinitions().size()-1;
				for (int t=selectedRows.length-1;t>=0;t--) {
					int pos=selectedRows[t];
					if (pos<last) {
						gob.getPropertyDefinitions().add(pos	,gob.getPropertyDefinitions().remove(pos+1));
						last=pos;
					} else {
						last=pos-1;
					}
					newSelectedRows[t]=last+1;
				}
				gobTableModel.fireTableRowsUpdated(selectedRows[0], Math.min(gob.getPropertyDefinitions().size()-1,selectedRows[selectedRows.length-1]));
				designTable.getSelectionModel().clearSelection();
				for (var i:newSelectedRows) {
					designTable.getSelectionModel().addSelectionInterval(i,i);
				}
				changed();
			}
		}
	}

	class MovePropertyUpAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		MovePropertyUpAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_MOVE_UP_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK));
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int[] selectedRows=designTable.getSelectedRows();
			
			if (selectedRows!=null && selectedRows.length>0) {
				int[] newSelectedRows=new int[selectedRows.length];
				Arrays.sort(selectedRows);
				if (designTable.isEditing()) {
					designTable.getCellEditor().stopCellEditing();
				}
				int last=0;
				for (int t=0;t<selectedRows.length;t++) {
					int pos=selectedRows[t];
					if (pos>last) {
						gob.getPropertyDefinitions().add(pos-1,gob.getPropertyDefinitions().remove(pos));
						last=pos;
					} else {
						last=pos+1;
					}
					newSelectedRows[t]=last-1;
				}
				gobTableModel.fireTableRowsUpdated(selectedRows[0]>1?selectedRows[0]-1:0, selectedRows[selectedRows.length-1]);
				designTable.getSelectionModel().clearSelection();
				for (var i:newSelectedRows) {
					designTable.getSelectionModel().addSelectionInterval(i,i);
				}
				changed();
			}
		}
	}
	
	class TabsChangeListener implements ChangeListener{
		public void stateChanged(ChangeEvent changeEvent) {
			if (designTable.isEditing()) {
				designTable.getCellEditor().stopCellEditing();
			}
			if (dataTable.isEditing()) {
				dataTable.getCellEditor().stopCellEditing();
			}
		}
	}
	
	class GobTableSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				removePropertyAction.setEnabled(designTable.getSelectedRowCount()>0);
				movePropertyUpAction.setEnabled(designTable.getSelectedRowCount()>0);
				movePropertyDownAction.setEnabled(designTable.getSelectedRowCount()>0);
				
				updatePropertiesSelection();
				changed();
			}
		}
	}

	class DataTableSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			System.out.println("valueChanged "+e.getValueIsAdjusting());
			if (!e.getValueIsAdjusting()) {
				if (removeDataAction!=null) {
					removeDataAction.setEnabled(dataTable.getSelectedRowCount()>0);
				}
				updateInstancePropertiesSelection();
			}
		}
	}

	class AddDataAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		AddDataAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_ROW_INSERT_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (dataTable.isEditing()) {
				dataTable.getCellEditor().stopCellEditing();
			}
			int pos=gobDataTableModel.addRow(-1);
			adventureProjectModel.fireDataInstanceChange(this, gobDataTableModel.getRow(pos));
			gobDataTableModel.fireTableRowsInserted(pos, pos);
			dataTable.setRowSelectionInterval(pos, pos);
		}
	}

	class RemoveDataAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		RemoveDataAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_ROW_DELETE_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK));
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int[] selectedRows=dataTable.getSelectedRows();
			if (selectedRows!=null && selectedRows.length>0) {
				if (dataTable.isEditing()) {
					dataTable.getCellEditor().stopCellEditing();
				}
				int lowestRow=-1;
				var rows=new ArrayList<GOBInstance>();
				for (var r:selectedRows) {
					if (lowestRow<0 || lowestRow>r) {
						lowestRow=r;
					}
					var gi=gobDataTableModel.getRow(r);
					rows.add(gi);
					adventureProjectModel.fireDataInstanceChange(this, gi);
				}
				gobDataTableModel.removeRows(rows);
				for (var r:selectedRows) {
					gobDataTableModel.fireTableRowsDeleted(r,r);
				}
				if (lowestRow>=1) {
					dataTable.getSelectionModel().setSelectionInterval(lowestRow-1, lowestRow-1);
				} else {
					dataTable.clearSelection();
				}
			}
		}
	}
	
	class CleanDataAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		CleanDataAction(){
			putValue(NAME,"Remove Orphaned Instances");
			putValue(SHORT_DESCRIPTION,"This will remove all instances that are no longer embeded in a Base instance.");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.TABLE_ROW_DELETE_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (adventureProjectModel.hasChangesToSave()) {
				JOptionPane.showMessageDialog((Component)e.getSource(), "This feature is only available is all data is saved first.", "Unable to proceed.", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			System.out.println("allGobs start");
			List<GOB> allGobs=adventureProjectModel.getGOBReferances(gob);
			System.out.println("allGobs="+allGobs.size());
			List<List<GOBPropertyDefinition>> allProps=new ArrayList<>();
			for (GOB g:allGobs) {
				allProps.add(adventureProjectModel.getAllGobProperties(g));
				System.out.println("allProps="+allGobs.size());
			}
			System.out.println("allPropsFinal="+allGobs.size());
			
			List<GOB> thisAndParentGobs=new ArrayList<>();
			thisAndParentGobs.add(gob);
			thisAndParentGobs.addAll(adventureProjectModel.getGOBParents(gob));
			
			List<GOBInstance> unusedInstances=adventureProjectModel.getDataModel().selectUnused(gob,thisAndParentGobs,adventureProjectModel.getAllGobProperties(gob),allGobs,allProps);
			for (GOBInstance g:unusedInstances) {
				System.out.println(g.getUuid());
			}
			
			if (unusedInstances.size()>0) {
				if (JOptionPane.showConfirmDialog((Component)e.getSource(), "Are you sure you want to delete the "+unusedInstances.size()+" unused instances?", "Confirm Delete", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
					if (dataTable.isEditing()) {
						dataTable.getCellEditor().stopCellEditing();
					}
					gobDataTableModel.removeRows(unusedInstances);
					gobDataTableModel.fireTableDataChanged();
				}
			}
		}
	}

	class SearchDataAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		SearchDataAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.SEARCH_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.BUTTON_ICON_SIZE));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			gobDataTableModel.search(searchField.getText());
		}
	}
	
	class ToggleProperties extends FlatToggleButton {
		private static final long serialVersionUID = 1L;

		ToggleProperties() {
			this.setButtonType(ButtonType.borderless);
			this.setSelectedIcon(ImageUtilities.getIcon(FluentUiFilledAL.CARET_RIGHT_24,
					ImageUtilities.TOOLBAR_ICON_COLOR, ImageUtilities.TOOL_ICON_SIZE));
			this.setIcon(ImageUtilities.getIcon(FluentUiRegularAL.CARET_LEFT_24,
					ImageUtilities.TOOLBAR_ICON_COLOR, ImageUtilities.TOOL_ICON_SIZE));
			this.setToolTipText("Toggles the properties editor panel.");
			this.setFocusable(false);

//			this.setSelected(view.isDrawGrid());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updatePropertiesSeen();
				}
			});
		}

	}


}
