package ninja.egg82.analytics.common;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    TRANS("trans"),
    UNKNOWN("unknown");

    private final String str;
    Gender(String str) {
        this.str = str;
    }

    public String getGenderString() { return str; }

    public static Gender fromString(String str) {
        for (Gender g : Gender.values()) {
            if (g.str.equals(str)) {
                return g;
            }
        }
        return Gender.UNKNOWN;
    }
}
