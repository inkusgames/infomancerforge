package ninja.egg82.analytics.events.base;

import java.util.UUID;
import ninja.egg82.analytics.GameAnalytics;
import org.json.simple.JSONObject;

public class GAGoogleBase extends GAEventBase {
    private boolean loggedIntoGooglePlay = false;
    private String androidID = null;
    private String googlePlusID = null;
    private String googleAID = null;

    private GAGoogleBase(GameAnalytics ga, UUID userID, long sessionNum) { super(ga, userID.toString(), sessionNum); }

    public static GAGoogleBase.Builder googleBuilder(GameAnalytics ga, UUID userID, long sessionNum) { return new GAGoogleBase.Builder(ga, userID, sessionNum); }

    public static class Builder {
        private final GAGoogleBase event;

        public Builder(GameAnalytics ga, UUID userID, long sessionNum) { event = new GAGoogleBase(ga, userID, sessionNum); }

        public GAGoogleBase build() { return event; }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getObject() {
        JSONObject retVal = super.getObject();

        retVal.put("logon_googleplay", loggedIntoGooglePlay);
        if (googleAID != null && androidID != null) {
            retVal.put("android_id", androidID);
        }
        if (googlePlusID != null) {
            retVal.put("googleplus_id", googlePlusID);
        }
        if (googleAID != null) {
            retVal.put("google_aid", googleAID);
        }

        return retVal;
    }
}
