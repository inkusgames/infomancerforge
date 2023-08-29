package ninja.egg82.analytics.events;

import java.util.Optional;
import ninja.egg82.analytics.common.ProgressionStatus;
import ninja.egg82.analytics.events.base.GAEventBase;
import org.json.simple.JSONObject;

public class GAProgression implements GAEvent {
    private final GAEventBase eventBase;
    private final ProgressionStatus progressionStatus;
    private final String progression1;

    private long attemptNum = -1L;
    private String progression2 = null;
    private String progression3 = null;
    private Optional<Long> score = Optional.empty();

    private GAProgression(GAEventBase eventBase, ProgressionStatus progressionStatus, String progression1) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (progressionStatus == null) {
            throw new IllegalArgumentException("progressionStatus cannot be null.");
        }
        if (progressionStatus == ProgressionStatus.UNKNOWN) {
            throw new IllegalArgumentException("progressionStatus cannot be UNKNOWN.");
        }
        if (progression1 == null) {
            throw new IllegalArgumentException("progression1 cannot be null.");
        }

        this.eventBase = eventBase;
        this.progressionStatus = progressionStatus;
        this.progression1 = progression1;
    }

    public static GAProgression.Builder builder(GAEventBase eventBase, ProgressionStatus progressionStatus, String progression1) { return new GAProgression.Builder(eventBase, progressionStatus, progression1); }

    public static class Builder {
        private final GAProgression event;

        private Builder(GAEventBase eventBase, ProgressionStatus progressionStatus, String progression1) { event = new GAProgression(eventBase, progressionStatus, progression1); }

        public Builder attemptNum(long val) {
            event.attemptNum = val;
            return this;
        }

        public Builder progression2(String val) {
            event.progression2 = val;
            return this;
        }

        public Builder progression3(String val) {
            event.progression3 = val;
            return this;
        }

        public Builder score(long val) {
            event.score = Optional.of(val);
            return this;
        }

        public GAProgression build() { return event; }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getObject() {
        JSONObject retVal = eventBase.getObject();

        retVal.put("category", "progression");
        retVal.put("event_id", progressionStatus.getName() + ":" + progression1 + (progression2 != null ? ":" + progression2 : "") + (progression3 != null ? ":" + progression3 : ""));
        if (attemptNum > 0 && (progressionStatus == ProgressionStatus.FAIL || progressionStatus == ProgressionStatus.COMPLETE)) {
            retVal.put("attempt_num", attemptNum);
        }
        if (score.isPresent() && (progressionStatus == ProgressionStatus.FAIL || progressionStatus == ProgressionStatus.COMPLETE)) {
            retVal.put("score", score.get());
        }

        return retVal;
    }
}
