package matcha;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListIterator;

import static com.google.common.collect.Lists.newArrayList;

public class PositionStats {

    private int tickCounter;
    private int winners;
    private int losers;

    private List<LocalDateTime> losingDates;
    private List<LocalDateTime> winningDates;

    private double high = 0.0;
    private double low = 0.0;

    private double sma30;

    private List<Boolean> last30WinnersList;

    public PositionStats() {
        tickCounter = 0;
        winners = 0;
        losers = 0;

        losingDates = newArrayList();
        winningDates = newArrayList();
        last30WinnersList = newArrayList();

        this.sma30 = -100;
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

        int totalTrades = winningDates.size() - losingDates.size();
        if(totalTrades == 0) {
            sma30 = -100.0;
        } else {
            sma30 = totalTrades / (double) movingAverageDayCount;

            if (sma30 > high) {
                System.out.println("Winning dates: " + winningDates.size() + "Losing Dates: " + losingDates.size());
                System.out.println("New High SMA: " + candleDate + " " + sma30);
                this.high = sma30;
            }

            if (sma30 < low) {
                System.out.println("Winning dates: " + winningDates.size() + "Losing Dates: " + losingDates.size());
                System.out.println("New Low SMA: " + candleDate + " " + sma30);
                this.low = sma30;
            }
        }
    }

    void addWinner(LocalDateTime candleDate) {
        this.winningDates.add(candleDate);
        this.last30WinnersList.add(true);
    }

    public double getSma30() {
        return sma30;
    }

    public double getTradeCountSma30(int movingAverageDayCount) {
        if(last30WinnersList.size() < movingAverageDayCount) {
            return -100.0;
        }
        int winLoseCount = 0;
        for (Boolean winLose : last30WinnersList) {
            if(winLose) {
                winLoseCount++;
            }
        }
        return winLoseCount / (double) movingAverageDayCount;
    }
}
