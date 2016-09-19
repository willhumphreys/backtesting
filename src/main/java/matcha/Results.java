package matcha;

import com.google.common.base.MoreObjects;

class Results {
    private final String outputFile;
    private final PositionStats positionStats;
    private OpenOptions openOptions;

    Results(String outputFile, PositionStats positionStats, OpenOptions openOptions) {
        this.outputFile = outputFile;
        this.positionStats = positionStats;
        this.openOptions = openOptions;
    }

    OpenOptions getOpenOptions() {
        return openOptions;
    }

    PositionStats getPositionStats() {
        return positionStats;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("outputFile", outputFile)
                .add("positionStats", positionStats)
                .add("openOptions", openOptions)
                .toString();
    }
}
