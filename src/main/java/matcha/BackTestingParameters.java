package matcha;

class BackTestingParameters {
    private final String name;
    private final int extraTicks;
    private final int highLowCheckPref;

    private BackTestingParameters(String name,
                                  int extraTicks,
                                  int highLowCheckPref) {
        this.name = name;
        this.extraTicks = extraTicks;
        this.highLowCheckPref = highLowCheckPref;
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

    static class Builder {
        private String name;
        private int extraTicks;
        private int highLowCheckPref;
        private final double targetMultiplier = 1;

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
                    highLowCheckPref
            );
        }

        Builder fadeTheBreakout() {
            return this;
        }
    }
}
