package com.inkus.infomancerforge.editor.property;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.AbstractCellEditor;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import com.formdev.flatlaf.extras.components.FlatScrollPane;
import com.formdev.flatlaf.extras.components.FlatTable;
import com.formdev.flatlaf.ui.FlatTableCellBorder;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellEditor;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer;
import com.inkus.infomancerforge.display.factories.cells.GeneralCellRenderer.SpecialValues;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.property.gob.PropertyValuesGobInstance.GobInstanceArrayPropertyValue;
import com.inkus.infomancerforge.editor.swing.Reorderable;

public class PropertyEditor extends JPanel {
	private static final long serialVersionUID = 1L;

	private FlatTable table;
	private FileGameObject fileGameObject;
	//	private DataInstance dataInstance;

	private List<List<PropertyValue>> common=new ArrayList<>();
	private PropertyEditorTableModel propertyEditorTableModel;
	private AdventureProjectModel adventureProjectModel;

	public PropertyEditor(AdventureProjectModel adventureProjectModel) {
		super(new BorderLayout());
		this.adventureProjectModel=adventureProjectModel;
		build();
	}

	public void setPropertyValues(FileGameObject fileGameObject, List<PropertyValues> allProperties) {
		this.fileGameObject=fileGameObject;
		this.setPropertyValues(allProperties);
	}
	//	
	//	public void setPropertyValues(DataInstance dataInstance, List<PropertyValues> allProperties) {
	//		this.dataInstance=dataInstance;
	//		this.setPropertyValues(allProperties);
	//	}
	//	
	public void setPropertyValues(List<PropertyValues> allProperties) {
		Map<String,Class<?>> commonFields=new LinkedHashMap<>();
		Map<String,List<PropertyValue>> commonLists=new LinkedHashMap<>();
		common.clear();

		System.out.println("Prop Start");
		if (allProperties.size()>0) {
			// Build first list
			for (var p:allProperties.get(0).getValues()) {
				System.out.println("Prop:"+p.getKey());
				commonFields.put(p.getKey(),p.getClass());
				List<PropertyValue> el=new ArrayList<PropertyValue>();
				el.add(p);
				commonLists.put(p.getKey(),el);
			}
			// Compare first list to each of the other lists
			for (int t=1;t<allProperties.size();t++) {
				Map<String,Class<?>> rowFields=new HashMap<>();
				// Test each property against the standing list
				for (var p:allProperties.get(t).getValues()) {
					if (commonFields.containsKey(p.getKey())) {
						System.out.println("Prop Common:"+p.getKey());

						rowFields.put(p.getKey(),p.getClass());
						commonLists.get(p.getKey()).add(p);
					}
				}

				for (var k:commonFields.keySet()) {
					if (!rowFields.containsKey(k)) {
						System.out.println("Prop Remove 1:"+k);
						commonLists.remove(k);
					} else if (!rowFields.get(k).equals(commonFields.get(k))){
						System.out.println("Prop Remove 2:"+k);
						commonLists.remove(k);
					}
				}
			}
		}
		common=new ArrayList<>(commonLists.values());
		common.sort(new PropertyValue.KeyComparator());
		//		common.sort(new Comparator<List<PropertyValue>>() {
		//			@Override
		//			public int compare(List<PropertyValue> o1, List<PropertyValue> o2) {
		//				return o1.get(0).getKey().compareTo(o2.get(0).getKey());
		//			}
		//			
		//		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				propertyEditorTableModel.fireTableStructureChanged();
				table.getColumnModel().getColumn(0).setPreferredWidth(80);
				table.getColumnModel().getColumn(1).setPreferredWidth(200);
				table.getTableHeader().setReorderingAllowed(false);
			}
		});
	}

	private void build() {
		propertyEditorTableModel=new PropertyEditorTableModel();

		table=new FlatTable();
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setModel(propertyEditorTableModel);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);
		table.setDragEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setTransferHandler(new TableRowTransferHandler(table)); 

		table.setDefaultEditor(Object.class, new PropertiesTableEditor());
		table.setDefaultRenderer(Object.class, new PropertiesTableRenderer());

		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);

		FlatScrollPane pane=new FlatScrollPane();
		pane.getViewport().add(table);
		add(pane,BorderLayout.CENTER);
	}

	public void change() {
		if (fileGameObject!=null) {
			adventureProjectModel.fireFileGameObjectChange(this ,fileGameObject);
		}
	}

	private class PropertyEditorTableModel extends AbstractTableModel implements Reorderable{
		private static final long serialVersionUID = 1L;

		@Override
		public int getRowCount() {
			//			System.out.println("Rows="+common.size());
			return common.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			if (column==0) {
				return "Property";
			}
			return "Value";
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex==1;
		}

		@Override
		public void reorder(int fromIndex, int toIndex, int size) {
			if (size==1) {
				var p1=common.get(fromIndex).get(0);
				var p2=common.get(toIndex).get(0);
				if (p1 instanceof GobInstanceArrayPropertyValue<?> p1g && p2 instanceof GobInstanceArrayPropertyValue<?> p2g) {
					if (p1g.getArray()==p2g.getArray()) {
						int nf=p1g.getArray().indexOf(common.get(fromIndex).get(0).getValue());
						int nt=p1g.getArray().indexOf(common.get(toIndex).get(0).getValue());
						
						for (int t=0;t<common.get(fromIndex).size();t++) {
							GobInstanceArrayPropertyValue<?> array=(GobInstanceArrayPropertyValue<?>)common.get(fromIndex).get(0);
							
							int d=nt<nf?-1:1;
							for (int n=nf;n!=nt;n=n+d) {
								Collections.swap(array.getArray(),n,n+d);
							}
						}
						fireTableRowsUpdated(Math.min(nf,nt), Math.max(nf,nt));
						
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								for (int t=0;t<common.get(fromIndex).size();t++) {
									GobInstanceArrayPropertyValue<?> array=(GobInstanceArrayPropertyValue<?>)common.get(fromIndex).get(0);
									array.changed();
								}
							}
						});
					}
				}
			}
		}

		@Override
		public boolean reorderable(int fromIndex, int toIndex, int size) {
			boolean can=false;
			if (size==1) {
				var p1=common.get(fromIndex).get(0);
				var p2=common.get(toIndex).get(0);
				if (p1 instanceof GobInstanceArrayPropertyValue<?> p1g && p2 instanceof GobInstanceArrayPropertyValue<?> p2g) {
					can=p1g.getArray()==p2g.getArray();
				}
			}
			return can;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex==0) {
				//				System.out.println("Name="+common.get(rowIndex).get(0).getName());
				return common.get(rowIndex).get(0).getName();
			} else {
				// TODO: Find a better way to show that values don't match then empty
				Object val1=common.get(rowIndex).get(0).getValue();
				for (int t=1;t<common.get(rowIndex).size();t++) {
					Object val2=common.get(rowIndex).get(t).getValue();
					if ((val1==null && val2!=null) || (val1!=null && !val1.equals(val2))) {
						val1=GeneralCellRenderer.SpecialValues.Mixed;
						break;
					}
				}
				return val1;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// We set to null on UnSet when all values were the same or there was only one value
			if (getValueAt(rowIndex, columnIndex)!=SpecialValues.Mixed && aValue==SpecialValues.UnSet) {
				aValue=null;
			}

			if (columnIndex==1 && aValue!=SpecialValues.UnSet) {
				for (var r:common.get(rowIndex)) {
					// Test that this value has in fact changed
					Object oValue=r.getValue();
					if ((aValue==null && oValue!=null) || (aValue!=null && !aValue.equals(oValue))) {
						change();
						r.setValue(aValue);
					}
				}
			}
		}
	}

	private class PropertiesTableEditor extends AbstractCellEditor implements TableCellEditor {
		private static final long serialVersionUID = 1L;

		private GeneralCellEditor currentEditor=null;


		@Override
		public Object getCellEditorValue() {
			return currentEditor!=null?currentEditor.getCellEditorValue():null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			currentEditor=common.get(row).get(0).getEditor();
			return currentEditor.getTableCellEditorComponent(table, value, isSelected);
		}

	}

	private class PropertiesTableRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JComponent component;
			if (column==0) {
				//				if (common.get(row).get(0).getLabelRenderer()!=null) {
				//					component=common.get(row).get(0).getLabelRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				//					JComponent baseComponent=(JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				//					component.setBackground(baseComponent.getBackground());
				//					if (hasFocus) {
				//						component.setBorder(new FlatTableCellBorder.Focused());
				//					} else if (isSelected) {
				//						component.setBorder(new FlatTableCellBorder.Selected());
				//					} else {
				//						component.setBorder(new FlatTableCellBorder.Default());
				//					}
				//					if (hasFocus && column==0) table.changeSelection(row,column+1,false,false);
				//				}else {
				component=(JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				//				}
			}else {
				component=common.get(row).get(0).getRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JComponent baseComponent=(JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				component.setBackground(baseComponent.getBackground());
				if (hasFocus) {
					component.setBorder(new FlatTableCellBorder.Focused());
				} else if (isSelected) {
					component.setBorder(new FlatTableCellBorder.Selected());
				} else {
					component.setBorder(new FlatTableCellBorder.Default());
				}
				if (hasFocus && column==0) table.changeSelection(row,column+1,false,false);
			}
			return component;
		}
	}
	/**
	 * Handles drag & drop row reordering
	 */
	public class TableRowTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;
		private final DataFlavor localObjectFlavor=new ActivationDataFlavor(Integer.class, "application/x-java-Integer;class=java.lang.Integer", "Integer Row Index");
		private JTable table=null;

		public TableRowTransferHandler(JTable table) {
			this.table = table;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			assert (c == table);
			return new DataHandler((int)table.getSelectedRow(), localObjectFlavor.getMimeType());
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {
			boolean b=false;
			JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
			int index = dl.getRow();
			int max = table.getModel().getRowCount();
			if (index < 0 || index >= max) {
				index = max-1;
			}
			try {
				Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
				if (rowFrom != -1 && rowFrom != index) {
					b=((Reorderable)table.getModel()).reorderable(rowFrom, index, 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			b = b && info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
			table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
			return b;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY_OR_MOVE;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			JTable target = (JTable) info.getComponent();
			JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
			int index = dl.getRow();
			int max = table.getModel().getRowCount();
			if (index < 0 || index > max) {
				index = max;
			}
			target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			try {
				Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
				if (rowFrom != -1 && rowFrom != index) {
					((Reorderable)table.getModel()).reorder(rowFrom, index, 1);
					if (index > rowFrom) {
						index--;
					}
					target.getSelectionModel().addSelectionInterval(index, index);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void exportDone(JComponent c, Transferable t, int act) {
			if ((act == TransferHandler.MOVE) || (act == TransferHandler.NONE)) {
				table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

	}
}
