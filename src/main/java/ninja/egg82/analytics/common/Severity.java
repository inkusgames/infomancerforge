package ninja.egg82.analytics.common;

public enum Severity {
    DEBUG("debug"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    CRITICAL("critical"),
    UNKNOWN("unknown");

    private final String str;
    Severity(String str) {
        this.str = str;
    }

    public String getType() {
        return str;
    }

    public static Severity fromString(String str) {
        for (Severity s : Severity.values()) {
            if (s.str.equals(str)) {
                return s;
            }
        }
        return Severity.UNKNOWN;
    }
}
