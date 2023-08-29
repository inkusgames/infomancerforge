package ninja.egg82.analytics.common;

public enum FlowType {
    SINK("Sink"),
    SOURCE("Source"),
    UNKNOWN("Unknown");

    private final String str;
    FlowType(String str) {
        this.str = str;
    }

    public String getName() {
        return str;
    }

    public static FlowType fromString(String str) {
        for (FlowType f : FlowType.values()) {
            if (f.str.equals(str)) {
                return f;
            }
        }
        return FlowType.UNKNOWN;
    }
}
