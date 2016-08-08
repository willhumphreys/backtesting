package matcha;

public class BackTestingParameters {
    private String name;
    private int extraTicks;

    public BackTestingParameters(String name, int extraTicks) {
        this.name = name;
        this.extraTicks = extraTicks;
    }

    public String getName() {
        return name;
    }

    public int getExtraTicks() {
        return extraTicks;
    }

    public static class Builder {
        private String name;
        private int extraTicks;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setExtraTicks(int extraTicks) {
            this.extraTicks = extraTicks;
            return this;
        }

        public BackTestingParameters createBackTestingParameters() {
            return new BackTestingParameters(name, extraTicks);
        }
    }
}
