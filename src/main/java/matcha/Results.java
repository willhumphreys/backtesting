package matcha;

class Results {
    private final String outputFile;
    private final PositionStats positionStats;

    public Results(String outputFile, PositionStats positionStats) {
        this.outputFile = outputFile;
        this.positionStats = positionStats;
    }


    /**
     * Gets positionStats
     *
     * @return value of positionStats
     */
    public PositionStats getPositionStats() {
        return positionStats;
    }

    @Override
    public String toString() {
        return "Results{" +
                "outputFile='" + outputFile + '\'' +
                ", tickCounter=" + positionStats.getTickCounter() +
                ", winners=" + positionStats.getWinners() +
                ", losers=" + positionStats.getLosers() +
                '}';
    }
}
