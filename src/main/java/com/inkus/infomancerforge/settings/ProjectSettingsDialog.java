package com.inkus.infomancerforge.settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatTree;
import com.inkus.infomancerforge.beans.SettingsBag;
import com.inkus.infomancerforge.editor.AdventureProjectModel;

public class ProjectSettingsDialog extends JDialog implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;

	private Map<String,SettingsCard> cards=new HashMap<>();
	private List<String> orderedCards=new ArrayList<>();
	private FlatTree list;
	private JPanel center;
	private CardLayout cardLayout;
	private AdventureProjectModel adventureProjectModel;
	
	public ProjectSettingsDialog(Frame owner,AdventureProjectModel adventureProjectModel) {
		super(owner,"Project Settings");
		this.adventureProjectModel=adventureProjectModel;
		setSize(1000,600);
		setResizable(false);
		setLocationRelativeTo(owner);
		setModal(true);
		build();
		setVisible(true);
	}
	
	private JComponent buildConfigList() {
		list=new FlatTree();
		list.setRootVisible(false);
		list.setModel(new ConfigListModel(orderedCards));
		list.setMinimumSize(new Dimension(200,150));
		list.setPreferredSize(new Dimension(200,150));
		list.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		list.getSelectionModel().addTreeSelectionListener(this);
		
		return new JScrollPane(list);
	}
	
	private JComponent buildCenter() {
		return center; 
	}

	private void buildCards() {
		Map<String,SettingsBag> settingsBags=adventureProjectModel.getSettingsModel().getSettings();
		
		cardLayout=new CardLayout();
		center=new JPanel(cardLayout);
		center.add(new JPanel(),"_");

		System.out.println("Build configs");
		for (String set:settingsBags.keySet()) {
			System.out.println("Build config "+set);
			SettingsCard settingsCard=new SettingsCard(settingsBags.get(set),adventureProjectModel);
			cards.put(set,settingsCard);
			orderedCards.add("root."+set);
			center.add(settingsCard,set);
		}
		orderedCards.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		
	}
	
	private JComponent buildButtons() {
		JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		FlatButton cancelButton=new FlatButton();
		cancelButton.setAction(new CancelSettings());
		buttons.add(cancelButton);
		
		FlatButton saveButton=new FlatButton();
		saveButton.setAction(new SaveSettings());
		buttons.add(saveButton);

		return buttons;
	}
	
	private void build() {
		buildCards();

		JPanel basePanel=new JPanel(new BorderLayout());
		basePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		basePanel.add(buildConfigList(),BorderLayout.WEST);
		basePanel.add(buildCenter(),BorderLayout.CENTER);
		basePanel.add(buildButtons(),BorderLayout.SOUTH);
		setContentPane(basePanel);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
//				list.getSelectionModel().setSelectionPath();
			}
		});
	}

//	@Override
//	public void valueChanged(ListSelectionEvent e) {
//		if (!e.getValueIsAdjusting()) {
//			System.out.println("Show setting card "+list.getSelectedValue());
//			cardLayout.show(center, list.getSelectedValue());
//		}
//	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		var path=list.getSelectionModel().getSelectionPath();
		if (path!=null) {
			String selection=((TreeSettingsElement)path.getLastPathComponent()).element;
			System.out.println("Show setting card "+selection);
			cardLayout.show(center, selection);
		}
	}

	private class ConfigListModel implements TreeModel {
		private TreeSettingsElement root;

		ConfigListModel(List<String> elements){
			root=new TreeSettingsElement("root","root", elements);
		}
		
		@Override
		public Object getRoot() {
			return root;
		}

		@Override
		public Object getChild(Object parent, int index) {
			return ((TreeSettingsElement)parent).children.get(index);
		}

		@Override
		public int getChildCount(Object parent) {
			return ((TreeSettingsElement)parent).children.size();
		}

		@Override
		public boolean isLeaf(Object node) {
			return ((TreeSettingsElement)node).children.size()==0;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			return ((TreeSettingsElement)parent).children.indexOf(child);
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
		}

	}

	class SaveSettings extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SaveSettings() {
			putValue(NAME, "Save");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			for (var card:cards.values()) {
				card.updateSettings();
			}
			ProjectSettingsDialog.this.setVisible(false);
			adventureProjectModel.getSettingsModel().saveSettings();
		}
		
	}
	
	class CancelSettings extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CancelSettings() {
			putValue(NAME, "Cancel");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			ProjectSettingsDialog.this.setVisible(false);
		}
	}
	
	class TreeSettingsElement {
		String element;
		String text;
		List<TreeSettingsElement> children=new ArrayList<>();
		
		TreeSettingsElement(String text,String element,List<String> childrenList){
			Set<String> done=new HashSet<>();

			String mybase=element+".";
			for (var child:childrenList) {
				if (child.startsWith(mybase)) {
					int end=child.indexOf(".", mybase.length());
					if (end==-1) {
						end=child.length();
					}
					String subChild=child.substring(mybase.length(),end);
					if (!done.contains(subChild)) {
						done.add(subChild);
						children.add(new TreeSettingsElement(subChild,mybase+subChild,childrenList));
					}
				}
			}
			
			this.text=text;
			this.element=mybase.substring("root.".length());
			if (this.element.endsWith(".")) {
				this.element=this.element.substring(0,this.element.length()-1);
			}
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
}
