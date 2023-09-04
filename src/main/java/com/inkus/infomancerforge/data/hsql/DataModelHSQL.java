package com.inkus.infomancerforge.data.hsql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hsqldb.jdbc.JDBCArray;

import com.inkus.infomancerforge.ErrorUtilities;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBProperty;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition.Type;
import com.inkus.infomancerforge.data.DataModel;
import com.inkus.infomancerforge.editor.AdventureProjectModel;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

public class DataModelHSQL implements DataModel {
	static private final Logger log=LogManager.getLogger(DataModelHSQL.class);

	private Connection connection;
	private boolean inTransaction=false;
	
	private AdventureProjectModel adventureProjectModel; 
	
	private Map<String,GOBInstance> loadedGobInstances=new HashMap<>();

	public DataModelHSQL(File dataLocation,AdventureProjectModel adventureProjectModel) throws SQLException, ClassNotFoundException {
		this.adventureProjectModel=adventureProjectModel;
		dataLocation.getParentFile().mkdirs();
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		connection = DriverManager.getConnection("jdbc:hsqldb:file:"+dataLocation.getAbsolutePath()+";shutdown=true", "SA", "");
	}

	private void executeSQL(String sql) {
		try (Statement s=connection.createStatement()){
			s.execute(sql);
		} catch (SQLException e) {
			// TODO: Should this be fatal? I think so
			ErrorUtilities.showFatalException(e);
		}
	}

	@SuppressWarnings("unused")
	private void printResults(ResultSet r) throws SQLException {
		var rsmd = r.getMetaData();
		for (int t=1;t<=rsmd.getColumnCount();t++) {
			System.out.print("|");
			System.out.print(rsmd.getColumnName(t));
			System.out.print("(");
			System.out.print(rsmd.getColumnTypeName(t));
			System.out.print(")");
		}
		while (r.next()) {
			for (int t=1;t<=rsmd.getColumnCount();t++) {
				System.out.print("|");
				System.out.print(r.getObject(t));
			}
			System.out.println("|");
		}
	}

	private String getTypeSQL(Type type,boolean array) {
		StringBuffer result=new StringBuffer();
		switch (type) {
		case Boolean:
			result.append("BOOLEAN");
			break;
		case Float:
			result.append("DOUBLE");
			break;
		case Integer:
			result.append("INTEGER");
			break;
		case ID:
		case GOB:
//		case Image:
//		case Audio:
//			result.append("VARCHAR(255)");
//			break;
		case String:
			result.append("VARCHAR(4096)");
			break;
		default:
			log.warn("HSQL Does not know how to handle the type '"+type.name()+"' using varchar(4096)");
			result.append("VARCHAR(4096)");
			break;
		}
		if (array) {
			result.append(" ARRAY");
		}
		return result.toString();
	}

	private boolean tableExists(String name) {
		try (PreparedStatement s=connection.prepareStatement("SELECT count(*) FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME=? AND TABLE_TYPE='TABLE'")){
			s.setString(1, name);
			var r=s.executeQuery();
			r.next();
			return r.getInt(1)==1;
		} catch (SQLException e) {
			// TODO: Should this be fatal? I think so
			ErrorUtilities.showFatalException(e);
			return false;
		}
	}

	private void createNewTable(GOB gob, List<GOBPropertyDefinition> fullProperties) throws SQLException {
		StringBuffer sb=new StringBuffer("CREATE CACHED TABLE ");
		sb.append("GOB_"+gob.getGobTableName());
		sb.append("(UUID VARCHAR(36)");
		for (var c:fullProperties) {
			sb.append(",");
			sb.append(c.getGobFieldName());
			sb.append(" ");
			switch (c.getType()) {
			case Boolean:
				sb.append("BOOLEAN");
				break;
			case Float:
				sb.append("DOUBLE");
				break;
			case Integer:
				sb.append("INTEGER");
				break;
			case ID:
			case GOB:
//			case Image:
//			case Audio:
//				sb.append("VARCHAR(255)");
//				break;
			case String:
				sb.append("VARCHAR(4096)");
				break;
			default:
				break;
			}
			if (c.isArray()) {
				sb.append(" ARRAY");
			}
		}
		sb.append(")");
		try (Statement s=connection.createStatement()){
			log.trace("SQL: "+sb.toString());
			s.execute(sb.toString());
		}
	}

	private Map<String, String> getTableColumns(String table) throws SQLException{
		Map<String,String> columns=new HashMap<>();
		try (PreparedStatement s=connection.prepareStatement("SELECT TABLE_NAME, COLUMN_NAME, TYPE_NAME, COLUMN_SIZE, DECIMAL_DIGITS, IS_NULLABLE FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME=?")){
			s.setString(1, table);
			var r=s.executeQuery();
			while (r.next()) {
				String name=r.getString("COLUMN_NAME");
				String type=r.getString("TYPE_NAME");
				if ("VARCHAR".equals(type)) {
					type=type+"("+r.getInt("COLUMN_SIZE")+")";
				}
				columns.put(name,type);
			}
		}
		return columns;
	}
	
	private void updateTable(GOB gob, List<GOBPropertyDefinition> fullProperties) throws SQLException {
		Map<String, String> columns=getTableColumns("GOB_"+gob.getGobTableName());
		
		Set<String> remove=new HashSet<>();
		Set<String> update=new HashSet<>();
		Set<String> insert=new HashSet<>();
		Set<String> convertStringToInteger=new HashSet<>();
		Set<String> convertStringToDouble=new HashSet<>();
		Set<String> convertStringToBoolean=new HashSet<>();
		Map<String,String> all=new HashMap<>();
		
		for (var p:fullProperties) {
			String col=p.getGobFieldName();
			String type=getTypeSQL(p.getType(),p.isArray());
			all.put(col,type);
			if (!columns.containsKey(col)) {
				insert.add(col);
			} else {
				// Has the type changed from array to single or vice versa
				if ((columns.get(col).indexOf("ARRAY")!=-1)!=p.isArray()) {
					// Remove the old one and create the new one.
					remove.add(col);
					insert.add(col);
				} else {
					log.trace("Was '"+columns.get(col)+"' is '"+type+"'");
					if (!type.equals(columns.get(col))){
						if (type.indexOf("VARCHAR")!=-1) {
							if (columns.get(col).indexOf("BOOLEAN")!=-1) {
								convertStringToBoolean.add(col);
							} else if (columns.get(col).indexOf("INTEGER")!=-1) {
								convertStringToInteger.add(col);
							} else if (columns.get(col).indexOf("DOUBLE")!=-1) {
								convertStringToInteger.add(col);
							}
						}
						update.add(col);
					}
				}
			}
		}
		for (var c:columns.keySet()) {
			if (!all.containsKey(c) && !"UUID".equals(c)) {
				remove.add(c);
			}
		}
		// Perform data conversions
		for (var f:convertStringToInteger) {
			try (var s=connection.prepareStatement("UPDATE GOB_"+gob.getGobTableName()+" SET "+f+"='0' WHERE NOT REGEXP_MATCHES("+f+",'[0-9]*')")){
				s.execute();
			}
		}
		for (var f:convertStringToDouble) {
			try (var s=connection.prepareStatement("UPDATE GOB_"+gob.getGobTableName()+" SET "+f+"='0' WHERE NOT REGEXP_MATCHES("+f+",'[0-9]*.[0-9]*')")){
				s.execute();
			}
		}
		for (var f:convertStringToBoolean) {
			try (var s=connection.prepareStatement("UPDATE GOB_"+gob.getGobTableName()+" SET "+f+"='TRUE' WHERE "+f+" IN ('Y','y','yes','Yes','YES','1')")){
				s.execute();
			}
			try (var s=connection.prepareStatement("UPDATE GOB_"+gob.getGobTableName()+" SET "+f+"='TRUE' WHERE "+f+" IN ('N','n','no','No','NO','0')")){
				s.execute();
			}
		}
		
		for (String f:remove) {
			StringBuffer source=new StringBuffer("SET TABLE ");
			source.append("GOB_"+gob.getGobTableName());
			StringBuffer alter=new StringBuffer();
			alter.append("ALTER TABLE ");
			alter.append("GOB_"+gob.getGobTableName());
			alter.append(" DROP COLUMN ");
			alter.append(f);
			try (var s=connection.prepareStatement(alter.toString())){
				log.trace("SQL: "+alter.toString());
				s.execute();
			}
		}
		// Alter table
		for (String f:update) {
			StringBuffer source=new StringBuffer("SET TABLE ");
			source.append("GOB_"+gob.getGobTableName());
			StringBuffer alter=new StringBuffer();
			alter.append("ALTER TABLE ");
			alter.append("GOB_"+gob.getGobTableName());
			alter.append(" ALTER COLUMN ");
			alter.append(f);
			alter.append(" ");
			alter.append(all.get(f));
			try (var s=connection.prepareStatement(alter.toString())){
				log.trace("SQL: "+alter.toString());
				s.execute();
			}
		}
		for (String f:insert) {
			StringBuffer source=new StringBuffer("SET TABLE ");
			source.append("GOB_"+gob.getGobTableName());
			StringBuffer alter=new StringBuffer();
			alter.append("ALTER TABLE ");
			alter.append("GOB_"+gob.getGobTableName());
			alter.append(" ADD COLUMN ");
			alter.append(f);
			alter.append(" ");
			alter.append(all.get(f));
			try (var s=connection.prepareStatement(alter.toString())){
				log.trace("SQL: "+alter.toString());
				s.execute();
			}
		}
	}

	@Override
	public void updateStructure(GOB gob,List<GOBPropertyDefinition> fullProperties) {
		try {
			if (!tableExists("GOB_"+gob.getGobTableName())){
				if (fullProperties.size()>0) {
					createNewTable(gob,fullProperties);
				}
			} else {
				if (fullProperties.size()>0) {
					updateTable(gob,fullProperties);
				} else {
					// TODO: Drop table;
				}
			}
		} catch (SQLException e) {
			ErrorUtilities.showFatalException(e);
		}
	}

	@Override
	public void save() {
		if (inTransaction) {
			executeSQL("commit;");
			inTransaction=false;
		}
	}
	
	private GOB findGobByUUID(String uuid) {
		EventList<GOB> allGobs = adventureProjectModel.getNamedResourceModel(GOB.class);
		for (var g:allGobs) {
			if (uuid.equals(g.getUuid())){
				return g;
			}
		}
		System.out.println("Unable to fing GOB '"+uuid+"'");
		return null;
	}
	
	private GOBInstance fetchGobInstance(String gobInstanceReference) {
		if (loadedGobInstances.containsKey(gobInstanceReference)) {
			return loadedGobInstances.get(gobInstanceReference);
		} else {
			String[] parts=gobInstanceReference.split(";");
			GOB gob=findGobByUUID(parts[0]);
			try (PreparedStatement ps=connection.prepareStatement("SELECT * FROM GOB_"+gob.getGobTableName()+" WHERE uuid=?")){
				ps.setString(1, parts[1]);
				var rs=ps.executeQuery();
				while (rs.next()) {
					return buildInstance(gob, adventureProjectModel.getAllGobProperties(gob), rs);
				}
			} catch (SQLException e) {
				ErrorUtilities.showFatalException(e);
			}
		}
		return null;
	}
	
	private GOBInstance buildInstance(GOB gob,List<GOBPropertyDefinition> fullProperties,ResultSet rs) throws SQLException {
		String uuid=gob.getUuid();
		String gobInstanceReference=gob.getUuid()+";"+rs.getString("UUID");
		if (loadedGobInstances.containsKey(gobInstanceReference)) {
			return loadedGobInstances.get(gobInstanceReference);
		} else {
			GOBInstance gobInstance=new GOBInstance();
			gobInstance.setGobType(uuid);
			loadedGobInstances.put(gobInstanceReference,gobInstance);
	
			Set<String> rsCols=new HashSet<>();
			for (int t=1;t<=rs.getMetaData().getColumnCount();t++) {
				rsCols.add(rs.getMetaData().getColumnName(t));
			}
			
			for (var pd:fullProperties) {
				if (rsCols.contains(pd.getGobFieldName())) {
					Object value=rs.getObject(pd.getGobFieldName());
					if (value!=null) {
						if (pd.getType()==Type.GOB) {
							if (!pd.isArray()) {
								if (value instanceof String valueString && valueString.indexOf(";")!=-1) {
									GOBInstance gi=fetchGobInstance(valueString);
									GOBProperty<GOBInstance> property=new GOBProperty<>(pd,gi);
									gobInstance.getProperties().put(pd.getGobFieldName(), property);
								}
							} else {
								System.out.println("Loading An Array "+value.getClass());
								List<GOBInstance> list=new ArrayList<>();
								if (value instanceof JDBCArray jdbcArray) {
									Object[] valueArray=(Object[])jdbcArray.getArray();
//									System.out.println("Loading An Array 10 "+jdbcArray.getArray().getClass());
									
									for (Object valueObject:valueArray) {
										String valueString=(String)valueObject;
										if (valueString!=null && valueString.indexOf(";")!=-1) {
											GOBInstance gi=fetchGobInstance(valueString);
											list.add(gi);
										}
									}
								}
								System.out.println("Loading An Array 20");
								GOBProperty<List<GOBInstance>> property=new GOBProperty<>(pd,list);
								gobInstance.getProperties().put(pd.getGobFieldName(), property);
								
							}
						} else {
							@SuppressWarnings({ "rawtypes", "unchecked" })
							GOBProperty<?> property=new GOBProperty(pd,value);
							if (pd.isArray()) {
								List<?> list=new ArrayList<>();
								if (value instanceof JDBCArray jdbcArray) {
									Object[] valueArray=(Object[])jdbcArray.getArray();
									list=new ArrayList<>(Arrays.asList(valueArray));
								}
								property.setValue(list);
							}
							gobInstance.getProperties().put(pd.getGobFieldName(), property);
						}
					}
				} else {
					log.warn("Colum ("+pd.getName()+")'"+pd.getGobFieldName()+"' not found in table ()"+gob.getName()+"'"+gob.getGobTableName()+"'.");
				}
			}
			gobInstance.setUuid(rs.getString("UUID"));
			gobInstance.setNewInstance(false);
			return gobInstance;
		}
	}
	
	// TODO: This needs to be cached so we don't return a new version if a reload is called
	public EventList<GOBInstance> selectAll(GOB gob,List<GOBPropertyDefinition> fullProperties){
		List<GOBInstance> instances=new ArrayList<>();
		if (tableExists("GOB_"+gob.getGobTableName())){
			try (PreparedStatement ps=connection.prepareStatement("SELECT * FROM GOB_"+gob.getGobTableName())){
				var rs=ps.executeQuery();
				while (rs.next()) {
					instances.add(buildInstance(gob, fullProperties, rs));
				}
			} catch (SQLException e) {
				ErrorUtilities.showFatalException(e);
			}
		}
		return GlazedLists.eventList(instances);
	}
	
	public List<GOBInstance> selectUnused(GOB gob,List<GOB> thisAndParentGobs,List<GOBPropertyDefinition> fullProperties,List<GOB> gobsThatContainIt,List<List<GOBPropertyDefinition>> fullPropertiesPerGob){
		if (gobsThatContainIt.size()==0) {
			return selectAll(gob, fullProperties);
		}
		
		StringBuffer sb=new StringBuffer("SELECT * FROM GOB_");
		sb.append(gob.getGobTableName());
		sb.append(" a WHERE NOT EXISTS (");
		boolean first=true;
		
		Set<String> allUuids=new HashSet<>();
		for (GOB uuidGob:thisAndParentGobs) {
			allUuids.add(uuidGob.getUuid());
		}
		
		for (int t=0;t<gobsThatContainIt.size();t++) {
			var gc=gobsThatContainIt.get(t);
			var props=fullPropertiesPerGob.get(t);
			for (var p:props) {
				if (p.getType()==Type.GOB && allUuids.contains(p.getGobType())) {
					for (GOB testGob:thisAndParentGobs) {
						if (!first) {
							sb.append(" UNION ");
						}else {
							first=false;
						}
						if (p.isArray()) {
							sb.append("SELECT * FROM GOB_");
							sb.append(gc.getGobTableName());
							sb.append(" WHERE CONCAT('");
							sb.append(testGob.getUuid());
							sb.append(";',a.UUID) in (SELECT * FROM UNNEST(");
							sb.append(p.getGobFieldName());
							sb.append("))");
						} else {
							sb.append("SELECT * FROM GOB_");
							sb.append(gc.getGobTableName());
							sb.append(" WHERE CONCAT('");
							sb.append(testGob.getUuid());
							sb.append(";',a.UUID) = ");
							sb.append(p.getGobFieldName());
							sb.append(" ");
						}
					}
				}
			}
		}
		sb.append(")");
		List<GOBInstance> instances=new ArrayList<>();
		if (tableExists("GOB_"+gob.getGobTableName())){
			try (PreparedStatement ps=connection.prepareStatement(sb.toString())){
				var rs=ps.executeQuery();
				while (rs.next()) {
					instances.add(buildInstance(gob, fullProperties, rs));
				}
			} catch (SQLException e) {
				ErrorUtilities.showFatalException(e);
			}
		}
		return GlazedLists.eventList(instances);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(GOB gob,List<GOBPropertyDefinition> fullProperties,GOBInstance gobInstance) {
		StringBuffer sb;
		if (!gobInstance.isNewInstance()) {
			sb=new StringBuffer("UPDATE ");
			sb.append("GOB_"+gob.getGobTableName());
			sb.append(" SET ");
			boolean first=true;
			for (var c:fullProperties) {
				if (first) {
					first=false;
				} else {
					sb.append(",");
				}
				sb.append(c.getGobFieldName());
				sb.append("=?");
			}
			sb.append(" WHERE UUID=?");
		} else {
			sb=new StringBuffer("INSERT INTO ");
			sb.append("GOB_"+gob.getGobTableName());
			sb.append("(");
			boolean first=true;
			for (var c:fullProperties) {
				if (first) {
					first=false;
				} else {
					sb.append(",");
				}
				sb.append(c.getGobFieldName());
			}
			if (!first) {
				sb.append(",");
			}
			sb.append("UUID) VALUES (");
			first=true;
			for (@SuppressWarnings("unused") var c:fullProperties) {
				if (first) {
					first=false;
				} else {
					sb.append(",");
				}
				sb.append("?");
			}
			if (!first) {
				sb.append(",");
			}
			sb.append("?");
			sb.append(")");
		}
		var properties=gobInstance.getProperties();
//		System.out.println(sb.toString());
		try (PreparedStatement ps=connection.prepareStatement(sb.toString())){
			int pos=1;
			for (var c:fullProperties) {
				var p=properties.get(c.getGobFieldName());
				var value=p==null?null:p.getValue();
				if (value!=null) {
					System.out.println("Saving "+value.getClass().getName());
				}
				
				if (value!=null && value instanceof GOBInstance gi) {
					value=gi.getGobType()+";"+gi.getUuid();
				} else if (value!=null && value instanceof List<?> list) {
					if (c.getType()==Type.GOB) {
						int listPos=0;
						Object[] stringList=new Object[list.size()];
						for (GOBInstance gi:(List<GOBInstance>)list) {
							stringList[listPos++]=gi==null?null:gi.getGobType()+";"+gi.getUuid();
						}
						value=stringList;
					}
				}
				if (value!=null) {
					System.out.println("Saving as "+value.getClass().getName());
				}
				ps.setObject(pos++, value);
			}
			ps.setString(pos, gobInstance.getUuid());
			ps.execute();
			gobInstance.setNewInstance(false);
		} catch (SQLException e) {
			ErrorUtilities.showFatalException(e);
		}
	}
	
	@Override
	public void delete(GOB gob,String uuid) {
		try (PreparedStatement ps=connection.prepareStatement("DELETE FROM GOB_"+gob.getGobTableName()+" WHERE UUID=?")){
			ps.setString(1, uuid);
			ps.execute();
		} catch (SQLException e) {
			ErrorUtilities.showFatalException(e);
		}		
	}

}
