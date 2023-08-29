package ninja.egg82.analytics.events;

import ninja.egg82.analytics.events.base.GAEventBase;
import org.json.simple.JSONObject;

public class GASessionEnd implements GAEvent {
    private final GAEventBase eventBase;
    private final long sessionLength;

    private GASessionEnd(GAEventBase eventBase, long sessionLength) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (sessionLength <= 0L) {
            throw new IllegalArgumentException("sessionLength cannot be <= 0");
        }

        this.eventBase = eventBase;
        this.sessionLength = sessionLength;
    }

    public static GASessionEnd.Builder builder(GAEventBase eventBase, long sessionLength) { return new GASessionEnd.Builder(eventBase, sessionLength); }

    public static class Builder {
        private final GASessionEnd end;

        private Builder(GAEventBase eventBase, long sessionLength) { end = new GASessionEnd(eventBase, sessionLength); }

        public GASessionEnd build() { return end; }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getObject() {
        JSONObject retVal = eventBase.getObject();

        retVal.put("category", "session_end");
        retVal.put("length", sessionLength);

        return retVal;
    }
}
