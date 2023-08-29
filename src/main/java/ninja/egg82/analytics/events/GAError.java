package ninja.egg82.analytics.events;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import ninja.egg82.analytics.common.Severity;
import ninja.egg82.analytics.events.base.GAEventBase;
import org.json.simple.JSONObject;

public class GAError implements GAEvent {
    private final GAEventBase eventBase;
    private final Severity severity;
    private final String message;

    private GAError(GAEventBase eventBase, Throwable exception) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (exception == null) {
            throw new IllegalArgumentException("exception cannot be null.");
        }

        String exString = null;
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            exception.printStackTrace(pw);
            exString = sw.toString();
        } catch (IOException ignored) {}

        this.eventBase = eventBase;
        this.severity = (exception instanceof Error) ? Severity.CRITICAL : Severity.ERROR;
        this.message = (exString != null) ? exString : exception.getMessage();
    }

    private GAError(GAEventBase eventBase, LogRecord record) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (record == null) {
            throw new IllegalArgumentException("record cannot be null.");
        }

        if (record.getThrown() != null) {
            String exString = null;
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                record.getThrown().printStackTrace(pw);
                exString = sw.toString();
            } catch (IOException ignored) {}

            this.severity = (record.getThrown() instanceof Error) ? Severity.CRITICAL : Severity.ERROR;
            this.message = (exString != null) ? exString : record.getThrown().getMessage();
        } else if (record.getMessage() != null) {
            this.message = record.getMessage();

            if (record.getLevel() == Level.WARNING) {
                severity = Severity.WARNING;
            } else if (record.getLevel() == Level.INFO) {
                severity = Severity.INFO;
            } else if (record.getLevel() == Level.FINE || record.getLevel() == Level.FINER || record.getLevel() == Level.FINEST) {
                severity = Severity.DEBUG;
            } else {
                severity = Severity.ERROR;
            }
        } else {
            throw new IllegalArgumentException("record does not have a thrown or a message.");
        }

        this.eventBase = eventBase;
    }

    private GAError(GAEventBase eventBase, Severity severity, String message) {
        if (eventBase == null) {
            throw new IllegalArgumentException("eventBase cannot be null.");
        }
        if (severity == null) {
            throw new IllegalArgumentException("severity cannot be null.");
        }
        if (severity == Severity.UNKNOWN) {
            throw new IllegalArgumentException("severity cannot be UNKNOWN.");
        }
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null.");
        }

        this.eventBase = eventBase;
        this.severity = severity;
        this.message = message;
    }

    public static GAError.Builder builder(GAEventBase eventBase, Throwable exception) { return new GAError.Builder(eventBase, exception); }

    public static GAError.Builder builder(GAEventBase eventBase, LogRecord record) { return new GAError.Builder(eventBase, record); }

    public static GAError.Builder builder(GAEventBase eventBase, Severity severity, String message) { return new GAError.Builder(eventBase, severity, message); }

    public static class Builder {
        private final GAError event;

        private Builder(GAEventBase eventBase, Throwable error) { event = new GAError(eventBase, error); }

        private Builder(GAEventBase eventBase, LogRecord record) { event = new GAError(eventBase, record); }

        private Builder(GAEventBase eventBase, Severity severity, String message) { event = new GAError(eventBase, severity, message); }

        public GAError build() { return event; }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getObject() {
        JSONObject retVal = eventBase.getObject();

        retVal.put("category", "error");
        retVal.put("severity", severity.getType());
        retVal.put("message", message);

        return retVal;
    }
}
