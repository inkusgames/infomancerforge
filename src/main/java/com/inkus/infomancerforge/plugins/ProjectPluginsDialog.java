package com.inkus.infomancerforge.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatScrollPane;
import com.formdev.flatlaf.extras.components.FlatTable;
import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.sourcecode.SourceCode;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectSourceCodeTreeNode;
import com.inkus.infomancerforge.plugins.PluginModel.PluginTableModel;
import com.inkus.infomancerforge.storage.DownLoader;
import com.inkus.infomancerforge.storage.DownLoader.DownloadProgressListener;
import com.inkus.infomancerforge.storage.DownLoader.DownloadState;

public class ProjectPluginsDialog extends JDialog {
	static private final Logger log=LogManager.getLogger(ProjectPluginsDialog.class);
	private static final long serialVersionUID = 1L;
	
	private AdventureProjectModel adventureProjectModel;
	private FlatTable pluginTable;
	private UpdateCataglogueAction updateCataglogueAction;
	private InstallSelectedAction installSelectedAction;
	private UninstallSelectedAction uninstallSelectedAction;
	private PluginTableModel pluginTableModel;
	
	private int downloadsHappening=0;

	public ProjectPluginsDialog(Frame owner,AdventureProjectModel adventureProjectModel) {
		super(owner,"Project Plugins");
		this.adventureProjectModel=adventureProjectModel;
		setSize(1000,600);
		setResizable(false);
		setLocationRelativeTo(owner);
		setModal(true);
		build();
		load();
		setVisible(true);
	}

	private JComponent buildCenter() {
		pluginTable=new FlatTable();
		pluginTableModel=adventureProjectModel.getPluginModel().getPluginTableModel();
		pluginTable.setModel(pluginTableModel);
		pluginTable.setAutoCreateRowSorter(true);
		pluginTable.setUpdateSelectionOnSort(true);

		pluginTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		pluginTable.getColumnModel().getColumn(1).setPreferredWidth(15);
		pluginTable.getColumnModel().getColumn(2).setPreferredWidth(10);
		pluginTable.getColumnModel().getColumn(3).setPreferredWidth(450);
		pluginTable.getColumnModel().getColumn(4).setPreferredWidth(120);
		pluginTable.getColumnModel().getColumn(5).setPreferredWidth(10);
		pluginTable.getColumnModel().getColumn(6).setPreferredWidth(10);

		FlatScrollPane flatScrollPane=new FlatScrollPane();
		flatScrollPane.getViewport().setView(pluginTable);
		return flatScrollPane;
	}

	private void load() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				adventureProjectModel.getAnalyticsController().sendEvent("Plugins", "LoadList", null);
				updateCataglogueAction.setEnabled(false);
				adventureProjectModel.getPluginModel().load(new DownloadProgressListener<String>() {
					@Override
					public void downloadStateChanged(DownLoader<String> downLoader, DownloadState downloadState) {
						if (downloadState.isCompleted()) {
							updateCataglogueAction.setEnabled(true);
						}
					}

					@Override
					public void downloadDone(DownLoader<String> downLoader, String data) {
					}
				});
			}
		});
	}

	private void addDependancies(Set<Plugin> dependancies,Plugin plugin) {
		if (plugin.getDependancies()!=null) {
			for (String d:plugin.getDependancies()) {
				Plugin pd=adventureProjectModel.getPluginModel().getPluginByName(d);
				if (pd!=null) {
					if (!dependancies.contains(pd)) {
						dependancies.add(pd);
						addDependancies(dependancies,pd);
					}
				} else {
					log.warn("Plugin dependancy '"+d+"' not found for plugin '"+plugin.getName()+"'.");
				}
			}
		}
	}
	
	private void install() {
		var rows=pluginTable.getSelectedRows();
		if (rows.length>0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					installSelectedAction.setEnabled(false);
					uninstallSelectedAction.setEnabled(false);
					Set<Plugin> setToInstall=new HashSet<>();
					Set<Plugin> requiredToInstall=new HashSet<>();
					for (int row:rows) {
						Plugin plugin=pluginTableModel.getPluginForRow(row);
						setToInstall.add(plugin);
						addDependancies(requiredToInstall,plugin);
					}
					
					for (Plugin plugin:requiredToInstall) {
						if (!setToInstall.contains(plugin) && (!adventureProjectModel.getPluginModel().arePlugingFilesFound(plugin) || !adventureProjectModel.getProject().getPlugins().containsKey(plugin.getName()))){
							setToInstall.add(plugin);
						}
					}
					
					for (Plugin plugin:setToInstall) {
						if (plugin.getType().equals("Full")) {
							adventureProjectModel.getAnalyticsController().sendEvent("Plugins", "Install", plugin.getName());
							DownLoader.getBinaryDownloader(PluginModel.PLUGIN_URL_BASE+plugin.getFilename(), new DownloadPluginFull(plugin));
						} else if (plugin.getType().equals("Module")) {
							adventureProjectModel.getAnalyticsController().sendEvent("Plugins", "Install", plugin.getName());
							DownLoader.getStringDownloader(PluginModel.PLUGIN_URL_BASE+plugin.getFilename(), new DownloadPluginModule(plugin));
						}
					}
				}
			});
		}
	}

	private void remove() {
		var rows=pluginTable.getSelectedRows();
		if (rows.length>0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					installSelectedAction.setEnabled(false);
					uninstallSelectedAction.setEnabled(false);
					for (int row:rows) {
						Plugin plugin=pluginTableModel.getPluginForRow(row);
						adventureProjectModel.getPluginModel().deletePlugingFiles(plugin);
						adventureProjectModel.getProject().getPlugins().remove(plugin.getName());
						pluginTableModel.fireChange(plugin);
						adventureProjectModel.getAnalyticsController().sendEvent("Plugins", "Delete", plugin.getName());
					}
					adventureProjectModel.refreshFiles();
					adventureProjectModel.getAdventureLuaEnviroment().loadAllCurrent();
					adventureProjectModel.saveAll();
					installSelectedAction.setEnabled(true);
					uninstallSelectedAction.setEnabled(true);
				}
			});
		}
	}

	private JComponent buildButtons() {
		JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));

		FlatButton updateButton=new FlatButton();
		updateCataglogueAction=new UpdateCataglogueAction();
		updateButton.setAction(updateCataglogueAction);
		updateButton.setFocusable(false);
		buttons.add(updateButton);

		FlatButton uninstallButton=new FlatButton();
		uninstallSelectedAction=new UninstallSelectedAction();
		uninstallButton.setAction(uninstallSelectedAction);
		uninstallButton.setFocusable(false);
		buttons.add(uninstallButton);

		FlatButton installButton=new FlatButton();
		installSelectedAction=new InstallSelectedAction();
		installButton.setAction(installSelectedAction);
		installButton.setFocusable(false);
		buttons.add(installButton);

		FlatButton cancelButton=new FlatButton();
		cancelButton.setAction(new CloseAction());
		cancelButton.setFocusable(false);
		buttons.add(cancelButton);

		return buttons;
	}

	private void build() {
		JPanel basePanel=new JPanel(new BorderLayout());
		basePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		basePanel.add(buildCenter(),BorderLayout.CENTER);
		basePanel.add(buildButtons(),BorderLayout.SOUTH);
		setContentPane(basePanel);
	}

	class UpdateCataglogueAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public UpdateCataglogueAction() {
			putValue(NAME, "Update plugin catalogue");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.ARROW_CLOCKWISE_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			load();
		}

	}

	class InstallSelectedAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public InstallSelectedAction() {
			putValue(NAME, "Install selected plugins");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.PLUG_DISCONNECTED_20, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			install();
		}
	}

	class UninstallSelectedAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public UninstallSelectedAction() {
			putValue(NAME, "Remove selected plugins");
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.DELETE_24, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			remove();
		}
	}

	class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CloseAction() {
			putValue(NAME, "Close");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ProjectPluginsDialog.this.setVisible(false);
		}
	}

	abstract class DownloadPluginAbstract<type> implements DownloadProgressListener<type>{

		private Plugin plugin;
		
		protected DownloadPluginAbstract(Plugin plugin){
			this.plugin=plugin;
			synchronized (ProjectPluginsDialog.this) {
				downloadsHappening++;
			}
		}
		
		@Override
		public void downloadDone(DownLoader<type> downLoader, type data) {
			adventureProjectModel.getPluginModel().setStatus(plugin, "Installing");
			if (install(plugin,data)) {
				adventureProjectModel.getProject().getPlugins().put(plugin.getName(), plugin);
				adventureProjectModel.getPluginModel().setStatus(plugin, "Installed");
			} else {
				Plugin p=plugin;
				try {
					p = (Plugin)BeanUtils.cloneBean(plugin);
				} catch (Exception e) {
					e.printStackTrace();
				}
				p.setLatestVersion("?");
				adventureProjectModel.getProject().getPlugins().put(plugin.getName(), p);
				adventureProjectModel.getPluginModel().setStatus(plugin, "Failed");

			}
			if (downloadsHappening==0) {
				// Refresh full tree node
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						adventureProjectModel.refreshFiles();
						adventureProjectModel.getAdventureLuaEnviroment().loadAllCurrent();
						adventureProjectModel.saveAll();
					}
				});
			}
		}

		@Override
		public void downloadStateChanged(DownLoader<type> downLoader, DownloadState downloadState) {
			adventureProjectModel.getPluginModel().setStatus(plugin, downloadState.name());
			if (downloadState.isCompleted()) {
				synchronized (ProjectPluginsDialog.this) {
					downloadsHappening--;
					if (downloadsHappening==0) {
						installSelectedAction.setEnabled(true);
						uninstallSelectedAction.setEnabled(true);
					}
				}
			}
		}

		public abstract boolean install(Plugin plugin,type data);
		
	}

	
	class DownloadPluginFull extends DownloadPluginAbstract<byte[]>{
		
		public DownloadPluginFull(Plugin plugin) {
			super(plugin);
		}

		public boolean install(Plugin plugin,byte[] data) {
			try {
				ProjectFileTreeNode pluginNode=adventureProjectModel.getAdventureProjectTreeModel().findPluginTreeNode(true);
				System.out.println("Plugin:"+plugin.getName()+" b:="+data.length);
				File path=new File(pluginNode.getFile().getAbsolutePath()+"/"+plugin.getName());
				ProjectFileTreeNode treeNode;
				if (path.exists()) {
					StorageUtilities.deleteAll(path);
					treeNode=(ProjectFileTreeNode) adventureProjectModel.getAdventureProjectTreeModel().findTreeNodeForFile(path);
					StorageUtilities.unzip(data, path.getParentFile().getAbsolutePath());
				} else {
					// Need to add path to tree
					path.mkdirs();
					StorageUtilities.unzip(data, path.getParentFile().getAbsolutePath());
					treeNode=new ProjectFileTreeNode(adventureProjectModel, pluginNode, path);
					adventureProjectModel.addFileNode(pluginNode,treeNode);
				}
	
				return true;
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			return false;
		}

	}
	
	class DownloadPluginModule extends DownloadPluginAbstract<String>{
		
		public DownloadPluginModule(Plugin plugin) {
			super(plugin);
		}

		public boolean install(Plugin plugin,String data) {
			ProjectFileTreeNode moduleNode=adventureProjectModel.getAdventureProjectTreeModel().findModuleTreeNode(true);
			
			File newFile=new File(moduleNode.getFile().getAbsolutePath()+"/"+plugin.getFilename());
			boolean existed=newFile.exists();
			if (existed) {
				ProjectSourceCodeTreeNode treeNode=(ProjectSourceCodeTreeNode)adventureProjectModel.getAdventureProjectTreeModel().findTreeNodeForFile(newFile);
				SourceCode module=treeNode.getSourceCode();
				module.setCode(data);
				StorageUtilities.saveSourceCode(module,newFile.getAbsolutePath());				
				adventureProjectModel.fireFileGameObjectChange(this, module);
			} else {
				String sourcePath=moduleNode.getFile().getAbsolutePath().substring(adventureProjectModel.getProject().getPath().length());
				String uuid=adventureProjectModel.getProject().getResources().get(sourcePath);
				if (uuid==null || uuid.length()==0) {
					uuid=UUID.randomUUID().toString();
					adventureProjectModel.getProject().getResources().put(sourcePath,uuid);
				}

				SourceCode module=new SourceCode(moduleNode.getFile(),sourcePath, "lua", data, uuid);

				StorageUtilities.saveSourceCode(module,newFile.getAbsolutePath());
				ProjectSourceCodeTreeNode treeNode=new ProjectSourceCodeTreeNode(adventureProjectModel, moduleNode, newFile);
				adventureProjectModel.addFileNode(moduleNode,treeNode);
			}
			
			return true;
		}

	}
	
}
