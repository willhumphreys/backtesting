package matcha;

class Results {
    private final double tickCounter;
    private final int winners;
    private final int losers;

    public Results(double tickCounter, int winners, int losers) {


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
                "tickCounter=" + tickCounter +
                ", winners=" + winners +
                ", losers=" + losers +
                '}';
    }
}
