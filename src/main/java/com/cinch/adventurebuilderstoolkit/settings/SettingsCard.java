package com.cinch.adventurebuilderstoolkit.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.parser.TaskTagParser;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.Setting;
import com.cinch.adventurebuilderstoolkit.beans.SettingsBag;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatCheckBox;
import com.formdev.flatlaf.extras.components.FlatComboBox;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatTextField;

public class SettingsCard extends JPanel {
	private static final long serialVersionUID = 1L;
	static private final Logger log=LogManager.getLogger(SettingsCard.class);

	private SettingsBag settingsBag;
	private List<SettingEditor> allEditors=new ArrayList<>();
	private AdventureProjectModel adventureProjectModel;
	
	public SettingsCard(SettingsBag settingsBag,AdventureProjectModel adventureProjectModel) {
		super(new BorderLayout());
		this.settingsBag=settingsBag;
		this.adventureProjectModel=adventureProjectModel;
		build();
	}

	private JPanel buildGroup(String name,List<SettingEditor> editors) {
		JPanel groupPanel=new JPanel(new GridBagLayout());
		groupPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(name),
				BorderFactory.createEmptyBorder(0,10,5,10)
				));
		
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.ipady=5;
		gbc.gridy=0;
		for (var e:editors) {
			if (e.isLayoutHorizontal()) {
				gbc.gridx=0;
				gbc.weightx=1;
				gbc.gridwidth=1;
				gbc.fill=GridBagConstraints.NONE;
				gbc.anchor=GridBagConstraints.EAST;
				JComponent label=e.getSettingLabel();
				label.setMinimumSize(new Dimension(150,20));
				label.setPreferredSize(new Dimension(150,20));
				label.setMaximumSize(new Dimension(150,20));
				groupPanel.add(label,gbc);
				
				gbc.gridx=1;
				gbc.weightx=4;
				gbc.fill=GridBagConstraints.HORIZONTAL;
				gbc.anchor=GridBagConstraints.WEST;
				groupPanel.add(e.getSettingEditor(),gbc);
			} else {
				gbc.gridwidth=2;
				gbc.gridx=0;
				gbc.weightx=5;
				gbc.fill=GridBagConstraints.HORIZONTAL;
				gbc.anchor=GridBagConstraints.WEST;
				JComponent label=e.getSettingLabel();
				label.setMinimumSize(new Dimension(150,20));
				label.setPreferredSize(new Dimension(150,20));
				label.setMaximumSize(new Dimension(150,20));
				groupPanel.add(label,gbc);
				
				gbc.gridy++;
				gbc.weightx=5;
				gbc.fill=GridBagConstraints.HORIZONTAL;
				gbc.anchor=GridBagConstraints.WEST;
				groupPanel.add(e.getSettingEditor(),gbc);
			}
			
			gbc.gridy++;			
		}
		return groupPanel;
	}
	
	public void updateSettings() {
		for (var editor:allEditors) {
			editor.updateSettings();
		}
	}
	
	public void updateUi() {
		for (var editor:allEditors) {
			editor.updateUi();
		}
	}
	
	private void build() {
		JPanel groupsPanel=new JPanel(new GridBagLayout());
//		groupsPanel.setBackground(Color.green);
		groupsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
		
		Map<String,List<SettingEditor>> grouped=new HashMap<>();
		List<String> groupOrder=new ArrayList<>();
		
		// Group all settings
		for (Setting s:settingsBag.getSettings()) {
			String group=s.getGroup();
			if (group==null) {
				group="Settings";
			}
			System.out.println("Examining group "+group);
			if (!grouped.containsKey(group)) {
				grouped.put(group,new ArrayList<>());
			}
			
			
			SettingEditor editor=null;
			switch (s.getSettingType()) {
			case Boolean:
				editor=new BooleanSettingEditor(s);
				break;
			case Choice:
				editor=new ChoiceSettingEditor(s);
				break;
			case Function:
				editor=new FunctionSettingEditor(s);
				break;
			case String:
				editor=new StringSettingEditor(s);
			case Code:
				editor=new CodeSettingEditor(s);
			default:
				break;
			}
			if (editor!=null) {
				allEditors.add(editor);
				grouped.get(group).add(editor);
			}
			
			if (!groupOrder.contains(group)) {
				groupOrder.add(group);
			}
		}
		
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.weightx=1;
		gbc.weighty=0;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		for (String group:groupOrder) {
			System.out.println("Adding group "+group);
			
			groupsPanel.add(buildGroup(group, grouped.get(group)),gbc);
			gbc.gridy++;

			JComponent label=new JLabel();
			label.setMinimumSize(new Dimension(150,10));
			label.setPreferredSize(new Dimension(150,10));
			label.setMaximumSize(new Dimension(150,10));
			groupsPanel.add(label,gbc);
			gbc.gridy++;

		}
		
		gbc.weighty=100;
		gbc.fill=GridBagConstraints.BOTH;
		groupsPanel.add(new JPanel(),gbc);
		add(groupsPanel,BorderLayout.CENTER);
	}
	
	public interface SettingEditor {
		void updateSettings();
		void updateUi();
		boolean isLayoutHorizontal();
		JComponent getSettingEditor();
		JComponent getSettingLabel();
	}
	
	class StringSettingEditor extends FlatTextField implements SettingEditor {
		private static final long serialVersionUID = 1L;
		
		private Setting setting;
		
		StringSettingEditor(Setting setting){
			this.setting=setting;
			updateUi();
		}
		
		public boolean isLayoutHorizontal() {
			return true;
		}
		
		public JComponent getSettingEditor() {
			return this;
		}

		public JComponent getSettingLabel() {
			FlatLabel label=new FlatLabel();
			label.setText(setting.getLabel());
			return label;
		}
		
		@Override
		public void updateSettings() {
			setting.setValue(getText());
		}
		
		@Override
		public void updateUi() {
			setText((String)setting.getValue());
		}

	}
	
	class BooleanSettingEditor extends FlatCheckBox implements SettingEditor {
		private static final long serialVersionUID = 1L;
		
		private Setting setting;
		
		BooleanSettingEditor(Setting setting){
			this.setting=setting;
			setText(setting.getLabel());
			updateUi();
		}
		
		public boolean isLayoutHorizontal() {
			return true;
		}
		
		public JComponent getSettingEditor() {
			return this;
		}

		public JComponent getSettingLabel() {
			FlatLabel label=new FlatLabel();
			return label;
		}
		
		@Override
		public void updateSettings() {
			setting.setValue(isSelected());
		}
		
		@Override
		public void updateUi() {
			setSelected((Boolean)setting.getValue());
		}
	}
	
	class ChoiceSettingEditor extends FlatComboBox<String> implements SettingEditor {
		private static final long serialVersionUID = 1L;
		
		private Setting setting;
		
		public boolean isLayoutHorizontal() {
			return true;
		}
		
		ChoiceSettingEditor(Setting setting){
			this.setting=setting;
			setModel(new DefaultComboBoxModel<>(setting.getChoices().split(",")));
			updateUi();
		}
		
		public JComponent getSettingEditor() {
			return this;
		}

		public JComponent getSettingLabel() {
			FlatLabel label=new FlatLabel();
			label.setText(setting.getLabel());
			return label;
		}
		
		@Override
		public void updateSettings() {
			setting.setValue(getSelectedItem());
		}
		
		@Override
		public void updateUi() {
			setSelectedItem(setting.getValue());
		}
	}

	class FunctionSettingEditor extends FlatButton implements SettingEditor {
		private static final long serialVersionUID = 1L;
		
		FunctionSettingEditor(Setting setting){
			setAction(new ActionSetting(setting));
		}
		
		public boolean isLayoutHorizontal() {
			return true;
		}
		
		public JComponent getSettingEditor() {
			return this;
		}

		public JComponent getSettingLabel() {
			return new FlatLabel();
		}
		
		@Override
		public void updateSettings() {
		}
		
		@Override
		public void updateUi() {
		}

	}
	
	class CodeSettingEditor extends JPanel implements SettingEditor {
		private static final long serialVersionUID = 1L;
		
		private Setting setting;
		private RSyntaxTextArea syntaxTextArea;
		private RTextScrollPane textScrollPane;
		private ErrorStrip errorStrip;

		private static Theme theme=null;
		private static SpellingParser parser=null;
		
		CodeSettingEditor(Setting setting){
			super(new BorderLayout());
			this.setting=setting;
			build();
			updateUi();
		}
		
		private static void loadTheme() {
			if (theme==null) {
				try (InputStream themeInputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("themes/syntaxtheme.xml")){
					theme=Theme.load(themeInputStream);
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
		}
		
		public static void loadSpellingParser() {
			if (parser==null) {
				boolean usEnglish = true; // "false" will use British English
				try {
					parser = SpellingParser.createEnglishSpellingParser(new File("english_dic.zip"), usEnglish);
				} catch (IOException e1) {
					log.error(e1.getMessage(),e1); 
				} 
			}
		}

		private void build() {
			loadTheme();
			loadSpellingParser();
			syntaxTextArea = new RSyntaxTextArea(20, 60);
			syntaxTextArea.setSyntaxEditingStyle(setting.getCodeType());
//			if ("lua".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
//			} else if ("c#".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
//			} else if ("json".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
//			} else if ("java".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
//			} else if ("c".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
//			} else if ("cpp".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
//			} else if ("css".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
//			} else if ("html".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
//			} else if ("js".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
//			} else if ("md".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
//			} else if ("ts".equals(setting.getCodeType())) {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
//			} else {
//				syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
//			}
			syntaxTextArea.setCodeFoldingEnabled(true);
			
			syntaxTextArea.setCodeFoldingEnabled(true);
			syntaxTextArea.setPaintTabLines(true);
			syntaxTextArea.setBracketMatchingEnabled(true);
			syntaxTextArea.discardAllEdits();
//			syntaxTextArea.getDocument().addUndoableEditListener(this);
			syntaxTextArea.addParser(parser);
			syntaxTextArea.addParser(new TaskTagParser());

			if (theme!=null) {
				theme.apply(syntaxTextArea);
			}
			textScrollPane = new RTextScrollPane(syntaxTextArea);
			
			errorStrip=new ErrorStrip(syntaxTextArea);
			errorStrip.setShowMarkAll(true);
			errorStrip.setCaretMarkerColor(ImageUtilities.HIGHLIGHT_COLOR);
			
//			setMinimumSize(new Dimension(100, 400));
//			setPreferredSize(new Dimension(100, 400));
			System.out.println("setting.getCodeHeight()="+setting.getCodeHeight());
			setMinimumSize(new Dimension(100, setting.getCodeHeight()));
			setPreferredSize(new Dimension(100, setting.getCodeHeight()));
			
			add(textScrollPane,BorderLayout.CENTER);
		}
		
		public boolean isLayoutHorizontal() {
			return false;
		}
		
		public JComponent getSettingEditor() {
			return this;
		}

		public JComponent getSettingLabel() {
			FlatLabel label=new FlatLabel();
			label.setText(setting.getLabel());
			return label;
		}
		
		@Override
		public void updateSettings() {
			setting.setValue(syntaxTextArea.getText());
		}
		
		@Override
		public void updateUi() {
			syntaxTextArea.setText((String)setting.getValue());
		}

	}
	class ActionSetting extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		Setting setting;
		
		ActionSetting(Setting setting){
			this.setting=setting;
			putValue(NAME, setting.getLabel());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			adventureProjectModel.getAdventureLuaEnviroment().execute(setting.getLabel(),setting.getAction());
			updateUi();
		}
	}
	
}
