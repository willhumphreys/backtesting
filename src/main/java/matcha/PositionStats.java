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
    }

    private void cleanList(LocalDateTime candleDate, List<LocalDateTime> dateList, String name) {
        for (ListIterator<LocalDateTime> losingDatesIterator = dateList.listIterator(); losingDatesIterator.hasNext();){
            final LocalDateTime next = losingDatesIterator.next();

            if(next.isBefore(candleDate.minusDays(30))) {
                losingDatesIterator.remove();
            }
        }

        while(last30WinnersList.size() > 30) {
            last30WinnersList.remove(0);
        }
    }

    void cleanLists(LocalDateTime candleDate) {
        cleanList(candleDate, winningDates, "Winning");
        cleanList(candleDate, losingDates, "Losing");

        int totalTrades = winningDates.size() - losingDates.size();
        sma30 = totalTrades / 30.0;

        if(sma30 > high) {
            System.out.println("Winning dates: " + winningDates.size() + "Losing Dates: " + losingDates.size());
            System.out.println("New High SMA: " + candleDate + " " + sma30);
            this.high = sma30;
        }

        if(sma30 < low) {
            System.out.println("Winning dates: " + winningDates.size() + "Losing Dates: " + losingDates.size());
            System.out.println("New Low SMA: " + candleDate + " " + sma30);
            this.low = sma30;
        }
    }

    void addWinner(LocalDateTime candleDate) {
        this.winningDates.add(candleDate);
    }

    public double getSma30() {
        return sma30;
    }

    public double getTradeCountSma30() {
        if(last30WinnersList.size() < 30) {
            return -1;
        }
        int winLoseCount = 0;
        for (Boolean winLose : last30WinnersList) {
            if(winLose) {
                winLoseCount++;
            }
        }
        return winLoseCount / 30.0;
    }
}
