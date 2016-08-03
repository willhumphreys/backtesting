package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionExecutor {

    private Utils utils;

    private final Signals signals;

    private String entryDate;
    private String exitDate;
    private double entry;
    private double stop;
    private double target;


    private boolean availableToTrade;
    private boolean timeToOpenPosition;

    private int tickCounter;
    private int winners;
    private int losers;

    @Autowired
    public PositionExecutor(Signals signals, Utils utils) {
        this.signals = signals;
        this.utils = utils;
        entry = -1.0;
        stop = -1.0;
        target = -1.0;
        availableToTrade = true;
    }


    void placePositions(UsefulTickData usefulTickData)  {
        if (signals.isShortSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

            stop = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData.getCandleLow());
            target = usefulTickData.getCandleLow();
            entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();
            availableToTrade = false;

        }

        if (signals.isLongSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

            stop = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData.getCandleClose());
            target = usefulTickData.getCandleHigh();
            entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();
            availableToTrade = false;
        }
    }

    void setTimeToOpenPosition(boolean timeToOpenPosition) {
        this.timeToOpenPosition = timeToOpenPosition;
    }

    void managePosition(UsefulTickData usefulTickData) {
        if (!availableToTrade) {
            if (target > stop) {

                if (usefulTickData.getCandleClose() <= stop) {
                    int profitLoss = utils.convertTicksToInt(stop - entry);
                    tickCounter+=profitLoss;
                    losers++;
                    availableToTrade = true;
                    exitDate = usefulTickData.getCandleDate();
                    System.out.printf("Close long %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, stop, profitLoss, tickCounter);
                } else if (usefulTickData.getCandleClose() > target) {
                    final int profitLoss = utils.convertTicksToInt(target - entry);
                    tickCounter += profitLoss;
                    winners++;
                    availableToTrade = true;
                    System.out.printf("Close long %s %.5f target: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, target, profitLoss, tickCounter);
                }
            } else {

                if (usefulTickData.getCandleClose() >= stop) {
                    final int profitLoss = utils.convertTicksToInt(entry - stop);
                    tickCounter += profitLoss;
                    losers++;
                    availableToTrade = true;
                    System.out.printf("Close short %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, stop, profitLoss, tickCounter);
                } else if (usefulTickData.getCandleClose() < target) {
                    final int profitLoss = utils.convertTicksToInt(entry - target);
                    tickCounter += profitLoss;
                    winners++;
                    availableToTrade = true;
                    System.out.printf("Close short %s %.5f target: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, target, profitLoss, tickCounter);
                }
            }
        }
    }

    Results getResults() {
        return new Results(tickCounter, winners, losers);
    }
}
