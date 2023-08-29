package com.cinch.adventurebuilderstoolkit.editor.swing;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.ComboPopup;

import com.cinch.adventurebuilderstoolkit.beans.NamedResource;
import com.cinch.adventurebuilderstoolkit.beans.gobs.GOB;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;
import snap.bean.ObjectMapper;
import snap.swing.CustomField;

/**
 * A class for filtered combo box.
 */
public class NamedResourceFilterComboBox<type extends NamedResource> extends JComboBox<type> implements CustomField {
	private static final long serialVersionUID = 1L;
	
	private static Set<Integer> navigationKeys;
	
	public enum ShowType {
		ShowAll(){
			public boolean canShow(NamedResource item) {
				return true;
			}
		},
		ShowNamedOnly(){
			public boolean canShow(NamedResource item) {
				return item.isNamed();
			}
		},
		ShowUnnamedOnly(){
			public boolean canShow(NamedResource item) {
				return !item.isNamed();
			}
		};
		
		public abstract boolean canShow(NamedResource item);
	}
	
	static {
		navigationKeys=new HashSet<>();
		navigationKeys.add(KeyEvent.VK_LEFT);
		navigationKeys.add(KeyEvent.VK_RIGHT);
		navigationKeys.add(KeyEvent.VK_UP);
		navigationKeys.add(KeyEvent.VK_DOWN);
		navigationKeys.add(KeyEvent.VK_TAB);
	}

	private FilterList<type> filtered;
	private String enteredText;
	private FilterMatcher filterMatcher; 
	private NamedMatcherEditor namedMatcherEditor;
	private JTextField textfield;
	private Matcher<NamedResource> additionalFilter;
	private ShowType showType=ShowType.ShowAll;
	private boolean excludeDefinitionOnly=false;

	public NamedResourceFilterComboBox(EventList<type> entries){
		this(entries,ShowType.ShowAll,false);
	}
	
	public NamedResourceFilterComboBox(EventList<type> entries,ShowType showType,boolean excludeDefinitionOnly){
		this.setEditable(true);
		this.showType=showType;
		this.excludeDefinitionOnly=excludeDefinitionOnly;
		setEntries(entries);
	}
	
	@SuppressWarnings("unchecked")
	public void setEntries(EventList<type> entries) {
		
		SortedList<type> sortedResources = new SortedList<>(entries,new Comparator<type>() {
			@Override
			public int compare(NamedResource o1, NamedResource o2) {
				return (o1!=null && o1.getName()!=null && o2!=null)?o1.getName().compareTo(o2.getName()):0;
			}
		});
		
		this.filtered=new FilterList<type>(sortedResources);
		DefaultEventComboBoxModel<type> gobComboModel=new DefaultEventComboBoxModel<type>(this.filtered);
		setModel(gobComboModel);
		setRenderer(new NamedResourceComboBoxRenderer());
		setEditor(new NamedResourceComboBoxEditor((EventList<NamedResource>) entries));
		
		filterMatcher=new FilterMatcher();
		namedMatcherEditor=new NamedMatcherEditor();
		this.filtered.setMatcher(filterMatcher);
		this.filtered.setMatcherEditor(namedMatcherEditor);
		textfield = (JTextField) this.getEditor().getEditorComponent();
		textfield.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				for (var fl:NamedResourceFilterComboBox.this.getFocusListeners()) {
					fl.focusLost(e);
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				for (var fl:NamedResourceFilterComboBox.this.getFocusListeners()) {
					fl.focusGained(e);
				}
			}
		});

		textfield.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ke){
				if (!navigationKeys.contains(ke.getExtendedKeyCode())) {
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							comboFilter(textfield.getText());
						}
					});
				}
			}
		});
	}
	
	public NamedResourceFilterComboBox(EventList<type> entries,Matcher<NamedResource> additionalFilter){
		this(entries);
		setAdditionalFilter(additionalFilter);
	}
	
	public Matcher<NamedResource> getAdditionalFilter() {
		return additionalFilter;
	}

	public void setAdditionalFilter(Matcher<NamedResource> additionalFilter) {
		this.additionalFilter = additionalFilter;
		comboFilter(enteredText);
	}

	@Override
	public void intoGui(String field, Serializable bean, JComponent component) {
		Runnable setSelected=new Runnable() {
			@Override
			public void run() {
				String value=(String)ObjectMapper.getFieldValue(field, bean);
				for (var n:filtered) {
					if (n.getUuid().equals(value)) {
						setSelectedItem(n);
						break;
					}
				}
			}
		};
		
		if (enteredText!=null && enteredText.length()>0) {
			enteredText=null;
			namedMatcherEditor.fireChange();
			SwingUtilities.invokeLater(setSelected);
		}else {
			setSelected.run();
		}
	}

	@Override
	public void intoBean(String field, Serializable bean, JComponent component) {
//		System.out.println ("textfield="+textfield.getText());
		if (textfield.getText().trim().length()==0) {
//			System.out.println ("selected="+null);
			ObjectMapper.setFieldValue(field, bean, null);
		} else {
			NamedResource selected=(NamedResource)getSelectedItem();
//			System.out.println ("selected="+selected);
			String value=selected==null?null:selected.getUuid();
			ObjectMapper.setFieldValue(field, bean, value);
		}
	}

	@Override
	public JComponent getComponent(String field, Serializable bean) {
		return this;
	}

	private void comboFilter(String enteredText) {
		
		this.enteredText=enteredText;
		int pos=textfield.getCaretPosition();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				namedMatcherEditor.fireChange();
				if (textfield.hasFocus()) {
					NamedResourceFilterComboBox.this.showPopup();
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							ComboPopup popup = (ComboPopup) NamedResourceFilterComboBox.this.getUI().getAccessibleChild(NamedResourceFilterComboBox.this, 0);
							((JComponent) popup).setPreferredSize(new Dimension(NamedResourceFilterComboBox.this.getWidth(),100));
							((JComponent) popup).setLayout(new GridLayout(1, 1));
							textfield.setText(enteredText);
							textfield.setCaretPosition(pos);
						}
					});
				}					
			}
		});
	}

	class FilterMatcher implements Matcher<NamedResource> {
		@Override
		public boolean matches(NamedResource item) {
			// TODO This should probably become part of the NamedResource interface instead
			if (excludeDefinitionOnly && item instanceof GOB gob) {
				if (gob.isDefinitionOnly()) {
					return false;
				}
			}
			
			if (!showType.canShow(item)) {
				return false;
			}
			// filter out additions filters first
			if (additionalFilter!=null) {
				if (!additionalFilter.matches(item)) {
					return false;
				}
			}
			
			if (enteredText==null || enteredText.length()==0) {
				return true;
			}
			return item.getName().toLowerCase().contains(enteredText.toLowerCase());
		}
		
	}
	
	class NamedMatcherEditor implements MatcherEditor<NamedResource> {
		List<Listener<NamedResource>> listeners=new ArrayList<>();

		public void fireChange() {
			Event<NamedResource> event=new Event<NamedResource>(this, Event.CHANGED, filterMatcher);
			for (var l:listeners) {
				l.changedMatcher(event);
			}
		}
		
		@Override
		public void addMatcherEditorListener(Listener<NamedResource> listener) {
			listeners.add(listener);
		}

		@Override
		public void removeMatcherEditorListener(Listener<NamedResource> listener) {
			listeners.remove(listener);
		}

		@Override
		public Matcher<NamedResource> getMatcher() {
			return filterMatcher;
		}
	}
	
	public static class NamedFilter implements Matcher<NamedResource>{
		private Set<String> excludeNames;
		private Set<String> includeNames;
		
		public NamedFilter(List<? extends NamedResource> excludeNames, List<? extends NamedResource> includeNames) {
			super();
			if (excludeNames!=null) {
				this.excludeNames = new HashSet<String>();
				for (var n:excludeNames) {
					this.excludeNames.add(n.getName());
				}
			}
			if (includeNames!=null) {
				this.includeNames = new HashSet<String>();
				for (var n:includeNames) {
					this.includeNames.add(n.getName());
				}
			}
		}

		@Override
		public boolean matches(NamedResource item) {
			boolean okay=true;
			
			if (okay && excludeNames!=null) {
				okay=!excludeNames.contains(item.getName());
			}
			if (okay && includeNames!=null) {
				okay=includeNames.contains(item.getName());
			}
			return okay;
		}
	}
}