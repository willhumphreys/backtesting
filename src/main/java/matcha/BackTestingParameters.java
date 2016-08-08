package matcha;

public class BackTestingParameters {
    private String name;
    private int extraTicks;
    private boolean skipNextIfWinner;

    public BackTestingParameters(String name, int extraTicks, boolean skipNextIfWinner) {
        this.name = name;
        this.extraTicks = extraTicks;
        this.skipNextIfWinner = skipNextIfWinner;
    }

    public String getName() {
        return name;
    }

    public int getExtraTicks() {
        return extraTicks;
    }

    public boolean isSkipNextIfWinner() {
        return skipNextIfWinner;
    }

    public static class Builder {
        private String name;
        private int extraTicks;
        private boolean skipNextIfWinner;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setExtraTicks(int extraTicks) {
            this.extraTicks = extraTicks;
            return this;
        }

        public BackTestingParameters createBackTestingParameters() {
            return new BackTestingParameters(name, extraTicks, skipNextIfWinner);
        }

        public Builder skipNextIfWinner() {
            this.skipNextIfWinner = true;
            return this;
        }


    }
}
