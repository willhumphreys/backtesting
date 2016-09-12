package matcha;

import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class PositionStats {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private int tickCounter;
    private int winners;
    private int shortTradeCount;
    private int losers;
    private int longTradeCount;

    private final List<LocalDateTime> losingDates;
    private final List<LocalDateTime> winningDates;

    private final List<Boolean> last30WinnersList;

    PositionStats() {
        tickCounter = 0;
        winners = 0;
        losers = 0;

        losingDates = newArrayList();
        winningDates = newArrayList();
        last30WinnersList = newArrayList();
    }

    void addToTickCounter(int ticks) {
        tickCounter+= ticks;
    }

    void incrementWinners() {
        winners++;
    }

    void incrementLosers() {
        losers++;
    }

    int getTickCounter() {
        return tickCounter;
    }


    int getWinners() {
        return winners;
    }

    int getLosers() {
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

    void addLoser(LocalDateTime candleDate) {
        this.losingDates.add(candleDate);
        this.last30WinnersList.add(false);
    }

    void addWinner(LocalDateTime candleDate) {
        this.winningDates.add(candleDate);
        this.last30WinnersList.add(true);
    }

    int getShortTradeCount() {
        return shortTradeCount;
    }

    int getLongTradeCount() {
        return longTradeCount;
    }

    void incrementShortTrades() {
        shortTradeCount++;
    }

    void incrementLongTrades() {
        longTradeCount++;
    }
}
