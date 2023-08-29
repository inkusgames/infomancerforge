package ninja.egg82.analytics.init;

import ninja.egg82.analytics.common.GAInputBase;

public class GAInit extends GAInputBase {
    private GAInit() {}

    public static GAInit.Builder builder() { return new GAInit.Builder(); }

    public static class Builder {
        private final GAInit init = new GAInit();

        private Builder() {}

        public GAInit build() { return init; }
    }
}
