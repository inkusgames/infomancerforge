package ninja.egg82.analytics.common;

public enum ProgressionStatus {
    START("Start"),
    FAIL("Fail"),
    COMPLETE("Complete"),
    UNKNOWN("Unknown");

    private final String str;
    ProgressionStatus(String str) {
        this.str = str;
    }

    public String getName() { return str; }

    public static ProgressionStatus fromString(String str) {
        for (ProgressionStatus p : ProgressionStatus.values()) {
            if (p.str.equals(str)) {
                return p;
            }
        }
        return ProgressionStatus.UNKNOWN;
    }
}
