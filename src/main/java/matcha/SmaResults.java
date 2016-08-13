package matcha;

public class SmaResults {
    private final double movingAverage;
    private final boolean newHigh;
    private final boolean newLow;

    public SmaResults(double movingAverage, boolean newHigh, boolean newLow) {
        this.movingAverage = movingAverage;
        this.newHigh = newHigh;
        this.newLow = newLow;
    }

    public boolean isNewHigh() {
        return this.newHigh;
    }

    public boolean isNewLow() {
        return this.newLow;
    }

    public double getMovingAverage() {
        return movingAverage;
    }


}
