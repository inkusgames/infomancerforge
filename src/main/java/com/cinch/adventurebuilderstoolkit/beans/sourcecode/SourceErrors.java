package com.cinch.adventurebuilderstoolkit.beans.sourcecode;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaErrorReader;

public class SourceErrors {

	protected String description;
	protected int lineNumber;
	
	public SourceErrors(int lineNumber,String description) {
		this.lineNumber=lineNumber;
		this.description=description;
	}
	
	public SourceErrors(LuaError luaError) {
		String error=luaError.getMessage();
		int lf=error.indexOf("\n");
		int c=error.indexOf(":");
		String fileline=LuaErrorReader.getFileline(luaError);
		String trace=LuaErrorReader.getTraceback(luaError);
		System.out.println("fileline="+fileline);
		System.out.println("trace="+trace);
		
		if (fileline!=null && c!=-1 && (lf==-1 || lf>c)) {
			if (lf>-1) {
				error=error.substring(0,lf);
			}
			//int marker = error==null?-1:error.indexOf(":");
			
			
			if (fileline.indexOf(":")!=-1) {
				lineNumber=Integer.parseInt(fileline.substring(fileline.indexOf(":")+1));
			}
			
//			lineNumber=Integer.parseInt(error.substring(c + 1, error.indexOf(" ", c))) - 1;
//			description=error.substring(error.indexOf(" ", c)).trim();
			description=fileline+(trace==null?"":"|"+trace);
//			if (marker != -1) {
//				throw new LUAScriptError(
//						
//						name, error,0,0);
//			} else {
//				throw new LUAScriptError(0, name, error,0,0);
//			}
		} else {
			if (c!=-1) {
				int c2=error.indexOf(":",c+1);
				if (c2!=-1) {
					lineNumber=Integer.parseInt(error.substring(c+1,c2));
				}
			}
			description=error;
		}
	}

	public String getDescription() {
		return description;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public int getColumn() {
		return 0;
	}

	public int getLength() {
		return 10;
	}
}
