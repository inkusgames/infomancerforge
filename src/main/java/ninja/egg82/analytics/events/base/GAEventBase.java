package ninja.egg82.analytics.events.base;

import java.util.UUID;
import ninja.egg82.analytics.common.ConnectionType;
import ninja.egg82.analytics.common.GAInputBase;
import ninja.egg82.analytics.GameAnalytics;
import ninja.egg82.analytics.common.Gender;
import org.json.simple.JSONObject;

public class GAEventBase extends GAInputBase {
    private GameAnalytics ga;
    private String userID;
    private long sessionNum;

    private boolean limitAdTracking = false;
    private String facebookID = null;
    private Gender gender = Gender.UNKNOWN;
    private int birthYear = -1;
    private String custom1 = null;
    private String custom2 = null;
    private String custom3 = null;
    private String buildVersion = null;
    private String engineVersion = null;
    private ConnectionType connectionType = ConnectionType.UNKNOWN;

    protected GAEventBase(GameAnalytics ga, String userID, long sessionNum) {
        if (ga == null) {
            throw new IllegalArgumentException("ga cannot be null.");
        }
        if (userID == null) {
            throw new IllegalArgumentException("userID cannot be null.");
        }
        if (sessionNum <= 0) {
            throw new IllegalArgumentException("sessionNum cannot be <= 0.");
        }

        this.ga = ga;
        this.userID = userID;
        this.sessionNum = sessionNum;
    }

    public static GAEventBase.Builder builder(GameAnalytics ga, UUID userID, long sessionNum) { return new GAEventBase.Builder(ga, userID.toString(), sessionNum); }

    public static class Builder {
        private final GAEventBase event;

        public Builder(GameAnalytics ga, String userID, long sessionNum) { event = new GAEventBase(ga, userID, sessionNum); }

        public Builder limitAdTracking(boolean val) {
            event.limitAdTracking = val;
            return this;
        }

        public Builder facebookID(String val) {
            event.facebookID = val;
            return this;
        }

        public Builder gender(Gender val) {
            if (val == null) {
                throw new IllegalArgumentException("val cannot be null.");
            }

            event.gender = val;
            return this;
        }

        public Builder birthYear(int val) {
            event.birthYear = val;
            return this;
        }

        public Builder custom1(String val) {
            event.custom1 = val;
            return this;
        }

        public Builder custom2(String val) {
            event.custom2 = val;
            return this;
        }

        public Builder custom3(String val) {
            event.custom3 = val;
            return this;
        }

        public Builder buildVersion(String val) {
            event.buildVersion = val;
            return this;
        }

        public Builder engineVersion(String val) {
            event.engineVersion = val;
            return this;
        }

        public Builder connectionType(ConnectionType val) {
            if (val == null) {
                throw new IllegalArgumentException("val cannot be null.");
            }

            event.connectionType = val;
            return this;
        }

        public GAEventBase build() { return event; }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getObject() {
        JSONObject retVal = super.getObject();

        retVal.put("device", System.getProperty("os.name").replaceAll("\\s", ""));
        retVal.put("v", 2);
        retVal.put("user_id", userID);
        retVal.put("client_ts", Math.floorDiv(System.currentTimeMillis(), 1000L) - ga.getInitResult().getTSOffset());
        retVal.put("manufacturer", parseSystemManufacturer(System.getProperty("os.name")));
        retVal.put("session_id", ga.getSessionId());
        retVal.put("session_num", sessionNum);

        retVal.put("limit_ad_tracking", limitAdTracking);
        if (facebookID != null) {
            retVal.put("facebook_id", facebookID);
        }
        if (gender != Gender.UNKNOWN) {
            retVal.put("gender", gender.getGenderString());
        }
        if (birthYear > -1) {
            retVal.put("birth_year", birthYear);
        }
        if (custom1 != null) {
            retVal.put("custom_01", custom1);
        }
        if (custom2 != null) {
            retVal.put("custom_02", custom2);
        }
        if (custom3 != null) {
            retVal.put("custom_03", custom3);
        }
        if (buildVersion != null) {
            retVal.put("build", buildVersion);
        }
        if (engineVersion != null) {
            retVal.put("engine_version", engineVersion);
        }
        if (connectionType != ConnectionType.UNKNOWN) {
            retVal.put("connection_type", connectionType.getConnectionString());
        }

        return retVal;
    }

    private String parseSystemManufacturer(String field) {
        String lowerField = field.toLowerCase();

        if (lowerField.contains("win")) {
            return "microsoft";
        } else if (lowerField.contains("mac")) {
            return "apple";
        } else if (lowerField.contains("nix") || lowerField.contains("nux") || lowerField.contains("aix")) {
            return "bell";
        } else if (lowerField.contains("sunos")) {
            return "sun";
        } else {
            return "unknown";
        }
    }
}
