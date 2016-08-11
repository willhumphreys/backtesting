package matcha;

public class BackTestingParameters {
    private String name;
    private int extraTicks;
    private boolean skipNextIfWinner;
    private int highLowCheckPref;
    private boolean lowsOnly;
    private boolean highsOnly;
    private final boolean skipIf4DownDays;
    private final boolean skipIf4UpDays;

    public BackTestingParameters(String name, int extraTicks, boolean skipNextIfWinner, int highLowCheckPref,
                                 boolean lowsOnly, boolean highsOnly, boolean skipIf4DownDays, boolean skipIf4UpDays) {
        this.name = name;
        this.extraTicks = extraTicks;
        this.skipNextIfWinner = skipNextIfWinner;
        this.highLowCheckPref = highLowCheckPref;
        this.lowsOnly = lowsOnly;
        this.highsOnly = highsOnly;
        this.skipIf4DownDays = skipIf4DownDays;
        this.skipIf4UpDays = skipIf4UpDays;
    }

    public boolean isSkipIf4DownDays() {
        return skipIf4DownDays;
    }

    public boolean isSkipIf4UpDays() {
        return skipIf4UpDays;
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

    public boolean isLowsOnly() {
        return lowsOnly;
    }

    public boolean isHighsOnly() {
        return highsOnly;
    }

    public static class Builder {
        private String name;
        private int extraTicks;
        private boolean skipNextIfWinner;
        private int highLowCheckPref;
        private boolean lowsOnly;
        private boolean highsOnly;
        private boolean skipIf4DownDays;
        private boolean skipIf4UpDays;

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
            return new BackTestingParameters(name, extraTicks, skipNextIfWinner, highLowCheckPref, lowsOnly, highsOnly, skipIf4DownDays, skipIf4UpDays);
        }

        public Builder skipNextIfWinner() {
            this.skipNextIfWinner = true;
            return this;
        }


        public Builder setLowsOnly() {
            this.lowsOnly = true;
            return this;
        }

        public Builder setHighsOnly() {
            this.highsOnly = true;
            return this;
        }

        public Builder skipIf4DownDays() {
            this.skipIf4DownDays = true;
            return this;
        }

        public Builder skipIf4UpDays() {
            this.skipIf4UpDays = true;
            return this;
        }
    }
}
