package com.inkus.infomancerforge.editor.sourcecode;

import java.awt.BorderLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.sourcecode.SourceCode;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.FileGameObjectChangeListener;
import com.inkus.infomancerforge.editor.swing.DockablePanel;

public abstract class SourceCodeEditor extends DockablePanel implements FileGameObjectChangeListener {
	@SuppressWarnings("unused")
	static private final Logger log=LogManager.getLogger(SourceCodeEditor.class);
	private static final long serialVersionUID = 1L;

	protected AdventureProjectModel adventureProjectModel;
	protected SourceCode sourceCode;

	public SourceCodeEditor(SourceCode sourceCode,AdventureProjectModel adventureProjectModel) {
		super(sourceCode.getName(),"sourceCode."+sourceCode.getUuid(), ImageUtilities.getSourceCodeIcon(sourceCode.getExtension(), ImageUtilities.TAB_ICON_COLOR, ImageUtilities.TAB_ICON_SIZE));
		this.adventureProjectModel=adventureProjectModel;
		this.sourceCode=sourceCode;
		setLayout(new BorderLayout());
		adventureProjectModel.addFileGameObjectChangeListener(this);
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

	public void fileGameObjectChanged(Object source,FileGameObject fileGameObject) {
//		if (source!=view) {
//			// If we have gobs of this type in the view we should refresh
//			if (fileGameObject instanceof View changedView) {
//				if (changedView.getUuid()==view.getUuid()) {
//					refresh();
//				}
//			}else if (fileGameObject instanceof GOB gob) {
//				Set<String> gobsIds=new HashSet<>();
//				gobsIds.add(gob.getUuid());
//				for (var g:adventureProjectModel.getGOBChildren(gob)) {
//					gobsIds.add(g.getUuid());
//				}
//				
//				boolean refreshNeeded=false;
//				for (var g:view.getGobs()) {
//					if (gobsIds.contains(g.getGobReferance().getTypeUuid())) {
//						refreshNeeded=true;
//						//System.out.println("Recalc Size");
//						//view.findReferance(g.getGobReferance()).recalcSize(adventureProjectModel);
//					}
//				}
//				
//				if (refreshNeeded) {
//					refresh();
//				}
//			}
//		}
	}	
}
