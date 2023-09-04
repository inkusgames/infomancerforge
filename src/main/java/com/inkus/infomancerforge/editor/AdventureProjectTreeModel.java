package com.inkus.infomancerforge.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.formdev.flatlaf.extras.components.FlatTree;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.data.DataInstance;
import com.inkus.infomancerforge.editor.treenodes.ProjectFileTreeNode;
import com.inkus.infomancerforge.editor.treenodes.ProjectTreeNode;

public class AdventureProjectTreeModel extends DefaultTreeModel implements FileGameObjectChangeListener, DataInstanceChangeListener {
	private static final long serialVersionUID = 1L;

	private AdventureProjectModel adventureProjectModel;

	private FlatTree tree;
	
	public AdventureProjectTreeModel(AdventureProjectModel adventureProjectModel) {
		super(new ProjectTreeNode(adventureProjectModel));
		this.adventureProjectModel=adventureProjectModel;
		adventureProjectModel.addFileGameObjectChangeListener(this);
		adventureProjectModel.addDataInstanceChangeListener(this);
	}
	
	public void setFlatTree(FlatTree tree) {
		this.tree=tree;
	}
	
	public AdventureProjectModel getAdventureProjectModel() {
		return adventureProjectModel;
	}

	private List<TreePath> getExpandedPaths(TreePath currPath) {
	    List<TreePath> paths = new ArrayList<TreePath>();
	    Enumeration<TreePath> expandEnum = tree.getExpandedDescendants(currPath);
	    if (expandEnum == null) {
	        return null;
	    }
	
	    while (expandEnum.hasMoreElements()) {
	        paths.add(expandEnum.nextElement());
	    }
	
	    return paths;
	}
	
	private void restoreExpandPaths(List<TreePath> expPaths) {
	    if(expPaths == null) {
	        return;
	    }
	    
	    for(TreePath tp : expPaths) {
	        tree.expandPath(tp);
	    }
	}
	
	@Override
	public void fileGameObjectChanged(Object source, FileGameObject fileGameObject) {
		ProjectTreeNode projectTreeNode=(ProjectTreeNode) getRoot();
		List<TreeNode> path=new ArrayList<>();
		int pos=searchPath(path,projectTreeNode,fileGameObject);
		if (pos>=0) {
			path.add(0,projectTreeNode);
			fireTreeStructureChanged(source,path.toArray(new TreeNode[path.size()]),new int[] {pos},new Object[] {fileGameObject});
		}
	}
	
	

	@Override
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		TreePath nodesPath = new TreePath((ProjectTreeNode) getRoot());
		TreePath currentSel = tree.getLeadSelectionPath();
		List<TreePath> currOpen  = getExpandedPaths(nodesPath);
		
		super.fireTreeStructureChanged(source, path, childIndices, children);
		restoreExpandPaths(currOpen);
		tree.setSelectionPath(currentSel);
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//			}
//		});
	}

	@Override
	public void dataInstanceObjectChanged(Object source, DataInstance dataInstance) {
		if (dataInstance instanceof GOBInstance gobInstance) {
			GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstance.getGobType());
			if (gob!=null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fileGameObjectChanged(source,gob);
					}
				});
			}
		}
	}
	
	private int searchPath(List<TreeNode> path,TreeNode node,FileGameObject fileGameObject) {
		for (int t=0;t<node.getChildCount();t++) {
			TreeNode child=node.getChildAt(t);
			if (child instanceof ProjectFileTreeNode projectFileTreeNode) {
				if (projectFileTreeNode.holdsFileGameObject(fileGameObject)) {
					return t;
				}
			}
			if (child.getChildCount()>0) {
				int pos=searchPath(path,child,fileGameObject);
				if (pos>=0) {
					path.add(0,child);
					return pos;
				}
			}
		}
		return -1;
	}

	public TreeNode findTreeNodeForFileGameObject(FileGameObject fileGameObject) {
		return findTreeNodeForFileGameObject((ProjectTreeNode) getRoot(),fileGameObject);
	}
	
	private TreeNode findTreeNodeForFileGameObject(TreeNode node,FileGameObject fileGameObject) {
		for (int t=0;t<node.getChildCount();t++) {
			TreeNode child=node.getChildAt(t);
			if (child instanceof ProjectFileTreeNode projectFileTreeNode) {
				if (projectFileTreeNode.holdsFileGameObject(fileGameObject)) {
					return projectFileTreeNode;
				}
			}
			if (child.getChildCount()>0) {
				TreeNode foundNode=findTreeNodeForFileGameObject(child,fileGameObject);
				if (foundNode!=null) {
					return foundNode;
				}
			}
		}
		return null;
	}

	public TreeNode findTreeNodeForFile(File file) {
		return findTreeNodeForFile((ProjectTreeNode) getRoot(),file);
	}

	public ProjectFileTreeNode findPluginTreeNode(boolean create) {
		synchronized (this) {
			File file=new File(adventureProjectModel.getProject().getPath()+"/Plugins/");
			if (!file.exists() && create) {
				file.mkdirs();
				ProjectFileTreeNode treeNode=new ProjectFileTreeNode(adventureProjectModel, (ProjectTreeNode) getRoot(), file);
				adventureProjectModel.addFileNode((ProjectTreeNode) getRoot(), treeNode);
				return treeNode;
			} else {
				return (ProjectFileTreeNode)findTreeNodeForFile((ProjectTreeNode) getRoot(),file);	
			}
		}
	}
	
	public ProjectFileTreeNode findModuleTreeNode(boolean create) {
		synchronized (this) {
			ProjectFileTreeNode pluginsNode=findPluginTreeNode(create);
			if (pluginsNode!=null) {
				File file=new File(pluginsNode.getFile().getAbsolutePath()+"/Modules/");
				if (!file.exists() && create) {
					file.mkdirs();
					ProjectFileTreeNode treeNode=new ProjectFileTreeNode(adventureProjectModel, pluginsNode, file);
					adventureProjectModel.addFileNode(pluginsNode, treeNode);
					return treeNode;
				} else {
					return (ProjectFileTreeNode)findTreeNodeForFile(pluginsNode,file);	
				}
			}
		}
		return null;
	}

	private TreeNode findTreeNodeForFile(TreeNode node,File file) {
		for (int t=0;t<node.getChildCount();t++) {
			TreeNode child=node.getChildAt(t);
			if (child instanceof ProjectFileTreeNode projectFileTreeNode) {
				if (projectFileTreeNode.getFile().equals(file)) {
					return projectFileTreeNode;
				}
			}
			if (child.getChildCount()>0) {
				TreeNode foundNode=findTreeNodeForFile(child,file);
				if (foundNode!=null) {
					return foundNode;
				}
			}
		}
		return null;
	}

}
