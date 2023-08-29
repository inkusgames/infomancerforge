package com.cinch.adventurebuilderstoolkit.lua.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LuaFile {
	static private final Logger log=LogManager.getLogger(LuaFile.class);
	
	static private final Charset charset=Charset.forName("utf8");
	
	private RandomAccessFile fileHandle;
	
	private LuaFile(File file,boolean read, boolean write, boolean append) {
	}

//	
//	public String read() {
//		return read("*line");
//	}
//	
	/**
	 * "*all"	reads the whole file
     * "*line"	reads the next line
     * "*number"	reads a number
     * num	reads a string with up to num characters
	 * 
	 * @param what
	 * @return
	 */
	public String readAll() {
		// Read next line of text from the file
		StringBuffer sb=new StringBuffer();
		for (String s=readString(1024);s!=null;s=readString(1024)) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	public String readLine() {
		// Read next line of text from the file
	    String line=null;
		try {
			line = fileHandle.readLine();
		    return new String(line.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			log.warn(e.getMessage(),e);
		}
		return line;
	}
	
	public Double readNumber() {
		// Read next line of text from the file
		return 0d;
	}
	
	public String readString(long size) {
		// Read next line of text from the file
		return "";
	}
	
	public void write(String text) {
		try {
			fileHandle.write(text.getBytes(charset));
		} catch (IOException e) {
			log.warn(e.getMessage(),e);
		}
	}
	
	public boolean isClosed() {
		return fileHandle==null;
	}

	public void close() {
		try {
			fileHandle.close();
			fileHandle=null;
		} catch (IOException e) {
			log.warn(e.getMessage(),e);
		}
	}
	
	public void seek(String from,long offset) {
		try {
			if ("set".equals(from)) {
				fileHandle.seek(offset);
			} else if ("end".equals(from)) {
				fileHandle.seek(fileHandle.getFilePointer()+offset);
			} else {
				fileHandle.seek(fileHandle.getFilePointer()+offset);
			}
		} catch (IOException e) {
			log.warn(e.getMessage(),e);
		}
	}
//	
//	public static LuaFile openFile(String path ,String access, AdventureProjectModel adventureProjectModel) {
//		LuaFile luaFile=null;
//		
//		String fromPath=adventureProjectModel.getProject().getPath();
//		if (!fromPath.endsWith("/") || !fromPath.endsWith("\\")) {
//			fromPath=fromPath+"/";
//		}
//		if (path.startsWith("/") || path.startsWith("\\")) {
//			path=path.substring(1);
//		}
//		path=fromPath+path;
//		
//		File file=new File(path);
//		
//		// Test Path
//		if (file.getAbsolutePath().toLowerCase().startsWith(adventureProjectModel.getProject().getPath().toLowerCase())) {
//			// Test for access
//			
//			
//			
//		}
//		return luaFile;
//	}
	
	
}
