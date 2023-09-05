package com.inkus.infomancerforge.lua;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.text.Document;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;
import org.luaj.vm2.parser.TokenMgrError;

import com.inkus.infomancerforge.ErrorUtilities;
import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.Setting;
import com.inkus.infomancerforge.beans.SettingsBag;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBReferance;
import com.inkus.infomancerforge.beans.sourcecode.SourceCode;
import com.inkus.infomancerforge.beans.sourcecode.SourceCodeErrorListener;
import com.inkus.infomancerforge.beans.sourcecode.SourceErrors;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.editor.AdventureProjectModel;
import com.inkus.infomancerforge.editor.FileGameObjectChangeListener;
import com.inkus.infomancerforge.utils.OutputStreamLimitedDocument;
import com.inkus.infomancerforge.utils.WeakList;

import snap.bean.ObjectMapper;

public class AdventureLuaEnviroment implements FileGameObjectChangeListener {
	static private final Logger log=LogManager.getLogger(AdventureLuaEnviroment.class);

	private AdventureProjectModel adventureProjectModel;
	private Globals globals = JsePlatform.standardGlobals();
    private OutputStreamLimitedDocument luaStdOutputStream;
    private PrintStream luaPrintStream;

	private Map<String,Set<SourceCode>> modulesUsed=new HashMap<>(); 
	private Map<String,LuaTable> modules=new HashMap<>();
	private Map<String,List<LuaSwingAction>> luaActionMap=new HashMap<>();
	private List<SourceCodeErrorListener> sourceCodeErrorListeners=new WeakList<>();
	

	private SourceCode currentlyExecuting=null;
	
	private Stack<String> currentScript=new Stack<>();
	
	private List<LuaActionChangeListener> luaActionChangeListeners=new WeakList<>();
	
	public AdventureLuaEnviroment(AdventureProjectModel adventureProjectModel) {
		this.adventureProjectModel=adventureProjectModel;
		adventureProjectModel.addFileGameObjectChangeListener(this);
		setUpGlobals();
	}

	public void addSourceCodeErrorListener(SourceCodeErrorListener sourceCodeErrorListener) {
		synchronized (sourceCodeErrorListeners) {
			removeSourceCodeErrorListener(sourceCodeErrorListener);
			sourceCodeErrorListeners.add(sourceCodeErrorListener);
		}
	}
	
	public void removeSourceCodeErrorListener(SourceCodeErrorListener sourceCodeErrorListener) {
		sourceCodeErrorListeners.remove(sourceCodeErrorListener);
	}
	
	private void setUpGlobals() {
		synchronized (globals) {
		    try {
				LoadState.install(globals);
				LuaC.install(globals);
				globals.load(new DebugLib());
				globals.set("debug", LuaValue.NIL);
		    	
		    	luaStdOutputStream = new OutputStreamLimitedDocument(128*1024, 512);
				luaPrintStream = new PrintStream(luaStdOutputStream, true, "utf-8");
				globals.STDOUT=luaPrintStream;
				
				globals.set("environment", CoerceJavaToLua.coerce(new LuaEnvironment()));
				globals.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/inkus/infomancerforge/lua/lua5.2.lua")), "lua5.2.lua").call();
				globals.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/inkus/infomancerforge/lua/modules.lua")), "modules.lua").call();
				globals.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/inkus/infomancerforge/lua/gobs.lua")), "gobs.lua").call();
			} catch (UnsupportedEncodingException e) {
				ErrorUtilities.showFatalException(e);
			}
		}
	}

	public void loadAllCurrent() {
		// clear modules
		modules.clear();
		modulesUsed.clear();
		for (SourceCode sourceFile:adventureProjectModel.getNamedResourceModel(SourceCode.class)) {
			if (sourceFile.getExtension().equals("lua")) {
				try {
					System.out.println("Loading "+sourceFile.getName());
					fileGameObjectCompile(null, sourceFile,new HashSet<SourceCode>());
				} catch (LUAScriptError e) {
					log.warn(e.getMessage(),e);
				}
			}
		}
	}

	public Document getStdOutDocument() {
		return luaStdOutputStream.getDocument();
	}
	
	public boolean isModule(SourceCode sourceCode) {
		String path=FilenameUtils.separatorsToUnix(sourceCode.getProjectPath().toLowerCase());
		return path.startsWith("modules/") || path.indexOf("/modules/")!=-1;
	}

	public String getModuleName(SourceCode sourceCode) {
		return sourceCode.getName().substring(0,sourceCode.getName().length()-4);
	}
	
	@Override
	public void fileGameObjectChanged(Object source, FileGameObject fileGameObject) {
		if (source!=this && fileGameObject instanceof SourceCode sourceCode && "lua".equalsIgnoreCase(sourceCode.getExtension())) {
			if (isModule(sourceCode)) {
				loadAllCurrent();
			} else {
				fileGameObjectCompile(source, fileGameObject,new HashSet<SourceCode>());
			}
		}
	}
	
	public boolean mustExclude(FileGameObject fileGameObject) {
		if (fileGameObject instanceof SourceCode sourceCode && "lua".equalsIgnoreCase(sourceCode.getExtension())) {
			return sourceCode.getProjectPath().startsWith("\\Build\\");
		}
		return true;
	}
	
	public void fileGameObjectCompile(Object source, FileGameObject fileGameObject,Set<SourceCode> processed) {
		if (source!=this && fileGameObject instanceof SourceCode sourceCode && !mustExclude(fileGameObject)) {
			processed.add(sourceCode);
			// This file needs to recompile.
			if (isModule(sourceCode)){
				// We need to recompile all source files that directly or indirectly use this module.
				String moduleName=getModuleName(sourceCode);
				modules.remove(moduleName);
				Set<SourceCode> usedIn=modulesUsed.get(moduleName);
				modulesUsed.remove(moduleName);
				if (usedIn!=null){
					for (var sc:usedIn) {
						if (!processed.contains(sc)) {
							fileGameObjectCompile(source, sc, processed);
						}
					}
				}
			} else {
				// Only the current file needs to be recompiled.
				execute(sourceCode);
			}
		}
	}
	
	public LUAScriptError parse(String name,String lua) {
		LuaParser parser=null;
		try {
			parser = new LuaParser(new StringReader(lua));
			Chunk chunk = parser.Chunk();
			// TODO: Use visitor to build a token lexicon. Editors can borrows from the ones required and use there own too.
			chunk.accept( new Visitor() {
				public void visit(Exp.NameExp exp) {
				}
			});
		} catch (TokenMgrError|ParseException e) {
			int column=parser.getCharStream().getBeginColumn();
			int length=-1;
			if (column!=-1) {
				length=parser.getCharStream().getEndColumn()-column;
				column=parser.getCharStream().bufpos-length;
				length++;
			}
			return new LUAScriptError(parser.getCharStream().getBeginLine()-1, name, e.getMessage(),column,length);
		}
		
		return null;
	}

	private LuaValue executeLuaScript(String name, String script) throws LuaError {
		System.out.println("Attempted Execution:"+name);
		return  globals.load(script, name, globals).call();
	}
	
	public LuaValue execute(SourceCode sourceCode) throws LuaError {
		LuaValue result=null;
		SourceCode oldCurrent=currentlyExecuting;
		currentlyExecuting=sourceCode;
		try {
			sourceCode.getErrors().clear();
			result=execute(sourceCode.getName(),sourceCode.getCode());
		} catch (LuaError luaError) {
			sourceCode.getErrors().add(new SourceErrors(luaError));
		} finally {
			currentlyExecuting=oldCurrent;
		}
		if (sourceCode.getErrors().size()>0) {
			System.out.println("ERRORS:"+sourceCode.getName());
			for (var e:sourceCode.getErrors()) {
				System.out.println(""+e.getLineNumber()+":"+e.getDescription());
			}
			System.out.println("ERRORS DONE:"+sourceCode.getName());
		}
		for (var l:sourceCodeErrorListeners) {
			l.errorsUpdated(sourceCode);
		}
		return result;
	}

	public LuaValue execute(String name,String lua) throws LuaError {
		LuaValue result=null;
		try {
			currentScript.push(name);
			clearLuaSwingActionGroup(name);
			
			result=executeLuaScript(name, lua);
		} catch (LuaError luaError) {
			try {
				luaStdOutputStream.write((luaError.getMessage()+"\n").getBytes());
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			log.warn(luaError.getMessage(),luaError);
			throw luaError;
		} finally {
			currentScript.pop();
		}
		
		return result!=null?result:null;
	}
	
	public LuaValue execute(String name,LuaFunction function) throws LuaError{
		LuaValue result=null;
		synchronized (globals) {
			try {
				currentScript.push(name);
				clearLuaSwingActionGroup(name);
				
				result=function.call();
			} catch (LuaError luaError) {
				// find sourcecode.
				String description=luaError.getMessage();
				System.out.println("description="+description);
				if (description.indexOf(":")!=-1) {
					String fileName=description.substring(0,description.indexOf(":"));
					SourceCode sourceCode=adventureProjectModel.getNamedResourceByName(SourceCode.class, fileName);
					if (sourceCode!=null) {
						System.out.println("found fileName="+fileName);
						sourceCode.getErrors().clear();
						sourceCode.getErrors().add(new SourceErrors(luaError));
						for (var l:sourceCodeErrorListeners) {
							l.errorsUpdated(sourceCode);
						}
					}
				}
				throw luaError;
			} finally {
				currentScript.pop();
			}
			
		}
		return result;
	}
	
	public String evaluate(String lua) {
		LuaValue v;
		synchronized (globals) {
			v=globals.load("return "+lua).call();
		}
		return v.tojstring();
	}

	public String evaluate(String lua,LuaGobInstance gobInstance) {
		try {
			LuaValue instance=globals.get("buildInstance").call(CoerceJavaToLua.coerce(gobInstance));

			var v=globals.load("return function(self) return "+lua+" end", "View Summary", globals).call().invoke(instance);

			return v.tojstring();
		} catch (LuaError e) {
			log.warn(e.getMessage(),e);
			return "err";
		}
	}

	public LuaValue getLuaValue(LuaGobInstance gobInstance) {
		if (gobInstance==null) {
			return new LuaTable();
		}else {
			return globals.get("buildInstance").call(CoerceJavaToLua.coerce(gobInstance));
		}
	}
	
	public void addLuaActionChangeListener(LuaActionChangeListener luaActionChangeListener) {
		removeLuaActionChangeListener(luaActionChangeListener);
		luaActionChangeListeners.add(luaActionChangeListener);
	}
	
	public void removeLuaActionChangeListener(LuaActionChangeListener luaActionChangeListener) {
		luaActionChangeListeners.remove(luaActionChangeListener);
	}
	
	public List<LuaSwingAction> getActions(GOB gob){
		List<LuaSwingAction> list=null;
		for (var l:luaActionMap.values()) {
			for (var a:l) {
				if (a.aimedAt(gob)) {
					if (list==null) {
						list=new ArrayList<>();
					}
					list.add(a);
				}
			}
		}
		return list;
	}
	
	public List<LuaSwingAction> getActions(GOBPropertyDefinition propertyDefinition){
		List<LuaSwingAction> list=null;
		for (var l:luaActionMap.values()) {
			for (var a:l) {
				if (a.aimedAt(propertyDefinition)) {
					if (list==null) {
						list=new ArrayList<>();
					}
					list.add(a);
				}
			}
		}
		return list;
	}
	
	public List<LuaSwingAction> getActions(View view){
		List<LuaSwingAction> list=null;
		for (var l:luaActionMap.values()) {
			for (var a:l) {
				if (a.aimedAt(view)) {
					if (list==null) {
						list=new ArrayList<>();
					}
					list.add(a);
				}
			}
		}
		return list;
	}
	
	private void addLuaSwingAction(LuaSwingAction luaSwingAction) {
		String group=currentScript.peek();
		if (!luaActionMap.containsKey(group)) {
			synchronized (luaActionMap) {
				if (!luaActionMap.containsKey(group)) {
					luaActionMap.put(group,new ArrayList<>());
				}				
			}
		}
		log.warn("Add action '"+StorageUtilities.gson.toJson(luaSwingAction)+"'");

		luaActionMap.get(group).add(luaSwingAction);
		// Call menu listeners
		for (var luaActionChangeListener:luaActionChangeListeners) {
			luaActionChangeListener.luaActionAdded(luaSwingAction);
		}
	}
	
	private void clearLuaSwingActionGroup(String group) {
		if (luaActionMap.containsKey(group)) {
			synchronized (luaActionMap) {
				if (luaActionMap.containsKey(group)) {
					var actions=luaActionMap.get(group);
					luaActionMap.remove(group);
					// Call listeners to remove these actions
					for (var action:actions) {
						for (var luaActionChangeListener:luaActionChangeListeners) {
							luaActionChangeListener.luaActionRemoved(action);
						}
					}
				}				
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void pushTableIntoBean(LuaTable table, Object bean) {
		for (LuaValue key:table.keys()) {
			if (key.isstring()) {
				String field=key.toString().substring(0,1).toUpperCase()+key.toString().substring(1);
				if (ObjectMapper.hasField(field, bean)) {
					Class<?> fieldType=ObjectMapper.getFieldType(field, bean);
					LuaValue value=table.get(key);
					if (Boolean.class.isAssignableFrom(fieldType) && value.isboolean()) {
						ObjectMapper.setFieldValue(field, bean, value.checkboolean());
					} else if (Integer.class.isAssignableFrom(fieldType) && value.isint()) {
						ObjectMapper.setFieldValue(field, bean, value.checkint());
					} else if (Long.class.isAssignableFrom(fieldType) && value.islong()) {
						ObjectMapper.setFieldValue(field, bean, value.checklong());
					} else if (Double.class.isAssignableFrom(fieldType) && value.isnumber()) {
						ObjectMapper.setFieldValue(field, bean, value.checkdouble());
					} else if (Float.class.isAssignableFrom(fieldType) && value.isnumber()) {
						ObjectMapper.setFieldValue(field, bean, (float)value.checkdouble());
					} else if (String.class.isAssignableFrom(fieldType) && value.isstring()) {
						ObjectMapper.setFieldValue(field, bean, value.toString());
					} else if (Enum.class.isAssignableFrom(fieldType) && value.isstring()) {
						ObjectMapper.setFieldValue(field, bean, Enum.valueOf((Class<Enum>) fieldType, value.toString()));
					} else if (LuaFunction.class.isAssignableFrom(fieldType) && value.isfunction()) {
						ObjectMapper.setFieldValue(field, bean, value.checkfunction());
					} else if (Object.class.isAssignableFrom(fieldType) && value.isboolean()) {
						ObjectMapper.setFieldValue(field, bean, value.checkboolean());
					} else if (Object.class.isAssignableFrom(fieldType) && value.isstring()) {
						ObjectMapper.setFieldValue(field, bean, value.toString());
					} else if (Object.class.isAssignableFrom(fieldType) && value.isint()) {
						ObjectMapper.setFieldValue(field, bean, (int)value.checkdouble());
					} else if (Object.class.isAssignableFrom(fieldType) && value.isnumber()) {
						ObjectMapper.setFieldValue(field, bean, (float)value.checkdouble());
					} else {
						log.warn("Unable to map type field '"+field+"' in object '"+bean.getClass().getName()+"' from LuaValue "+value.getClass().getName());
					}
				} else {
					log.warn("Unable to find field '"+field+"' in object '"+bean.getClass().getName()+"'");
				}
			}
		}
	}
	

	class LuaEnvironment {
		public LuaValue getInstance(String typeUuid, String uuid){
			return CoerceJavaToLua.coerce(new LuaGobInstance(adventureProjectModel, new GOBReferance(typeUuid, uuid)));
		}

		public LuaValue getGobByName(String name) {
			return CoerceJavaToLua.coerce(adventureProjectModel.getNamedResourceByName(GOB.class, name));
		}

		public LuaValue getGobByUuid(String uuid) {
			return CoerceJavaToLua.coerce(adventureProjectModel.getNamedResourceByUuid(GOB.class, uuid));
		}

		public LuaValue getGobInstacesByUuid(String uuid) {
			LuaTable instances=new LuaTable();
			int pos=1;
			GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, uuid);
			var table=adventureProjectModel.getGOBDataTableModel(gob);
			for (var gi:table.getRows()) {
				instances.rawset(pos++, CoerceJavaToLua.coerce(new LuaGobInstance(adventureProjectModel, gi)));
			}
			return instances;
		}
		
		public LuaTable getAllGobNames() {
			LuaTable names=new LuaTable();
			int pos=1;
			for (var gob:adventureProjectModel.getNamedResourceModel(GOB.class)) {
				names.rawset(pos++, gob.getName());
			}
			return names;
		}
		
		public LuaTable getModule(String name) throws LUAScriptError {
			System.out.println("Ednter get module:"+name);
			LuaTable module=modules.get(name);
			if (module==null) {
				var model=adventureProjectModel.getNamedResourceModel(SourceCode.class);
				System.out.println("moel size:"+model.size());
				for (var sourceCode:model) {
					System.out.println("sc:"+sourceCode.getName());
					if ("lua".equalsIgnoreCase(sourceCode.getExtension()) && isModule(sourceCode) && getModuleName(sourceCode).equals(name)) {
						//System.out.println("sc[module]:"+sourceCode.getName());
						System.out.println("sc[module]:"+sourceCode.getName()+";"+sourceCode.getProjectPath());
						if (module!=null) {
							// TODO: See if you can create a better error
							System.out.println("executing:"+currentlyExecuting.getName());
							throw new LUAScriptError(0,currentlyExecuting.getName(),"The module '"+name+"' is defined in more then one place.",-1,-1); 
						} else {
							LuaValue result=execute(sourceCode);
							if (result.istable()) {
								System.out.println("Getting table for "+name);
								module=result.checktable();
							} else {
								throw new LUAScriptError(0,currentlyExecuting.getName(),"The module '"+name+"' is defined but when executed does not return a table.",-1,-1); 
							}
						}
					}
				}
				
				if (module!=null) {
					modules.put(name,module);
				} else {
					throw new LUAScriptError(0,currentlyExecuting.getName(),"The module '"+name+"' can not be found in the system.",-1,-1); 
				}
			}
			
			// Make sure we know of this reference
			if (module!=null) {
				if (!modulesUsed.containsKey(name)) {
					modulesUsed.put(name, new HashSet<>());
				}
				modulesUsed.get(name).add(currentlyExecuting);
			}
			
			System.out.println("Exit get module:"+name);
			return modules.get(name); 
		}
		
		public boolean addAction(LuaTable details) {
			LuaSwingAction action=new LuaSwingAction(adventureProjectModel);
			pushTableIntoBean(details, action);
			if (action.isOk()) {
				addLuaSwingAction(action);
			} else {
				log.warn("Unable to add action '"+StorageUtilities.gson.toJson(action)+"'");
			}
			return action.isOk();
		}
		
		public boolean saveFile(String filename,String content) {
			File outputFile=new File(adventureProjectModel.getProject().getPath()+"/"+filename);
			if (outputFile.getParentFile().exists() || outputFile.getParentFile().mkdirs()) {
				try (FileWriter writer=new FileWriter(outputFile)){
					writer.append(content);
					return true;
				} catch (IOException e) {
					log.warn("Unable to save file '"+filename+"'",e);
				}
			} else {
				log.warn("Unable to save file '"+filename+"'");
			}
			return false;
		}
		
		public String loadFile(String filename) {
			File outputFile=new File(adventureProjectModel.getProject().getPath()+"/"+filename);
			if (outputFile.exists()) {
				try {
					return FileUtils.readFileToString(outputFile,(String)null);
				} catch (IOException e) {
					log.warn("Unable to load file '"+filename+"'",e);
				}
			}
			return null;
		}
		
		public void addSettings(String name,LuaTable luaSettings) {
			var settingsModel=adventureProjectModel.getSettingsModel();
			
			List<Setting> settings=new ArrayList<>();
			System.out.println("#luaSettings="+luaSettings.length());
			for (int t=1;t<=luaSettings.length();t++) {
				Setting setting=new Setting();
				pushTableIntoBean(luaSettings.get(t).checktable(), setting);
				settings.add(setting);
			}
			
			SettingsBag settingsBag=new SettingsBag();
			settingsBag.setName(name);
			settingsBag.setSettings(settings);
			settingsModel.addSettings(name, settingsBag);
		}
		
		public LuaTable getSettings(String name) {
			var settingsModel=adventureProjectModel.getSettingsModel();
			var bag=settingsModel.getSettings().get(name);
			
			LuaTable settings=new LuaTable();
			for (var s:bag.getSettings()) {
				System.out.println("S:"+s.getField()+"="+s.getValue());
				if (s.getField()!=null) {
					switch (s.getSettingType()) {
					case Boolean:
						settings.rawset(LuaString.valueOf(s.getField()), 
								LuaBoolean.valueOf(true==(boolean)(s.getValue())));
						break;
					case Choice:
					case Code:
					case String:
					default:
						settings.rawset(LuaString.valueOf(s.getField()), 
								LuaString.valueOf((String)(s.getValue())));
					}
				}
			}
			return settings;
		}
		
		public void setSettings(String name,String field,Object value) {
			var settingsModel=adventureProjectModel.getSettingsModel();
			var settings=settingsModel.getSettings().get(name);
			System.out.println("name="+name+" ,field="+field+", object="+value+" ,type="+value.getClass().getName());
			if (settings!=null) {
				if (value instanceof String luaString) {
					settings.setSetting(field,luaString);
				} else if (value instanceof Boolean luaBoolean) {
					settings.setSetting(field,luaBoolean);
				}
			}
			settingsModel.saveSettings();
		}
	}
}
