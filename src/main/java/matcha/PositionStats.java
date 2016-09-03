package matcha;

import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListIterator;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class PositionStats {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    static final double NOT_ENOUGH_DATA_FOR_EDGE = -100.0;
    private int tickCounter;
    private int winners;
    private int losers;

    private List<LocalDateTime> losingDates;
    private List<LocalDateTime> winningDates;

    private double high = 0.0;
    private double low = 0.0;

    private List<Boolean> last30WinnersList;

    public PositionStats() {
        tickCounter = 0;
        winners = 0;
        losers = 0;

        losingDates = newArrayList();
        winningDates = newArrayList();
        last30WinnersList = newArrayList();
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

    double getSma30(int movingAverageDayCount) {
        double sma30;
        int totalTrades = winningDates.size() - losingDates.size();
        if(totalTrades == 0) {
            sma30 = NOT_ENOUGH_DATA_FOR_EDGE;
        } else {
            sma30 = totalTrades / (double) movingAverageDayCount;

            if (sma30 > high) {
                LOG.info("Winning dates: " + winningDates.size() + "Losing Dates: " + losingDates.size());
                LOG.info("New High SMA: " + sma30);
                this.high = sma30;
            }

            if (sma30 < low) {
                LOG.info("Winning dates: " + winningDates.size() + "Losing Dates: " + losingDates.size());
                LOG.info("New Low SMA: " + sma30);
                this.low = sma30;
            }
        }

        return sma30;
    }

    SmaResults getTradeCountSma(int movingAverageDayCount) {
        boolean newHigh = false;
        boolean newLow = false;
        if(last30WinnersList.size() < movingAverageDayCount) {
            return new SmaResults(NOT_ENOUGH_DATA_FOR_EDGE, newHigh, newLow);
        }
        int winLoseCount = 0;
        for (Boolean winLose : last30WinnersList) {
            if(winLose) {
                winLoseCount++;
            } else {
                winLoseCount--;
            }
        }
        final double movingAverage = winLoseCount / (double) movingAverageDayCount;

        if (movingAverage > high) {
            LOG.info("Winners: " + winners + "Losing Losers: " + losers);
            LOG.info("New High SMA: " + movingAverage);

            this.high = movingAverage;
            newHigh = true;
        }

        if (movingAverage < low) {
            LOG.info("Winners: " + winners + "Losers: " + losers);
            LOG.info("New Low SMA: " + movingAverage);
            this.low = movingAverage;
            newLow = true;
        }
        return new SmaResults(movingAverage, newHigh, newLow);
    }
}
