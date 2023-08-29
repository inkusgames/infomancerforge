package ninja.egg82.analytics.common;

import org.json.simple.JSONObject;

public class GAInputBase {
    @SuppressWarnings("unchecked")
	public JSONObject getObject() {
        JSONObject retVal = new JSONObject();

        retVal.put("platform", parseSystemName(System.getProperty("os.name")));
        retVal.put("os_version", System.getProperty("os.name").toLowerCase());
        retVal.put("sdk_version", "rest api v2");

        return retVal;
    }

    private String parseSystemName(String field) {
        String lowerField = field.toLowerCase();

        if (lowerField.contains("win")) {
            return "windows";
        } else if (lowerField.contains("mac")) {
            return "macintosh";
        } else if (lowerField.contains("nix") || lowerField.contains("nux") || lowerField.contains("aix")) {
            return "unix";
        } else if (lowerField.contains("sunos")) {
            return "solaris";
        } else {
            return "unknown: '" + field + "'";
        }
    }
}
