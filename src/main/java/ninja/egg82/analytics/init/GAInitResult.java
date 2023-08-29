package ninja.egg82.analytics.init;

public class GAInitResult {
    private final boolean enabled;
    private final long tsOffset;
    private final String[] flags;

    private final long startTime = System.currentTimeMillis();

    public GAInitResult(boolean enabled, long tsOffset, String[] flags) {
        this.enabled = enabled;
        this.tsOffset = tsOffset;
        this.flags = flags;
    }

    public boolean getEnabled() { return enabled; }

    public long getTSOffset() { return tsOffset; }

    public String[] getFlags() { return flags; }

    public long getStartTime() { return startTime; }
}
