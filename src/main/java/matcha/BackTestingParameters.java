package matcha;

class BackTestingParameters {
    private String name;
    private int extraTicks;
    private int highLowCheckPref;
    private double targetMultiplier;

    private BackTestingParameters(String name,
                                  int extraTicks,
                                  int highLowCheckPref,
                                  double targetMultiplier) {
        this.name = name;
        this.extraTicks = extraTicks;
        this.highLowCheckPref = highLowCheckPref;
        this.targetMultiplier = targetMultiplier;
    }

    String getName() {
        return name;
    }

    int getExtraTicks() {
        return extraTicks;
    }

    int getHighLowCheckPref() {
        return highLowCheckPref;
    }

    double getTargetMultiplier() {
        return targetMultiplier;
    }

    static class Builder {
        private String name;
        private int extraTicks;
        private int highLowCheckPref;
        private double targetMultiplier = 1;

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        Builder setExtraTicks(int extraTicks) {
            this.extraTicks = extraTicks;
            return this;
        }

        Builder setHighLowCheckPref(int highLowCheckPref) {
            this.highLowCheckPref = highLowCheckPref;
            return this;
        }

        BackTestingParameters createBackTestingParameters() {
            return new BackTestingParameters(
                    name,
                    extraTicks,
                    highLowCheckPref,
                    targetMultiplier);
        }

        Builder fadeTheBreakout() {
            return this;
        }
    }
}
