package com.inkus.infomancerforge.lua;

public class LUAScriptError extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public int line;
	public int column;
	public int lenght;
	public String chunk;
	public String description;
	
	public LUAScriptError(int line, String chunk, String description,int column, int length) {
		super(description);
		this.line = line;
		this.chunk = chunk;
		this.description = description;
		this.column=column;
		this.lenght=length;
	}
//	
//	private LUAScriptError(LuaError luaError,String name) {
//		super(luaError);
//		
//		String error=luaError.getMessage();
//		
//		if (error.startsWith("[string \"")) {
//			error=error.substring(error.indexOf("\"]:")+3);
//		} else {
//			error=error.substring(error.indexOf(":")+1);
//		}
//		
//		int numberEnds=error.indexOf(":");
//		if (numberEnds==-1) {
//			numberEnds=error.indexOf(" ");
//		}
//		this.line = Integer.parseInt(error.substring(0, numberEnds))-1;
//		this.chunk = name;
//		this.description = error.substring(numberEnds+1).trim();
//	}
}
