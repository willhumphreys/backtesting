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

    public PositionStats() {
        tickCounter = 0;
        winners = 0;
        losers = 0;

        losingDates = newArrayList();
        winningDates = newArrayList();
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

    private void cleanList(LocalDateTime candleDate, List<LocalDateTime> dateList) {
        for (ListIterator<LocalDateTime> losingDatesIterator = dateList.listIterator(); losingDatesIterator.hasNext();){
            final LocalDateTime next = losingDatesIterator.next();

            if(next.isBefore(candleDate.minusDays(30))) {
                losingDatesIterator.remove();
            }
        }
    }

    void cleanLists(LocalDateTime candleDate) {
        cleanList(candleDate, winningDates);
        cleanList(candleDate, losingDates);
    }

    void addWinner(LocalDateTime candleDate) {
        this.winningDates.add(candleDate);
    }
}
