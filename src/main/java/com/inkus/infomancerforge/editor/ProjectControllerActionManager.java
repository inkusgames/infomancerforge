package com.inkus.infomancerforge.editor;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.extras.components.FlatMenuItem;
import com.formdev.flatlaf.extras.components.FlatPopupMenu;
import com.formdev.flatlaf.extras.components.FlatTree;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.editor.actions.CreateGobAction;
import com.inkus.infomancerforge.editor.actions.CreateLuaScriptAction;
import com.inkus.infomancerforge.editor.actions.CreateNewFolder;
import com.inkus.infomancerforge.editor.actions.CreateViewAction;
import com.inkus.infomancerforge.editor.actions.DeleteFileAction;
import com.inkus.infomancerforge.editor.actions.DeleteFolderAction;
import com.inkus.infomancerforge.editor.actions.OpenProjectFileAction;
import com.inkus.infomancerforge.editor.actions.RenameFileAction;
import com.inkus.infomancerforge.editor.actions.SaveProjectAction;
import com.inkus.infomancerforge.editor.actions.TreeActionInterface;
import com.inkus.infomancerforge.editor.swing.DockablePanel;

import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;

public class ProjectControllerActionManager implements MouseListener, FileGameObjectChangeListener, FileGameObjectDeletedListener {
	static private final Logger log=LogManager.getLogger(ProjectControllerActionManager.class);

	private FlatTree flatTree;
	
	private List<TreeActionInterface> actions=new ArrayList<>();
	private TreeActionInterface openAction;
	private CWorkingArea work;
	private ProjectItemEditorFactory projectItemEditorFactory;
	@SuppressWarnings("unused")
	private AdventureProjectModel adventureProjectModel;
	private Map<String,DefaultSingleCDockable> openEditors=new HashMap<>();
	private Map<String,DockablePanel> openDockablePanels=new HashMap<>();

	public ProjectControllerActionManager(FlatTree flatTree, AdventureProjectModel adventureProjectModel,CWorkingArea work) {
		this.flatTree = flatTree;
		this.work = work;
		this.adventureProjectModel=adventureProjectModel;

		projectItemEditorFactory=new ProjectItemEditorFactory(adventureProjectModel);

		// General Stuff
		openAction=new OpenProjectFileAction(flatTree,adventureProjectModel,this);
		actions.add(openAction);
		actions.add(null);
		
		// Game Stuff
		actions.add(new CreateGobAction(adventureProjectModel,null));
		actions.add(new CreateViewAction(adventureProjectModel));
		actions.add(new CreateLuaScriptAction(adventureProjectModel));
		actions.add(null);
		
		// File Stuff
		actions.add(new CreateNewFolder(adventureProjectModel));
		actions.add(null);
		// File rename Stuff
		actions.add(new RenameFileAction(adventureProjectModel));
		actions.add(null);
		// File delete Stuff
		actions.add(new DeleteFileAction(adventureProjectModel));
		actions.add(new DeleteFolderAction(adventureProjectModel));
		actions.add(null);
		
		// Project Stuff
		actions.add(new SaveProjectAction(adventureProjectModel));
		
		adventureProjectModel.addFileGameObjectChangeListener(this);
		adventureProjectModel.addFileGameObjectDeletedListener(this);
	}
	
	public ProjectItemEditorFactory getProjectItemEditorFactory() {
		return projectItemEditorFactory;
	}
	
	public void openSelectedItems() {
		// Get selected items
		TreePath[] paths = flatTree.getSelectionPaths();

		// Show that menu.
		if (paths!=null) {
			for (TreePath treePath:paths) {
				TreeNode node=(TreeNode)treePath.getLastPathComponent();
				if (openAction.canProcess(node)) {
					openAction.executeOn(node, null);
				}
			}
		}
	}

	public void leftMouseDoubleClicked(MouseEvent e) {
		openSelectedItems();
	}
	
	public boolean canOpen(TreeNode treeNode) {
		return projectItemEditorFactory.canBuildEditor(treeNode);
	}
	
	public void addEditor(TreeNode treeNode,DockablePanel dockable,DefaultSingleCDockable editor) {
		String editorKey=projectItemEditorFactory.getEditorKey(treeNode);
		editor.setCloseable(true);
		editor.addVetoClosingListener(new CVetoClosingListener() {
			@Override
			public void closing(CVetoClosingEvent event) {
			}
			
			@Override
			public void closed(CVetoClosingEvent event) {
				openEditors.remove(editorKey);
//				work.getControl().removeSingleDockable(editorKey);
				work.getControl().removeDockable(editor);
			}
		});
		openEditors.put(editorKey, editor);
		openDockablePanels.put(editorKey, dockable);
	}

	public void openOrShowEditor(TreeNode treeNode) {
		String editorKey=projectItemEditorFactory.getEditorKey(treeNode);
		
		if (editorKey!=null) {
			if (!openEditors.containsKey(editorKey)) {
				// Create new editor and add it
				var dockable=projectItemEditorFactory.buildNewEditor(treeNode);
				
				if (dockable!=null) {
					var editor = new DefaultSingleCDockable(dockable.getPersistentId(), dockable.getIcon(), dockable.getTitle(), dockable);
					editor.setCloseable(true);
//					
//					if (treeNode instanceof ProjectFileTreeNamedResourceNode projectFileTreeNamedResourceNode) {
//						System.out.println("Title Listener="+projectFileTreeNamedResourceNode.getName());
//						adventureProjectModel.addFileGameObjectChangeListener(new FileGameObjectChangeListener() {
//							@Override
//							public void fileGameObjectChanged(Object source, FileGameObject fileGameObject) {
//								System.out.println("Title="+projectFileTreeNamedResourceNode.getName());
//								editor.setTitleText(projectFileTreeNamedResourceNode.getName());
//							}
//						});
//					}
					
					work.show(editor);
					editor.toFront();
					editor.addVetoClosingListener(new CVetoClosingListener() {
						
						@Override
						public void closing(CVetoClosingEvent event) {
						}
						
						@Override
						public void closed(CVetoClosingEvent event) {
							openEditors.remove(editorKey);
							work.getControl().removeDockable(editor);
							
						}
					});
					openEditors.put(editorKey, editor);
					openDockablePanels.put(editorKey, dockable);
				} else {
					log.error("Failed to create editor for '"+editorKey+"'");
					return;
				}
			}
			openEditors.get(editorKey).toFront();
		} else {
			log.error("Unable to open treeNode.",treeNode);
		}
	}

	@Override
	public void fileGameObjectChanged(Object source, FileGameObject fileGameObject) {
		if (openEditors.containsKey(fileGameObject.getUuid())) {
			var dockable=openDockablePanels.get(fileGameObject.getUuid());
			var editor=openEditors.get(fileGameObject.getUuid());
			editor.setTitleText(dockable.getTitle());
			editor.setTitleIcon(dockable.getIcon());
		}
	}

	public void rightMouseClicked(MouseEvent e) {
		if (!e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
			// Find item under cursor.
			int selectedRow = flatTree.getRowForLocation(e.getX(), e.getY());
			TreePath selectedPath = flatTree.getPathForLocation(e.getX(), e.getY());
			flatTree.setSelectionPath(selectedPath);
			if (selectedRow > -1) {
				flatTree.setSelectionRow(selectedRow);
			}
		}
		// Get selected items
		TreePath[] paths = flatTree.getSelectionPaths();
		
		List<AbstractAction> availableActions=new ArrayList<>();
		
		// Show that menu.
		if (paths!=null) {
			boolean seenNull=false;
			for (TreeActionInterface treeAction:actions) {
				if (treeAction==null) {
					seenNull=true;
				} else {
					boolean available=paths.length>0;
					for (TreePath treePath:paths) {
						TreeNode node=(TreeNode)treePath.getLastPathComponent();
						available&=treeAction.canProcess(node);
						if (!available) {
							break;
						}
					}
					
					available&=paths.length==1 || !treeAction.onlySuitableForSingleSelections();
					
					if (available) {
						if (seenNull) {
							if (availableActions.size()>0) {
								availableActions.add(null);
							}
							seenNull=false;
						}
						availableActions.add(new TreeActionWrapper(treeAction,paths));
					}
				}
			}
		}
		
		if (availableActions.size()>0) {
			FlatPopupMenu popupMenu=new FlatPopupMenu();
			
			for (var action:availableActions) {
				if (action==null) {
					popupMenu.addSeparator();
				} else {
					FlatMenuItem menuItem=new FlatMenuItem();
					menuItem.setAction(action);
					popupMenu.add(action);
				}
			}
			
			popupMenu.show(flatTree, e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
			rightMouseClicked(e);
		}else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
			leftMouseDoubleClicked(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void fileGameObjectDeleted(FileGameObject fileGameObject) {
//		var dockable=openDockablePanels.get(fileGameObject.getUuid());
		var editor=openEditors.remove(fileGameObject.getUuid());
		work.getControl().removeDockable(editor);
//		work.
//		dockable.get
//		
//		//ActionEvent e=new ActionEvent(this, 0, null);
//		dockable.getActionMap().get(CDockable.ACTION_KEY_CLOSE).actionPerformed(e);
//		
		
//		if (dockable.isVisible()) {
//			dockable.setVisible(false);
//		}
	}
	
	class TreeActionWrapper extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		TreeActionInterface parent;
		TreePath[] paths;
		
		TreeActionWrapper(TreeActionInterface parent,TreePath[] paths){
			this.parent=parent;
			this.paths=paths;
			
			AbstractAction parentAction=parent.getAction();
			for (Object key:parentAction.getKeys()) {
				putValue(key.toString(), parentAction.getValue(key.toString()));
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (TreePath treePath:paths) {
				TreeNode node=(TreeNode)treePath.getLastPathComponent();
				parent.executeOn(node,e);
			}
		}
	}
	
}
