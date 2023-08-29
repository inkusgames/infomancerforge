package ninja.egg82.analytics.common;

public enum ConnectionType {
    WWAN("wwan"),
    WIFI("wifi"),
    LAN("lan"),
    OFFLINE("offline"),
    UNKNOWN("unknown");

    private final String str;
    ConnectionType(String str) {
        this.str = str;
    }

    public String getConnectionString() { return str; }

    public ConnectionType fromString(String str) {
        for (ConnectionType t : ConnectionType.values()) {
            if (t.str.equals(str)) {
                return t;
            }
        }
        return ConnectionType.UNKNOWN;
    }
}
