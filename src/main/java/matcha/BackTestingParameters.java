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
    private boolean fadeTheBreakout;
    private final boolean after4DaysOfNegativeCloses;
    private final boolean after4DaysOfPositiveCloses;
    private boolean withEdge;
    private boolean withTradeCountEdge;
    private double edgeLevel;

    public BackTestingParameters(String name,
                                 int extraTicks,
                                 boolean skipNextIfWinner,
                                 int highLowCheckPref,
                                 boolean lowsOnly,
                                 boolean highsOnly,
                                 boolean skipIf4DownDays,
                                 boolean skipIf4UpDays,
                                 boolean fadeTheBreakout,
                                 boolean after4DaysOfNegativeCloses,
                                 boolean after4DaysOfPositiveCloses,
                                 boolean withEdge,
                                 boolean withTradeCountEdge,
                                 double edgeLevel) {
        this.name = name;
        this.extraTicks = extraTicks;
        this.skipNextIfWinner = skipNextIfWinner;
        this.highLowCheckPref = highLowCheckPref;
        this.lowsOnly = lowsOnly;
        this.highsOnly = highsOnly;
        this.skipIf4DownDays = skipIf4DownDays;
        this.skipIf4UpDays = skipIf4UpDays;
        this.fadeTheBreakout = fadeTheBreakout;
        this.after4DaysOfNegativeCloses = after4DaysOfNegativeCloses;
        this.after4DaysOfPositiveCloses = after4DaysOfPositiveCloses;
        this.withEdge = withEdge;
        this.withTradeCountEdge = withTradeCountEdge;
        this.edgeLevel = edgeLevel;
    }

    /**
     * Gets edgeLevel
     *
     * @return value of edgeLevel
     */
    public double getEdgeLevel() {
        return edgeLevel;
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

    public boolean isFadeTheBreakout() {

        return fadeTheBreakout;
    }

    public boolean isAfter4DaysOfNegativeCloses() {
        return after4DaysOfNegativeCloses;
    }

    public boolean isAfter4DaysOfPositiveCloses() {
        return after4DaysOfPositiveCloses;
    }

    public boolean isWithEdge() {
        return withEdge;
    }

    public boolean isWithTradeCountEdge() {
        return withTradeCountEdge;
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
        private boolean fadeTheBreakout;
        private boolean after4DaysOfNegativeCloses;
        private boolean after4DaysOfPositiveCloses;
        private boolean withEdge;
        private boolean withTradeCountEdge;
        private double edgeLevel;

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
            return new BackTestingParameters(
                    name,
                    extraTicks,
                    skipNextIfWinner,
                    highLowCheckPref,
                    lowsOnly,
                    highsOnly,
                    skipIf4DownDays,
                    skipIf4UpDays,
                    fadeTheBreakout,
                    after4DaysOfNegativeCloses,
                    after4DaysOfPositiveCloses,
                    withEdge,
                    withTradeCountEdge,
                    edgeLevel
            );
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

        public Builder fadeTheBreakout() {
            this.fadeTheBreakout = true;
            return this;
        }

        public Builder onlyAfter4DaysOfNegativeCloses() {
            this.after4DaysOfNegativeCloses = true;
            return this;

        }

        public Builder onlyAfter4DaysOfPositiveCloses() {
            this.after4DaysOfPositiveCloses = true;
            return this;
        }

        public Builder withEdge(double edgeLevel) {
            this.withEdge = true;
            this.edgeLevel = edgeLevel;
            return this;
        }

        public Builder withTradeCountEdge() {
            this.withTradeCountEdge = true;
            return this;
        }
    }
}
