package com.cinch.adventurebuilderstoolkit.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.StorageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.Project;
import com.cinch.adventurebuilderstoolkit.beans.ProjectConfig;
import com.cinch.adventurebuilderstoolkit.display.factories.DynamicTreeCellRenderer;
import com.cinch.adventurebuilderstoolkit.editor.actions.ExitAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.OnlineDiscordAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.OnlineDiscordForumAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.OnlineDiscordGuidesAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.OnlineHelpAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.OnlinePrivacyAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.OnlineTermsAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.ProjectPluginsAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.ProjectSettingsAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.SaveProjectAction;
import com.cinch.adventurebuilderstoolkit.editor.consoles.LuaConsole;
import com.cinch.adventurebuilderstoolkit.editor.swing.DockablePanel;
import com.cinch.adventurebuilderstoolkit.lua.LuaActionChangeListener;
import com.cinch.adventurebuilderstoolkit.lua.LuaSwingAction;
import com.formdev.flatlaf.extras.components.FlatMenu;
import com.formdev.flatlaf.extras.components.FlatMenuBar;
import com.formdev.flatlaf.extras.components.FlatMenuItem;
import com.formdev.flatlaf.extras.components.FlatTree;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.util.Filter;

public class AdventureEditor extends JFrame implements ComponentListener, WindowStateListener, LuaActionChangeListener, WindowFocusListener {
	static private final Logger log = LogManager.getLogger(AdventureEditor.class);
	private static final long serialVersionUID = 1L;

	private AdventureProjectModel adventureProjectModel;
	private ProjectControllerActionManager projectControllerActionManager;
	private FlatTree projectTree = new FlatTree();
	private ExitAction exitAction;
	private ProjectConfig projectConfig;

	private CWorkingArea work;
	private CControl control;

	private FlatMenuBar menuBar;

	public AdventureEditor(Project project) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		super("Infomancer Forge - " + project.getName());

		System.setProperty("user.dir", project.getPath());

		control = new CControl(this);
		control.setTheme(ThemeMap.KEY_FLAT_THEME);
		control.putProperty(StackDockStation.TAB_PLACEMENT, TabPlacement.TOP_OF_DOCKABLE);
		
		addWindowFocusListener(this);
		
		work = control.createWorkingArea("work");

		adventureProjectModel = new AdventureProjectModel(project);
		adventureProjectModel.getAdventureLuaEnviroment().addLuaActionChangeListener(this);
		projectControllerActionManager = new ProjectControllerActionManager(projectTree, adventureProjectModel, work);

		control.addSingleDockableFactory(new Filter<String>() {
			
			@Override
			public boolean includes(String arg0) {
				return true;
			}
		}, new DockableFactory(adventureProjectModel,projectControllerActionManager));		
		
		setResizable(true);
		setIconImages(ImageUtilities.getApplicationIcons());

		projectConfig = StorageUtilities.getProjectConfig(project);
		if (projectConfig.getEditorWindowSize() != null) {
			setSize(projectConfig.getEditorWindowSize());
		} else {
			setSize(800, 600);
		}

		if (projectConfig.getEditorWindowPosition() != null) {
			setLocation(projectConfig.getEditorWindowPosition());
		} else {
			setLocationByPlatform(true);
		}

		if (projectConfig.isMaximized()) {
			setExtendedState(MAXIMIZED_BOTH);
		}

		addComponentListener(this);
		addWindowStateListener(this);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitAction.actionPerformed(new ActionEvent(e.getSource(), 0, "Frame"));
			}
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {saveDockingXML();}));

		build();

		setVisible(true);

		// Init project
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				adventureProjectModel.getAdventureLuaEnviroment().loadAllCurrent();
				restoreDockingXML();
			}
		});
	}

	public AdventureProjectModel getAdventureProjectModel() {
		return adventureProjectModel;
	}

	private DockablePanel buildProjectTree() {

		projectTree.setModel(adventureProjectModel.getProjectTreeModel());
		adventureProjectModel.getProjectTreeModel().setFlatTree(projectTree);
		projectTree.setCellRenderer(new DynamicTreeCellRenderer());
		projectTree.getModel().addTreeModelListener(new TreeSelectingModelListener());

		DockablePanel dockablePanel = new DockablePanel("Project", "main.ProjectTree", ImageUtilities
				.getIcon(FluentUiRegularMZ.MAP_24, ImageUtilities.TAB_ICON_COLOR, ImageUtilities.TAB_ICON_SIZE));
		dockablePanel.setLayout(new BorderLayout());
		dockablePanel.add(projectTree, BorderLayout.CENTER);

		// Add mouse listener to tree to add right click menu
		// TODO: We probably need to add keyboard listener too
		projectTree.addMouseListener(projectControllerActionManager);

		return dockablePanel;
	}
	private JMenu buildProjectMenu() {
		
		FlatMenuItem projectPluginMenu = new FlatMenuItem();
		projectPluginMenu.setAction(new ProjectPluginsAction(this,adventureProjectModel));
		
		FlatMenuItem projectSettingsMenu = new FlatMenuItem();
		projectSettingsMenu.setAction(new ProjectSettingsAction(this,adventureProjectModel));

		FlatMenu menu = new FlatMenu();
		menu.setText("Project");
		menu.setMnemonic('P');
		menu.add(projectPluginMenu);
		menu.add(projectSettingsMenu);

		return menu;
	}
	
	private JMenu buildFileMenu() {
		FlatMenuItem saveMenu = new FlatMenuItem();
		saveMenu.setAction(new SaveProjectAction(adventureProjectModel));

		exitAction = new ExitAction(adventureProjectModel);
		FlatMenuItem exitMenu = new FlatMenuItem();
		exitMenu.setAction(exitAction);

		FlatMenu menu = new FlatMenu();
		menu.setText("File");
		menu.setMnemonic('F');
		menu.add(saveMenu);
		menu.addSeparator();
		menu.add(exitMenu);

		return menu;
	}
	
	private JMenu buildHelpMenu() {
		FlatMenuItem helpMenu = new FlatMenuItem();
		helpMenu.setAction(new OnlineHelpAction());

		FlatMenuItem discordMenu = new FlatMenuItem();
		discordMenu.setAction(new OnlineDiscordAction());
		FlatMenuItem discordGuidesMenu = new FlatMenuItem();
		discordGuidesMenu.setAction(new OnlineDiscordGuidesAction());
		FlatMenuItem discordForumsMenu = new FlatMenuItem();
		discordForumsMenu.setAction(new OnlineDiscordForumAction());

		FlatMenuItem termsMenu = new FlatMenuItem();
		termsMenu.setAction(new OnlineTermsAction());

		FlatMenuItem privacyMenu = new FlatMenuItem();
		privacyMenu.setAction(new OnlinePrivacyAction());

		FlatMenu menu = new FlatMenu();
		menu.setText("Help");
		menu.setMnemonic('H');
		menu.add(helpMenu);
		menu.addSeparator();
		menu.add(discordMenu);
		menu.add(discordGuidesMenu);
		menu.add(discordForumsMenu);
		menu.addSeparator();
		menu.add(termsMenu);
		menu.add(privacyMenu);

		return menu;
	}

	private void buildMenus() {
		menuBar = new FlatMenuBar();
		menuBar.add(buildFileMenu());
		menuBar.add(buildProjectMenu());
		menuBar.add(buildHelpMenu());

		setJMenuBar(menuBar);
	}

	private CGrid grid;

	private void buildDocking() {
		DockablePanel projectTree = buildProjectTree();
		var treeDock = new DefaultSingleCDockable(projectTree.getPersistentId(), projectTree.getIcon(),
				projectTree.getTitle(), projectTree);

		LuaConsole luaConsole = new LuaConsole(adventureProjectModel);
		var consoleDock = new DefaultSingleCDockable(luaConsole.getPersistentId(), luaConsole.getIcon(),
				luaConsole.getTitle(), luaConsole);

		grid = new CGrid(control);
		grid.add(1, 1, 3, 3, work);
		grid.add(0, 0, 1, 4, treeDock);
		grid.add(1, 3, 3, 1, consoleDock);
		control.getContentArea().deploy(grid);

		setLayout(new GridLayout(1, 1));
		add(control.getContentArea());
	}

	private void build() {
		buildMenus();
		buildDocking();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (this.getExtendedState() != MAXIMIZED_BOTH) {
			projectConfig.setEditorWindowSize(this.getSize());
			StorageUtilities.saveProjectConfig(adventureProjectModel.getProject(), projectConfig);
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		if (this.getExtendedState() != MAXIMIZED_BOTH) {
			projectConfig.setEditorWindowPosition(this.getLocation());
			StorageUtilities.saveProjectConfig(adventureProjectModel.getProject(), projectConfig);
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void windowStateChanged(WindowEvent e) {
		if (e.getNewState() == MAXIMIZED_BOTH) {
			projectConfig.setMaximized(true);
		} else {
			projectConfig.setMaximized(false);
			projectConfig.setEditorWindowSize(this.getSize());
			projectConfig.setEditorWindowPosition(this.getLocation());
		}
		StorageUtilities.saveProjectConfig(adventureProjectModel.getProject(), projectConfig);
	}

	class TreeSelectingModelListener implements TreeModelListener {

		public void treeNodesInserted(TreeModelEvent e) {
			// Invoke later so we know the node has been updated by other listeners
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					projectTree.setSelectionPath(e.getTreePath().pathByAddingChild(e.getChildren()[0]));
				}
			});
		}

		@Override
		public void treeNodesChanged(TreeModelEvent e) {
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {
		}

	}

	private JMenu findOrAddMenu(JMenu menu, String name) {
		if (menu == null) {
			for (int t = 0; t < menuBar.getMenuCount(); t++) {
				menu = menuBar.getMenu(t);
				if (menu.getText().equals(name)) {
					return menu;
				}
			}

			FlatMenu newMenu = new FlatMenu();
			newMenu.setText(name);
			menuBar.add(newMenu);
			return newMenu;
		} else {
			for (int t = 0; t < menu.getItemCount(); t++) {
				JMenuItem menuItem = menu.getItem(t);
				if (menuItem instanceof JMenu sm && sm.getText().equals(name)) {
					return sm;
				}
			}

			FlatMenu newMenu = new FlatMenu();
			newMenu.setText(name);
			menu.add(newMenu);
			return newMenu;
		}
	}

	private JMenu getMenuFor(String path) {
		JMenu found = null;
		if (path != null && path.length() > 0) {
			String[] paths = path.split("\\/");
			// Ignore last peace thats the name
			for (int t = 0; t < paths.length; t++) {
				found = findOrAddMenu(found, paths[t]);
			}
		}
		if (found == null) {
			found = findOrAddMenu(null, "Addon");
		}

		return found;
	}

	private void cleanOutEmptyMenus(JMenu menu) {
		for (int t = menu.getItemCount() - 1; t >= 0; t--) {
			JMenuItem menuItem = menu.getItem(t);
			if (menuItem instanceof JMenu subMenu) {
				cleanOutEmptyMenus(subMenu);
				if (subMenu.getItemCount() == 0) {
					menu.remove(t);
				}
			}
		}
	}

	private void cleanOutEmptyMenus() {
		for (int t = menuBar.getMenuCount() - 1; t >= 0; t--) {
			JMenu menu = menuBar.getMenu(t);
			cleanOutEmptyMenus(menu);
			if (menu.getItemCount() == 0) {
				menuBar.remove(t);
			}
		}
	}

	@Override
	public void luaActionAdded(LuaSwingAction luaSwingAction) {
		System.out.println("Adding action " + luaSwingAction.getName());
		if (luaSwingAction.isMenu()) {
			FlatMenuItem item = new FlatMenuItem();
			item.setAction(luaSwingAction.getMenuAction());
			getMenuFor(luaSwingAction.getPath()).add(item);
		}
	}

	@Override
	public void luaActionRemoved(LuaSwingAction luaSwingAction) {
		if (luaSwingAction.isMenu()) {
			JMenu menu = getMenuFor(luaSwingAction.getPath());
			for (int t = 0; t < menu.getItemCount(); t++) {
				if (menu.getItem(t).getText().equals(luaSwingAction.getName())) {
					menu.remove(t);
				}
			}
			cleanOutEmptyMenus();
		}
	}

	public void saveDockingXML() {
		try {
			File layoutFile=new File(adventureProjectModel.getProject().getPath()+"/.data/layout.xml");
			control.writeXML(layoutFile);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void restoreDockingXML() {
		try {
			File layoutFile=new File(adventureProjectModel.getProject().getPath()+"/.data/layout.xml");
			if (layoutFile.exists()) {
				control.readXML(layoutFile);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				adventureProjectModel.refreshFiles();
				AdventureEditor.this.repaint();
			}
		});
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
	}

}
