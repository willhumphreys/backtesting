package matcha;

public class BackTestingParameters {
    private String name;
    private int extraTicks;
    private boolean skipNextIfWinner;
    private int highLowCheckPref;

    public BackTestingParameters(String name, int extraTicks, boolean skipNextIfWinner, int highLowCheckPref) {
        this.name = name;
        this.extraTicks = extraTicks;
        this.skipNextIfWinner = skipNextIfWinner;
        this.highLowCheckPref = highLowCheckPref;
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

    public int getHighLowCheckPref() {
        return highLowCheckPref;
    }

    public static class Builder {
        private String name;
        private int extraTicks;
        private boolean skipNextIfWinner;
        private int highLowCheckPref;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setExtraTicks(int extraTicks) {
            this.extraTicks = extraTicks;
            return this;
        }

        public Builder setHighLowCheckPref(int highLowCheckPref) {
            this.highLowCheckPref = highLowCheckPref;
            return this;
        }

        public BackTestingParameters createBackTestingParameters() {
            return new BackTestingParameters(name, extraTicks, skipNextIfWinner, highLowCheckPref);
        }

        public Builder skipNextIfWinner() {
            this.skipNextIfWinner = true;
            return this;
        }


    }
}
