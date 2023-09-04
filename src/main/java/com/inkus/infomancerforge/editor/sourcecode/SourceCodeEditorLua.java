package com.inkus.infomancerforge.editor.sourcecode;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice.Level;
import org.fife.ui.rsyntaxtextarea.parser.TaskTagParser;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.sourcecode.SourceCode;
import com.inkus.infomancerforge.beans.sourcecode.SourceCodeErrorListener;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;

public class SourceCodeEditorLua extends SourceCodeEditor implements FocusListener, SourceCodeErrorListener {
	static private final Logger log=LogManager.getLogger(SourceCodeEditorLua.class);
	private static final long serialVersionUID = 1L;

	private RSyntaxTextArea syntaxTextArea;
	private RTextScrollPane textScrollPane;
	private ErrorStrip errorStrip;
	private LuaExecuteErrorParser luaExecuteErrorParser;
	private LuaErrorParser luaErrorParser;
	private DefaultCompletionProvider defaultProvider;
	private AutoCompletion autoCompletion;

	private static Theme theme=null;
	private static SpellingParser parser=null;

	public SourceCodeEditorLua(SourceCode sourceCode,AdventureProjectModel adventureProjectModel) {
		super(sourceCode,adventureProjectModel);
		build();
		refresh();
		adventureProjectModel.getAdventureLuaEnviroment().addSourceCodeErrorListener(this);
	}

	public AdventureProjectModel getAdventureProjectModel() {
		return adventureProjectModel;
	}

	public String getTitle() {
		return sourceCode.getName();
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}

	public void changed() {
		if (sourceCode.hasChanges()) {
			adventureProjectModel.fireFileGameObjectChange(sourceCode,sourceCode);
		}
	}

	private void refresh() {
		//		viewDesigner.refresh();
	}

	public void fileGameObjectChanged(Object source,FileGameObject fileGameObject) {
		if (source!=this && fileGameObject instanceof SourceCode sourceCode && sourceCode==this.sourceCode) {
			syntaxTextArea.setText(sourceCode.getCode());
		}
	}

	@Override
	public void errorsUpdated(SourceCode sourceCode) {
		if (sourceCode==this.sourceCode) {
			System.out.println("Updating errors");
			luaExecuteErrorParser.result=new DefaultParseResult(luaExecuteErrorParser);
	
			if (sourceCode.getErrors()!=null && sourceCode.getErrors().size()>0) {
	//			int lineCount=syntaxTextArea.getLineCount();
				
				for (var e:sourceCode.getErrors()) {
					ErrorNotice errorNotice = new ErrorNotice(luaExecuteErrorParser, e.getDescription(), e.getLineNumber()-1);
					//ErrorNotice errorNotice = new ErrorNotice(luaErrorParser, e.getDescription(), e.getLineNumber(), e.getColumn(), e.getLength());
					errorNotice.setLevel(Level.ERROR);
					errorNotice.setShowInEditor(true);
	//				lineCount=e.getLineNumber();
					luaExecuteErrorParser.result.addNotice(errorNotice);
				}
	//			luaExecuteErrorParser.result.setParsedLines(0, lineCount);
				
			}
			syntaxTextArea.forceReparsing(luaExecuteErrorParser);
		// TODO Auto-generated method stub
		}		
	}

	public void buildAutocomplete() {
		defaultProvider=new DefaultCompletionProvider() {
			protected boolean isValidChar(char ch) {
				return Character.isLetterOrDigit(ch) || ch=='_' || ch=='.' || ch=='_';
			}
		};

		List<Completion> completions=new ArrayList<>();

		completions.add(new BasicCompletion(defaultProvider, "and"));
		completions.add(new BasicCompletion(defaultProvider, "break"));
		completions.add(new BasicCompletion(defaultProvider, "do"));
		completions.add(new BasicCompletion(defaultProvider, "else"));
		completions.add(new BasicCompletion(defaultProvider, "elseif"));
		completions.add(new BasicCompletion(defaultProvider, "end"));
		completions.add(new BasicCompletion(defaultProvider, "false"));
		completions.add(new BasicCompletion(defaultProvider, "for"));
		completions.add(new BasicCompletion(defaultProvider, "function"));
		completions.add(new BasicCompletion(defaultProvider, "if","if statement.","<pre style=\"margin: 0; line-height: 125%\"><span style=\"color: #008800; font-weight: bold\">if</span> i <span style=\"color: #333333\">==</span> <span style=\"color: #0000DD; font-weight: bold\">1</span> <span style=\"color: #008800; font-weight: bold\">then</span>\n"+
				"   <span style=\"color: #007020\">print</span>(<span style=\"background-color: #fff0f0\">&quot;One&quot;</span>)\n"+
				"<span style=\"color: #008800; font-weight: bold\">elseif</span> i<span style=\"color: #333333\">==</span><span style=\"color: #0000DD; font-weight: bold\">2</span> <span style=\"color: #008800; font-weight: bold\">then</span>\n"+
				"   <span style=\"color: #007020\">print</span>(<span style=\"background-color: #fff0f0\">&quot;Two&quot;</span>)\n"+
				"<span style=\"color: #008800; font-weight: bold\">else</span>\n"+
				"   <span style=\"color: #007020\">print</span>(i)\n"+
				"<span style=\"color: #008800; font-weight: bold\">end</span>\n"+
				"</pre>"
				));
		completions.add(new BasicCompletion(defaultProvider, "in"));
		completions.add(new BasicCompletion(defaultProvider, "ipairs"));
		completions.add(new BasicCompletion(defaultProvider, "local"));
		completions.add(new BasicCompletion(defaultProvider, "nil"));
		completions.add(new BasicCompletion(defaultProvider, "not"));
		completions.add(new BasicCompletion(defaultProvider, "or"));
		completions.add(new BasicCompletion(defaultProvider, "pairs"));
		completions.add(new BasicCompletion(defaultProvider, "print"));
		completions.add(new BasicCompletion(defaultProvider, "repeat"));
		completions.add(new BasicCompletion(defaultProvider, "require"));
		completions.add(new BasicCompletion(defaultProvider, "return"));
		completions.add(new BasicCompletion(defaultProvider, "then"));
		completions.add(new BasicCompletion(defaultProvider, "true"));
		completions.add(new BasicCompletion(defaultProvider, "until"));
		completions.add(new BasicCompletion(defaultProvider, "while"));

		defaultProvider.addCompletions(completions);
		defaultProvider.setParameterizedCompletionParams('(', ", ", ')');
		defaultProvider.setAutoActivationRules(true, ".");

		autoCompletion = new AutoCompletion(defaultProvider);
		autoCompletion.setShowDescWindow(true);
		autoCompletion.setAutoCompleteSingleChoices(false);
		autoCompletion.setParameterAssistanceEnabled(true);
		autoCompletion.setAutoActivationEnabled(true);
		autoCompletion.setAutoActivationDelay(100);
		autoCompletion.setAutoCompleteEnabled(true);
	}

	public static void loadTheme() {
		if (theme==null) {
			//			try (InputStream themeInputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("org/fife/ui/rsyntaxtextarea/themes/dark.xml")){
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

	private JComponent buildCodeEditor() {
		loadTheme();
		loadSpellingParser();
		buildAutocomplete();

		luaErrorParser=new LuaErrorParser();
		luaExecuteErrorParser=new LuaExecuteErrorParser();

		syntaxTextArea = new RSyntaxTextArea(20, 60);
		syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
		syntaxTextArea.setCodeFoldingEnabled(true);
		syntaxTextArea.addFocusListener(this);

		syntaxTextArea.setCodeFoldingEnabled(true);
		syntaxTextArea.setPaintTabLines(true);
		syntaxTextArea.setBracketMatchingEnabled(true);

		syntaxTextArea.discardAllEdits();
		//		syntaxTextArea.getDocument().addUndoableEditListener(this);
		syntaxTextArea.addParser(parser);
		syntaxTextArea.addParser(luaErrorParser);
		syntaxTextArea.addParser(luaExecuteErrorParser);
		syntaxTextArea.addParser(new TaskTagParser());
		syntaxTextArea.setText(new String(sourceCode.getCode()));
		if (theme!=null) {
			theme.apply(syntaxTextArea);
		}
		textScrollPane = new RTextScrollPane(syntaxTextArea);

		errorStrip=new ErrorStrip(syntaxTextArea);
		errorStrip.setShowMarkAll(true);
		errorStrip.setCaretMarkerColor(ImageUtilities.HIGHLIGHT_COLOR);

		autoCompletion.install(syntaxTextArea);

		return textScrollPane;
	}

	private void build() {
		setLayout(new BorderLayout());

		add(buildCodeEditor(),BorderLayout.CENTER);
		add(errorStrip,BorderLayout.LINE_END);
	}

	class LuaErrorParser extends AbstractParser {
		private DefaultParseResult result;

		public LuaErrorParser() {
			result = new DefaultParseResult(this);
		}

		@Override
		public ParseResult parse(RSyntaxDocument doc, String style) {
			try {
//				System.out.println("Lua Parse");
//				Element root = doc.getDefaultRootElement();
//				int lineCount = root.getElementCount();
//
//				result.clearNotices();
//
				sourceCode.setCode(doc.getText(0, doc.getLength()));
//
//				LUAScriptError error=adventureProjectModel.getAdventureLuaEnviroment().parse(sourceCode.getName(), sourceCode.getCode());
//				if (error!=null) {
//					ErrorNotice errorNotice = new ErrorNotice(this, error.description, error.line+1, error.column, error.lenght);
//					result.addNotice(errorNotice);
//					result.setParsedLines(0, error.line-1);
//					lineCount=error.line-1;
//				}
//				luaErrorParser.result.setParsedLines(0, lineCount);

				adventureProjectModel.fireFileGameObjectChange(SourceCodeEditorLua.this, sourceCode);
			} catch (BadLocationException e) {
				log.warn(e.getMessage(),e);
			}
			return result;
		}
	}
	
	class LuaExecuteErrorParser extends AbstractParser {
		private DefaultParseResult result;

		public LuaExecuteErrorParser() {
			result = new DefaultParseResult(this);
		}

		@Override
		public ParseResult parse(RSyntaxDocument doc, String style) {
			Element root = doc.getDefaultRootElement();
			int lineCount = root.getElementCount();
			result.setParsedLines(0, lineCount-1);

			return result;
		}
	}

	public static class ErrorNotice extends DefaultParserNotice {

		public ErrorNotice(Parser parser, String message, int line) {
			super(parser, message, line);
			setLevel(Level.ERROR);
			setShowInEditor(true);
			setToolTipText(message);
		}

		public ErrorNotice(Parser parser, String message, int line, int column, int length) {
			super(parser, message, line, column, length);
			setLevel(Level.ERROR);
			setShowInEditor(true);
			setToolTipText(message);
		}

	}

	@Override
	public void focusGained(FocusEvent e) {
		if (ProjectFileTreeNode.wasFileChanged(sourceCode.getSourceFile())) {
			boolean reload=true;
			if (sourceCode.hasChanges()) {
				reload=JOptionPane.showConfirmDialog(e.getComponent(),"File has been modified on the drive. Reload and loose local changes.","Reload disk version?",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
			}
			// This will prevent the reload from asking over and over
			ProjectFileTreeNode.updateFileChangedDate(sourceCode.getSourceFile());
			if (reload) {
				StorageUtilities.reloadSourceCode(sourceCode);
				syntaxTextArea.setText(new String(sourceCode.getCode()));
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						adventureProjectModel.fireFileGameObjectChange(this, sourceCode);
					}
				});
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
	}
}
