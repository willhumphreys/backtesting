package matcha;

class Results {
    private String outputFile;
    private final double tickCounter;
    private final int winners;
    private final int losers;

    public Results(String outputFile, double tickCounter, int winners, int losers) {
        this.outputFile = outputFile;
        this.tickCounter = tickCounter;
        this.winners = winners;
        this.losers = losers;
    }

    public double getTickCounter() {
        return tickCounter;
    }

    public int getWinners() {
        return winners;
    }

    public int getLosers() {
        return losers;
    }

    @Override
    public String toString() {
        return "Results{" +
                "outputFile='" + outputFile + '\'' +
                ", tickCounter=" + tickCounter +
                ", winners=" + winners +
                ", losers=" + losers +
                '}';
    }
}
