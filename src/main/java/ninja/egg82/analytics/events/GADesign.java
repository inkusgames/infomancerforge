package ninja.egg82.analytics.events;

import java.util.Optional;
import ninja.egg82.analytics.events.base.GAEventBase;
import org.json.simple.JSONObject;

public class GADesign implements GAEvent {
    private final GAEventBase eventBase;
    private final String part1;

    private String part2 = null;
    private String part3 = null;
    private String part4 = null;
    private String part5 = null;
    private Optional<Float> value = Optional.empty();

    private GADesign(GAEventBase eventBase, String part1) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (part1 == null) {
            throw new IllegalArgumentException("part1 cannot be null.");
        }

        this.eventBase = eventBase;
        this.part1 = part1;
    }

    public static GADesign.Builder builder(GAEventBase eventBase, String part1) { return new GADesign.Builder(eventBase, part1); }

    public static class Builder {
        private final GADesign event;

        private Builder(GAEventBase eventBase, String part1) { event = new GADesign(eventBase, part1); }

        public Builder part2(String val) {
            event.part2 = val;
            return this;
        }

        public Builder part3(String val) {
            event.part3 = val;
            return this;
        }

        public Builder part4(String val) {
            event.part4 = val;
            return this;
        }

        public Builder part5(String val) {
            event.part5 = val;
            return this;
        }

        public Builder value(float val) {
            event.value = Optional.of(val);
            return this;
        }

        public GADesign build() { return event; }
    }

    @SuppressWarnings("unchecked")
	public JSONObject getObject() {
        JSONObject retVal = eventBase.getObject();

        retVal.put("category", "design");
        retVal.put("event_id", part1 + (part2 != null ? ":" + part2 : "") + (part3 != null ? ":" + part3 : "") + (part4 != null ? ":" + part4 : "") + (part5 != null ? ":" + part5 : ""));
        if (value.isPresent()) {
            retVal.put("value", value.get());
        }

        return retVal;
    }
}
