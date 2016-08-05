package matcha;

public class PositionStats {

    private int tickCounter;
    private int winners;
    private int losers;

    public PositionStats() {
        tickCounter = 0;
        winners = 0;
        losers = 0;
    }

    public void addToTickCounter(int ticks) {
        tickCounter+= ticks;
    }

    public void incrementWinners() {
        winners++;
    }

    public void incrementLosers() {
        losers++;
    }

    public int getTickCounter() {
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
        return "PositionStats{" +
                "tickCounter=" + tickCounter +
                ", winners=" + winners +
                ", losers=" + losers +
                '}';
    }
}
