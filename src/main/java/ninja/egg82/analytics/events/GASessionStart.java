package ninja.egg82.analytics.events;

import ninja.egg82.analytics.events.base.GAEventBase;
import org.json.simple.JSONObject;

public class GASessionStart implements GAEvent {
    private final GAEventBase eventBase;

    private GASessionStart(GAEventBase eventBase) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }

        this.eventBase = eventBase;
    }

    public static GASessionStart.Builder builder(GAEventBase eventBase) { return new GASessionStart.Builder(eventBase); }

    public static class Builder {
        private final GASessionStart end;

        private Builder(GAEventBase eventBase) { end = new GASessionStart(eventBase); }

        public GASessionStart build() { return end; }
    }

    @SuppressWarnings("unchecked")
	public JSONObject getObject() {
        JSONObject retVal = eventBase.getObject();

        retVal.put("category", "user");

        return retVal;
	}
}
