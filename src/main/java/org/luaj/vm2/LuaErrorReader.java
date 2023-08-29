package org.luaj.vm2;

/**
 * Helper class to read LueErroir fileline
 * 
 * @author travi
 *
 */
public class LuaErrorReader extends LuaError {
	private static final long serialVersionUID = 1L;

	public LuaErrorReader(String filename, Varargs args) {
		super(args.arg(2).toString());
		String error = args.arg(2).toString();
		int marker = error == null ? -1 : error.indexOf(":");
		if (marker != -1) {
			fileline = error.substring(marker + 1, error.indexOf(" ", marker));
		}

	}

	static final public String getFileline(LuaError luaError) {
		return luaError.fileline;
	}

	static final public String getTraceback(LuaError luaError) {
		return luaError.traceback;
	}
}
