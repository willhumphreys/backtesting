package matcha;

class OpenOptions {
    private final int highLowPref;
    private final boolean aboveBelowMovingAverages;
    private final boolean aboveBelowBands;

    OpenOptions(int highLowPref, boolean aboveBelowMovingAverages, boolean aboveBelowBands) {
        this.highLowPref = highLowPref;
        this.aboveBelowMovingAverages = aboveBelowMovingAverages;
        this.aboveBelowBands = aboveBelowBands;
    }

    int getHighLowPref() {
        return highLowPref;
    }

    boolean isAboveBelowMovingAverages() {
        return aboveBelowMovingAverages;
    }

    boolean isAboveBelowBands() {
        return aboveBelowBands;
    }

    static class Builder {
        private int highLowPref;
        private boolean aboveBelowMovingAverages;
        private boolean aboveBelowBands;

        Builder setHighLowPref(int highLowPref) {
            this.highLowPref = highLowPref;
            return this;
        }

        Builder setAboveBelowMovingAverages(boolean aboveBelowMovingAverages) {
            this.aboveBelowMovingAverages = aboveBelowMovingAverages;
            return this;
        }

        Builder setAboveBelowBands(boolean aboveBelowBands) {
            this.aboveBelowBands = aboveBelowBands;
            return this;
        }

        OpenOptions createOpenOptions() {
            return new OpenOptions(highLowPref, aboveBelowMovingAverages, aboveBelowBands);
        }
    }
}
