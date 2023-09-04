package com.inkus.infomancerforge.lua;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;

import com.inkus.infomancerforge.ImageUtilities;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.actions.BaseAction;

public class LuaSwingAction {
	static private final Logger log=LogManager.getLogger(LuaSwingAction.class);

	public enum ActionType {
		Menu,
		GOBInstance,
		GOBProperty,
		View
	}

	// Some items are marked as transient to prevent them from json serialization
	
	private String name; // Name 
	private String path; // Path of submenu / / The will be a submenu of that path.
	private Integer weight; // Weight of item in meny list for sorting
	private transient String icon; // Icon with a base64 png or a svg in clear text.
	private String description; // 
	private String actionId; //Used to identify this action for overwriting and removal 
	private String targetId; //If GOB or View this is a UUID, for a field this would be the Field name 
	private Boolean adjustColors=true;  
	private ActionType actionType;
	private transient LuaFunction action;
	
	private transient AdventureProjectModel adventureProjectModel;
	
	public LuaSwingAction(AdventureProjectModel adventureProjectModel) {
		this.adventureProjectModel=adventureProjectModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		// Build icon from this is it base 64 or a svg
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public LuaFunction getAction() {
		return action;
	}

	public void setAction(LuaFunction action) {
		this.action = action;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Boolean getAdjustColors() {
		return adjustColors;
	}

	public void setAdjustColors(Boolean adjustColors) {
		this.adjustColors = adjustColors;
	}

	public boolean aimedAt(GOB gob) {
		return actionType==ActionType.GOBInstance && gob.getUuid().equals(targetId);
	}

	public boolean aimedAt(View view) {
		return actionType==ActionType.View && view.getUuid().equals(targetId);
	}

	public boolean aimedAt(GOBPropertyDefinition def) {
		return actionType==ActionType.GOBProperty && def.getGobFieldName().equals(targetId);
	}
	
	public boolean isMenu() {
		return actionType==ActionType.Menu;
	}
	
	public boolean isOk() {
		boolean ok=true;
		
		ok=ok && actionId!=null && actionId.length()>0;
		ok=ok && ((name!=null && name.length()>0) || (icon!=null && icon.length()>0));
		ok=ok && action!=null;
		ok=ok && actionType!=null;
		
		return ok;
	}

	public Action getMenuAction() {
		return new LuaSwingActionBinder(adventureProjectModel,ImageUtilities.MENU_ICON_SIZE,!adjustColors?null:ImageUtilities.MENU_ICON_COLOR);
	}
	
	public Action getButtonAction() {
		return new LuaSwingActionBinder(adventureProjectModel,ImageUtilities.BUTTON_ICON_SIZE,!adjustColors?null:ImageUtilities.BUTTON_ICON_COLOR);
	}

	@SuppressWarnings("unused")
	public class LuaSwingActionBinder extends BaseAction {
		private static final long serialVersionUID = 1L;

		private View view;
		private List<GOBInstance> gobInstances;
		
		LuaSwingActionBinder(AdventureProjectModel adventureProjectModel,int size,Color color){
			super(adventureProjectModel);
			
			if (icon!=null && icon.length()>0) {
				boolean svg=icon.indexOf("<")!=-1;
				if (svg) {
					putValue(SMALL_ICON, ImageUtilities.getIconSVG(icon, color, size));
				} else {
					putValue(SMALL_ICON, ImageUtilities.getIconBase64(icon, color, size));
				}
			}
			
			putValue(NAME, name);
			putValue(SHORT_DESCRIPTION, description);
			setEnabled(actionType==ActionType.Menu);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (action!=null) {
					switch (actionType) {
					case GOBInstance:
						// TODO: Call with gobinstances
						break;
					case GOBProperty:
						// TODO: Call with gobinstances
						break;
					case View:
						// TODO: Call with view as an instance
						break;
					case Menu:
					default:
//						action.call();
						adventureProjectModel.getAdventureLuaEnviroment().execute("Button:"+path+"\\"+name,action);
						
						break;
					}
				}
			} catch (LuaError le) {
				log.error(le.getMessage(),le);
			}
		}
		
		public void setView(View view) {
			if (actionType==ActionType.View) {
				this.view=view;
				setEnabled(view!=null);
			}
		}
		
		public void setGOBInstances(List<GOBInstance> gobInstances) {
			if (actionType==ActionType.GOBProperty || actionType==ActionType.GOBInstance) {
				this.gobInstances=gobInstances;
				setEnabled(gobInstances!=null && gobInstances.size()>0);
			}
		}
		
	}
}


