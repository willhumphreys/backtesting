package matcha;

public class OpenOptions {
    private final int highLowPref;
    private final boolean aboveBelowMovingAverages;
    private final boolean aboveBelowBands;

    public OpenOptions(int highLowPref, boolean aboveBelowMovingAverages, boolean aboveBelowBands) {

        this.highLowPref = highLowPref;
        this.aboveBelowMovingAverages = aboveBelowMovingAverages;
        this.aboveBelowBands = aboveBelowBands;
    }

    public int getHighLowPref() {
        return highLowPref;
    }

    public boolean isAboveBelowMovingAverages() {
        return aboveBelowMovingAverages;
    }

    public boolean isAboveBelowBands() {
        return aboveBelowBands;
    }
}
