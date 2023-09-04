package com.inkus.infomancerforge;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.inkus.infomancerforge.beans.Config;
import com.inkus.infomancerforge.beans.Project;
import com.inkus.infomancerforge.beans.ProjectConfig;
import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.sourcecode.SourceCode;
import com.inkus.infomancerforge.beans.views.View;
import com.inkus.infomancerforge.storage.JsonColor;

public class StorageUtilities {
	static private final Logger log=LogManager.getLogger(StorageUtilities.class);

	static public final String projectJsonFileName="project.json";
	static public final String ProjectConfigJsonFileName="projectConfig.json";
	static public final String configJsonFileName="config.json";

	public static final Gson gson=new GsonBuilder().registerTypeAdapter(Color.class, new JsonColor()).create();
	private static File storageLocation=null;
	private static Config config=null;

	public static Config getConfig() {
		if (config==null) {
			File configFile=new File(getStorageLocation(configJsonFileName));
			log.trace("Loading config file from "+configFile.getAbsolutePath());
			if (configFile.exists() && configFile.length()>0) {
				try {
					config=gson.fromJson(new FileReader(configFile), Config.class);
				} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
					ErrorUtilities.showFatalException(e);
				}
			} else {
				log.trace("Config not found creating default.");
				config=new Config();
			}
		} 
		return config;
	}

	public static void saveConfig() {
		Config config=getConfig();
		File configFile=new File(getStorageLocation(configJsonFileName));
		try (Writer writer = new FileWriter(configFile)){
			gson.toJson(config, writer);
		} catch (JsonIOException | IOException e) {
			log.error("Unable to save config", e);
		}
	}

	public static Project loadProjectIfFound(String path) {
		Project project=null;
		File projectFile=new File(path);
		if (projectFile.exists()) {
			try {
				project=gson.fromJson(new FileReader(projectFile), Project.class);
				project.setPath(projectFile.getParent());
			} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
				ErrorUtilities.showFatalException(e);
			}
		}
		return project;
	}

	public static GOB loadGOB(String path) {
		GOB gob=null;
		File gobFile=new File(path);
		if (gobFile.exists()) {
			try (Reader reader = new FileReader(gobFile)){
				gob=gson.fromJson(reader, GOB.class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				// TODO: Does this really need to be a fatal exception?
				log.error("Unable to load GOB from file '"+path+"'");
				ErrorUtilities.showFatalException(e);
			}
		}
		return gob;
	}

	public static View loadView(String path) {
		View view=null;
		File viewFile=new File(path);
		if (viewFile.exists()) {
			try (Reader reader = new FileReader(viewFile)){
				view=gson.fromJson(reader, View.class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				// TODO: Does this really need to be a fatal exception?
				ErrorUtilities.showFatalException(e);
			}
		}
		return view;
	}

	public static SourceCode loadSourceCode(String path,String basePath) {
		File sourceCodeFile=new File(path);
		SourceCode sourceCode=null;
		if (sourceCodeFile.exists()) {
			try (Reader reader = new FileReader(sourceCodeFile)){
				String name=FilenameUtils.getBaseName(path);
				String ext=FilenameUtils.getExtension(path);
				String projectPath=sourceCodeFile.getAbsolutePath().substring(basePath.length());
				System.out.println("PP:"+projectPath);
				String code=FileUtils.readFileToString(sourceCodeFile, "utf8");
				sourceCode=new SourceCode(sourceCodeFile,projectPath,name,ext,code);
			} catch (IOException e) {
				// TODO: Does this really need to be a fatal exception?
				ErrorUtilities.showFatalException(e);
			}
		}
		return sourceCode;
	}
	
	public static SourceCode reloadSourceCode(SourceCode sourceCode) {
		File sourceCodeFile=sourceCode.getSourceFile();
		if (sourceCodeFile.exists()) {
			try (Reader reader = new FileReader(sourceCodeFile)){
				String code=FileUtils.readFileToString(sourceCodeFile, "utf8");
				sourceCode.setCode(code);
				sourceCode.saved();
			} catch (IOException e) {
				// TODO: Does this really need to be a fatal exception?
				ErrorUtilities.showFatalException(e);
			}
		}
		return sourceCode;
	}

	public static String saveGOB(GOB gob,String path) {
		log.trace("Saving GOB "+gob.getName()+ " to "+path);
		File gobFile=new File(path);

		// TODO Make sure Gob names is suitable file characters
		// TODO New file name can not already be defined (This should be because GOB edit will fail if the name already exists
		if (!gobFile.getName().equals(gob.getName()+".gob")){
			String newPath=gobFile.getParent()+File.separator+gob.getName()+".gob";
			if (gobFile.renameTo(new File(newPath))) {
				gobFile=new File(newPath);
				path=newPath;
			} else {
				log.warn("Unable to rename GOB from "+path+" to "+newPath+". The GOB has been saved with it's original file name.");
			}
		}

		try (Writer writer = new FileWriter(gobFile)){
			gson.toJson(gob, writer);
		} catch (JsonIOException | IOException e) {
			log.error("Unable to save gob file", e);
		}

		return  path;
	}

	public static String saveView(View view,String path) {
		log.trace("Saving View "+view.getName()+ " to "+path);
		File viewFile=new File(path);

		// TODO Make sure View names is suitable file characters
		// TODO New file name can not already be defined (This should be because GOB edit will fail if the name already exists
		if (!viewFile.getName().equals(view.getName()+".view")){
			String newPath=viewFile.getParent()+File.separator+view.getName()+".view";
			if (viewFile.renameTo(new File(newPath))) {
				viewFile=new File(newPath);
				path=newPath;
			} else {
				log.warn("Unable to rename View from "+path+" to "+newPath+". The View has been saved with it's original file name.");
			}
		}

		try (Writer writer = new FileWriter(viewFile)){
			gson.toJson(view, writer);
		} catch (JsonIOException | IOException e) {
			log.error("Unable to save view file", e);
		}

		return  path;
	}

	public static String saveSourceCode(SourceCode sourceCode,String path) {
		File filePath=new File(path);
		if (!filePath.getParentFile().exists()) {
			filePath.getParentFile().mkdirs();
		}
		try (Writer writer = new FileWriter(path,Charset.forName("utf8"))){
			writer.append(sourceCode.getCode());
		} catch (JsonIOException | IOException e) {
			log.error("Unable to save SourceCode file", e);
		}
		return path;
	}

	public static String getStorageLocation(String fileName) {
		if (storageLocation==null) {
			// TODO: Create a MAC and Linux version of this
			storageLocation=new File(System.getProperty("user.home")+"\\AppData\\Local\\Informancer Forge\\");
		}
		if (!storageLocation.exists()) {
			storageLocation.mkdirs();
		}

		return storageLocation.getAbsolutePath()+File.separator+fileName;
	}

	public static void setProjectFileLastUsed(Project project) {
		File projectFile=new File(project.getPath()+File.separator+projectJsonFileName);
		getConfig().setLastWorkingPath(project.getPath());
		getConfig().getKnownProjects().remove(projectFile.getAbsolutePath());
		getConfig().getKnownProjects().add(0,projectFile.getAbsolutePath());
		saveConfig();
	}

	public static void saveProjectFile(Project project) {
		File projectFile=new File(project.getPath()+File.separator+projectJsonFileName);
		try (Writer writer = new FileWriter(projectFile)){
			gson.toJson(project, writer);
			setProjectFileLastUsed(project);
		} catch (JsonIOException | IOException e) {
			log.error("Unable to save project", e);
		}
	}


	public static ProjectConfig getProjectConfig(Project project) {
		ProjectConfig projectConfig=null;;
		File projectConfigFile=new File(project.getPath()+File.separator+".data"+File.separator+ProjectConfigJsonFileName);
		if (projectConfigFile.exists() && projectConfigFile.length()>0) {
			try {
				projectConfig=gson.fromJson(new FileReader(projectConfigFile), ProjectConfig.class);
			} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
				ErrorUtilities.showFatalException(e);
			}
		} else {
			projectConfig=new ProjectConfig();
		}
		return projectConfig;
	}

	public static void saveProjectConfig(Project project,ProjectConfig projectConfig) {
		File projectConfigFile=new File(project.getPath()+File.separator+".data"+File.separator+ProjectConfigJsonFileName);
		try (Writer writer = new FileWriter(projectConfigFile)){
			gson.toJson(projectConfig, writer);
		} catch (JsonIOException | IOException e) {
			log.error("Unable to save project config", e);
		}
	}

	private static String NAME_CHARS="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static String getRandomName(int length,Set<String> excludeThese) {
		StringBuffer name=new StringBuffer();
		do {
			name.setLength(0);
			for (int t=0;t<length;t++) {
				int pos=(int)(Math.random()*NAME_CHARS.length());
				name.append(NAME_CHARS.subSequence(pos, pos+1));
			}
		} while (excludeThese.contains(name.toString()));
		return name.toString();
	}

	public static String getJson(Serializable obj) {
		return gson.toJson(obj);
	}
	
	public static void unzip(String zipFilePath, String destDir) {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if(!dir.exists()) dir.mkdirs();
		FileInputStream fis;
		//buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while(ze != null){
				if (ze.getSize()>0) {
					String fileName = ze.getName();
					File newFile = new File(destDir + File.separator + fileName);
					System.out.println("Unzipping to "+newFile.getAbsolutePath());
					//create directories for sub directories in zip
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				//close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			//close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void unzip(byte[] zipFileData, String destDir) {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if(!dir.exists()) dir.mkdirs();
		ByteArrayInputStream bais;
		//buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			bais = new ByteArrayInputStream(zipFileData);
			ZipInputStream zis = new ZipInputStream(bais);
			ZipEntry ze = zis.getNextEntry();
			while(ze != null){
				if (ze.getSize()>0) {
					String fileName = ze.getName();
					File newFile = new File(destDir + File.separator + fileName);
					System.out.println("Unzipping to "+newFile.getAbsolutePath());
					//create directories for sub directories in zip
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				//close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			//close last ZipEntry
			zis.closeEntry();
			zis.close();
			bais.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> loadJSONMap(File fromFile){
		if (fromFile.exists()) {
			try (Reader reader = new FileReader(fromFile)){
				return gson.fromJson(reader, Map.class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				// TODO: Does this really need to be a fatal exception?
				log.error("Unable to load JSON Map from file '"+fromFile.getAbsolutePath()+"'");
				ErrorUtilities.showFatalException(e);
			}
		}
		return new HashMap<>();
	}

	public static void saveJSONMap(File file,Map<String,Object> map) {
		file.getParentFile().mkdirs();
		try (Writer writer = new FileWriter(file)){
			gson.toJson(map, writer);
		} catch (JsonIOException | IOException e) {
			log.error("Unable to save JSONMap '"+file.getAbsolutePath()+"'", e);
		}
	}

	public static void deleteAll(File path) throws IOException {
		if (path.exists()) {
			if (path.isDirectory()) {
				FileUtils.deleteDirectory(path);
			} else {
				path.delete();
			}
		}
	}
}
