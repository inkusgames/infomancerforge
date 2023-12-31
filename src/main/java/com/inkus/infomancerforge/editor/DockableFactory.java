package com.inkus.infomancerforge.editor;

import java.io.File;

import javax.swing.tree.TreeNode;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.views.View;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;

public class DockableFactory implements SingleCDockableFactory {

	
	private  ProjectControllerActionManager projectControllerActionManager;
	private ProjectItemEditorFactory projectItemEditorFactory;
	private AdventureProjectModel adventureProjectModel;
	
	public DockableFactory(AdventureProjectModel adventureProjectModel,ProjectControllerActionManager projectControllerActionManager) {
		this.adventureProjectModel=adventureProjectModel;
		this.projectControllerActionManager=projectControllerActionManager;
		this.projectItemEditorFactory=projectControllerActionManager.getProjectItemEditorFactory();
	}

	@Override
	public SingleCDockable createBackup(String dockableId) {
		TreeNode node=null;
		FileGameObject fileGameObject=null;
		if (dockableId.startsWith("gob.")) {
			fileGameObject=adventureProjectModel.getNamedResourceByUuid(GOB.class, dockableId.substring(4));
		} else if (dockableId.startsWith("view.")) {
			fileGameObject=adventureProjectModel.getNamedResourceByUuid(View.class, dockableId.substring(5));
		} else if (dockableId.startsWith("sourceCode.")) {
			 //  \Build\data.lua
			String uuid=dockableId.substring(11);
			File file=new File(adventureProjectModel.getProject().getPath()+"\\"+adventureProjectModel.getProject().getResourcePathUUID(uuid));
			System.out.println(">>>>>>>>>> "+dockableId);
			System.out.println("uuid:"+uuid);
			System.out.println("Path:"+file.getAbsolutePath());
			node=adventureProjectModel.getAdventureProjectTreeModel().findTreeNodeForFile(file);
		} else {
			System.out.println(">>"+dockableId);
		}
		
		if (fileGameObject!=null && node==null) {
			node=adventureProjectModel.getAdventureProjectTreeModel().findTreeNodeForFileGameObject(fileGameObject);
		}
		
		if (node!=null) {
			var dockable = projectItemEditorFactory.buildNewEditor(node);
			var editor = new DefaultSingleCDockable(dockable.getPersistentId(), dockable.getIcon(), dockable.getTitle(), dockable);

			projectControllerActionManager.addEditor(node, dockable, editor);
			
			return editor;
		}
		return null;
	}

}
