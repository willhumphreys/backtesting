package matcha;

import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListIterator;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class PositionStats {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    static final double NOT_ENOUGH_DATA_FOR_EDGE = -100.0;
    private int tickCounter;
    private int winners;
    private int shortTradeCount;

    private int losers;
    private int longTradeCount;

    private List<LocalDateTime> losingDates;
    private List<LocalDateTime> winningDates;

    private double high = 0.0;
    private double low = 0.0;
    private List<Boolean> last30WinnersList;

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

    private void cleanList(LocalDateTime candleDate, List<LocalDateTime> dateList, int movingAverageDayCount, int
            movingAverageTradeCount) {
        for (ListIterator<LocalDateTime> losingDatesIterator = dateList.listIterator(); losingDatesIterator.hasNext();){
            final LocalDateTime next = losingDatesIterator.next();

            if(next.isBefore(candleDate.minusDays(movingAverageDayCount))) {
                losingDatesIterator.remove();
            }
        }

        while(last30WinnersList.size() > movingAverageTradeCount) {
            last30WinnersList.remove(0);
        }
    }

    void cleanLists(LocalDateTime candleDate, int movingAverageDayCount, int movingAverageTradeCount) {
        cleanList(candleDate, winningDates, movingAverageDayCount, movingAverageTradeCount);
        cleanList(candleDate, losingDates, movingAverageDayCount, movingAverageTradeCount);
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
