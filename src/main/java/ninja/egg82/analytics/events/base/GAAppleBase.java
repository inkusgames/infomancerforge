package ninja.egg82.analytics.events.base;

import java.util.UUID;
import ninja.egg82.analytics.GameAnalytics;
import org.json.simple.JSONObject;

public class GAAppleBase extends GAEventBase {
    private boolean loggedIntoGamecenter = false;
    private boolean jailbroken = false;
    private String idfv = null;
    private String idfa = null;

    private GAAppleBase(GameAnalytics ga, UUID userID, long sessionNum) { super(ga, userID.toString(), sessionNum); }

    public static GAAppleBase.Builder appleBuilder(GameAnalytics ga, UUID userID, long sessionNum) { return new GAAppleBase.Builder(ga, userID, sessionNum); }

    public static class Builder {
        private final GAAppleBase event;

        public Builder(GameAnalytics ga, UUID userID, long sessionNum) { event = new GAAppleBase(ga, userID, sessionNum); }

        public GAAppleBase build() { return event; }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getObject() {
        JSONObject retVal = super.getObject();

        retVal.put("logon_gamecenter", loggedIntoGamecenter);
        retVal.put("jailbroken", jailbroken);
        if (idfv != null) {
            retVal.put("ios_idfv", idfv);
        }
        if (idfa != null) {
            retVal.put("ios_idfa", idfa);
        }

        return retVal;
    }
}
