package com.inkus.infomancerforge.editor.gob;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.data.DataInstance;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.DataInstanceChangeListener;
import com.inkus.infomancerforge.editor.FileGameObjectChangeListener;
import com.inkus.infomancerforge.editor.gob.view.ViewDesigner;
import com.inkus.infomancerforge.editor.swing.DockablePanel;

public class ViewEditor extends DockablePanel implements FileGameObjectChangeListener,DataInstanceChangeListener {
	@SuppressWarnings("unused")
	static private final Logger log=LogManager.getLogger(ViewEditor.class);
	private static final long serialVersionUID = 1L;
	
	private View view;
	private View viewOld=null;
	private AdventureProjectModel adventureProjectModel;
	private ViewDesigner viewDesigner;

	public ViewEditor(View view,AdventureProjectModel adventureProjectModel) {
		super(view.getName(),"view."+view.getUuid(),ImageUtilities.getIcon(FluentUiRegularMZ.WHITEBOARD_24, ImageUtilities.TAB_ICON_COLOR, ImageUtilities.TAB_ICON_SIZE));
		this.adventureProjectModel=adventureProjectModel;
		this.view=view;
		viewOld=(View)SerializationUtils.clone(view);
		setLayout(new BorderLayout());
		adventureProjectModel.addDataInstanceChangeListener(this);
		adventureProjectModel.addFileGameObjectChangeListener(this);
		build();
		refresh();
	}

	public AdventureProjectModel getAdventureProjectModel() {
		return adventureProjectModel;
	}

	public String getTitle() {
		return view.getName();
	}

	public View getView() {
		return view;
	}
	
	public void changed() {
		if ((viewOld!=null && !viewOld.equals(view))) {
			view.touch();
			adventureProjectModel.fireFileGameObjectChange(view,view);
		}
		viewOld=(View)SerializationUtils.clone(view);
	}

	private void refresh() {
		viewDesigner.refresh();
	}
	
	public void fileGameObjectChanged(Object source,FileGameObject fileGameObject) {
		if (source!=view) {
			// If we have gobs of this type in the view we should refresh
			if (fileGameObject instanceof View changedView) {
				if (changedView.getUuid()==view.getUuid()) {
					refresh();
				}
			}else if (fileGameObject instanceof GOB gob) {
				Set<String> gobsIds=new HashSet<>();
				gobsIds.add(gob.getUuid());
				for (var g:adventureProjectModel.getGOBChildren(gob)) {
					gobsIds.add(g.getUuid());
				}
				
				boolean refreshNeeded=false;
				for (var g:view.getGobs()) {
					if (gobsIds.contains(g.getGobReferance().getTypeUuid())) {
						refreshNeeded=true;
					}
				}
				
				if (refreshNeeded) {
					refresh();
				}
			}
		}
	}

	@Override
	public void dataInstanceObjectChanged(Object source, DataInstance dataInstance) {
		if (source!=view) {
			// If we have this GOB instance in the view we should refresh the view
			if (dataInstance instanceof GOBInstance gobInstance) {
				for (var g:view.getGobs()) {
					if (gobInstance.getUuid().equals(g.getGobReferance().getUuid())) {
						refresh();
						break;
					}
				}
			}
		}
	}

	public ViewDesigner getViewDesigner() {
		if (viewDesigner==null) {
			viewDesigner=new ViewDesigner(this,view,adventureProjectModel);
		}
		return viewDesigner;
	}
	
	private void build() {
		setLayout(new BorderLayout());
		
		add(getViewDesigner(),BorderLayout.CENTER);
	}
	
//	private void updatePropertiesSelection() {
//		int[] rows=designTable.getSelectedRows();
//		
//		List<PropertyValues> allProperties=new ArrayList<>();
//		for (int r:rows) {
//			if (r>=0 && r<gob.getPropertyDefinitions().size()) {
//				var gp=gob.getPropertyDefinitions().get(r);
//				allProperties.add(new PropertyValuesGOBProperty(adventureProjectModel,gp));
//			}
//		}
//				
//		propertyEditor.setPropertyValues(gob,allProperties);
//	}
}
