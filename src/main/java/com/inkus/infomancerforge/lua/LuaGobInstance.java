package com.inkus.infomancerforge.lua;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBReferance;
import com.inkus.infomancerforge.editor.AdventureProjectModel;

public class LuaGobInstance {
	static private final Logger log=LogManager.getLogger(LuaGobInstance.class);

	private AdventureProjectModel adventureProjectModel;
	private GOB gob;
	private GOBInstance gobInstance;

	public LuaGobInstance(AdventureProjectModel adventureProjectModel,GOBReferance gobReferance) {
		this(adventureProjectModel,gobReferance.getGobInstance(adventureProjectModel));
	}

	public LuaGobInstance(AdventureProjectModel adventureProjectModel,GOBInstance gobInstance) {
		this.adventureProjectModel=adventureProjectModel;
		this.gobInstance=gobInstance;
		this.gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gobInstance.getGobType());
	}

	//	public LuaTable getArray(List list) {
	//		
	//	}
	//
	@SuppressWarnings("unchecked")
	public LuaValue getFieldValue(String key) {
		try {
			if (key!=null) {
				if ("_uuid".equals(key)) {
					return LuaString.valueOf(gobInstance.getUuid());
				} else {
					for (var gp:adventureProjectModel.getAllGobProperties(gob)) {
						var p=gobInstance.getProperties().get(gp.getGobFieldName());
						if (key.equals(gp.getName()) && p!=null && p.getValue()!=null) {
							switch (gp.getType()) {
							case Boolean:
								if (gp.isArray()) {
									LuaTable luaTable=new LuaTable();
									int pos=1;
									for (var ai:(List<Boolean>)p.getValue()) {
										if (ai!=null) {
											luaTable.rawset(pos++, LuaBoolean.valueOf(ai));
										} else {
											luaTable.rawset(pos++, LuaBoolean.FALSE);
										}
									}
									return luaTable;
								} else {
									return LuaBoolean.valueOf((Boolean)p.getValue());
								}
							case Float:
								if (gp.isArray()) {
									LuaTable luaTable=new LuaTable();
									int pos=1;
									for (var ai:(List<Number>)p.getValue()) {
										if (ai!=null) {
											luaTable.rawset(pos++, LuaNumber.valueOf(ai.doubleValue()));
										} else {
											luaTable.rawset(pos++, LuaNumber.ZERO);
										}
									}
									return luaTable;
								} else {
									return LuaDouble.valueOf((Double)p.getValue());
								}
							case ID:
								if (gp.isArray()) {
									// Cant happen
								} else {
									return LuaString.valueOf((String)p.getValue());
								}
							case Integer:
								if (gp.isArray()) {
									LuaTable luaTable=new LuaTable();
									int pos=1;
									for (var ai:(List<Number>)p.getValue()) {
										if (ai!=null) {
											luaTable.rawset(pos++, LuaInteger.valueOf(ai.intValue()));
										} else {
											luaTable.rawset(pos++, LuaNumber.ZERO);
										}
									}
									return luaTable;
								} else {
									return LuaInteger.valueOf((Integer)p.getValue());
								}
							case String:
								if (gp.isArray()) {
									LuaTable luaTable=new LuaTable();
									int pos=1;
									for (var ai:(List<String>)p.getValue()) {
										if (ai!=null) {
											luaTable.rawset(pos++, LuaString.valueOf(ai));
										} else {
											luaTable.rawset(pos++, LuaString.valueOf(""));
										}
									}
									return luaTable;
								} else {
									return LuaString.valueOf((String)p.getValue());
								}
							case GOB:
								// If this is an embedded value we send the bog if not send a string name. (Type should not be needed)
								GOB gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, gp.getGobType());
								if (gob!=null){
									switch (gob.getType()) {
									case Embedded:
										if (gp.isArray()) {
											LuaTable luaTable=new LuaTable();
											int pos=1;
											for (var gi:(List<GOBInstance>)p.getValue()) {
												luaTable.rawset(pos++, adventureProjectModel.getAdventureLuaEnviroment().getLuaValue(new LuaGobInstance(adventureProjectModel, (GOBInstance)gi)));
											}
											return luaTable;
										} else {
											return adventureProjectModel.getAdventureLuaEnviroment().getLuaValue(new LuaGobInstance(adventureProjectModel, (GOBInstance)p.getValue()));
										}
									case Base:
										if (gp.isArray()) {
											LuaTable luaTable=new LuaTable();
											int pos=1;
											for (var gi:(List<GOBInstance>)p.getValue()) {
												luaTable.rawset(pos++, LuaString.valueOf(gi.getName()));
											}
											return luaTable;
										} else {
											return LuaString.valueOf(((GOBInstance)p.getValue()).getName());
										}
									case Linked:
										if (gp.isArray()) {
											LuaTable luaTable=new LuaTable();
											int pos=1;
											for (var gi:(List<GOBInstance>)p.getValue()) {
												luaTable.rawset(pos++, LuaString.valueOf(gi.getUuid()));
											}
											return luaTable;
										} else {
											return LuaString.valueOf(((GOBInstance)p.getValue()).getUuid());
										}
									}
									//								if (gob.getType()==Type.Embedded) {
									//									if (gp.isArray()) {
									//										LuaTable luaTable=new LuaTable();
									//										int pos=1;
									//										for (var gi:(List<GOBInstance>)p.getValue()) {
									//											luaTable.rawset(pos++, adventureProjectModel.getAdventureLuaEnviroment().getLuaValue(new LuaGobInstance(adventureProjectModel, (GOBInstance)gi)));
									//										}
									//										return luaTable;
									//									} else {
									//										return adventureProjectModel.getAdventureLuaEnviroment().getLuaValue(new LuaGobInstance(adventureProjectModel, (GOBInstance)p.getValue()));
									//									}
									//								} else {
									//									if (gp.isArray()) {
									//										LuaTable luaTable=new LuaTable();
									//										int pos=1;
									//										for (var gi:(List<GOBInstance>)p.getValue()) {
									//											luaTable.rawset(pos++, LuaString.valueOf(gi.getName()));
									//										}
									//										return luaTable;
									//									} else {
									//										return LuaString.valueOf(((GOBInstance)p.getValue()).getName());
									//									}
									//								}
								}
								//						case Audio:
								//						case Image:
							default:
								log.error("Unable to read value of type "+gp.getType());
								break;
							}
							break;
						}
					}

				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return LuaValue.NIL;
	}

	public void setFieldValue(String key,LuaValue value) {
		if (key!=null) {
			for (var gp:gob.getPropertyDefinitions()) {
				var p=gobInstance.getProperties().get(gp.getGobFieldName());
				if (key.equals(gp.getName()) && p!=null) {
					switch (gp.getType()) {
					case Boolean:
						p.setValue(value.checkboolean());
					case Float:
						p.setValue(value.checkdouble());
					case ID:
						p.setValue(value.checkstring());
					case Integer:
						p.setValue(value.checkint());
					case String:
						p.setValue(value.checkstring());
					case GOB:
						//					case Audio:
						//					case Image:
					default:
						log.error("Unable to read value of type "+gp.getType());
						break;
					}
					adventureProjectModel.fireDataInstanceChange(this, gobInstance);
					break;
				}
			}
		}
	}

	public LuaTable getFields() {
		LuaTable keys=new LuaTable();
		Set<String> doneKeys=new HashSet<>();
		int pos=1;
		keys.rawset(pos++, "_uuid");
		doneKeys.add("_uuid");
		for (var gp:adventureProjectModel.getAllGobProperties(gob)) {
			if (gobInstance.getProperties().containsKey(gp.getGobFieldName()) && gp.getName()!=null && gp.getName().length()>0 && !doneKeys.contains(gp.getName())) {
				keys.rawset(pos++, gp.getName());
				doneKeys.add(gp.getName());
			}
		}
		return keys;
	}
}
