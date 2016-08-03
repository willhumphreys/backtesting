package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionExecutor {

    private static final String STOPPED_LONG_TEMPLATE =
            "Close long %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n";
    private static final String TARGET_LONG_TEMPLATE =
            "Close long %s %.5f target: %s %.5f ticks %d cumulative profit %d%n";
    private static final String STOPPED_SHORT_TEMPLATE =
            "Close short %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n";
    private static final String TARGET_SHORT_TEMPLATE =
            "Close short %s %.5f target: %s %.5f ticks %d cumulative profit %d%n";

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

    void placePositions(UsefulTickData usefulTickData) {
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
        if (availableToTrade) {
            return;
        }

        if (isLongPosition()) {
            exitDate = usefulTickData.getCandleDate();
            if (isLongStopTouched(usefulTickData)) {
                int profitLoss = utils.convertTicksToInt(stop - entry);
                closePosition(profitLoss, STOPPED_LONG_TEMPLATE);
                losers++;
            } else if (isLongTargetExceeded(usefulTickData)) {
                final int profitLoss = utils.convertTicksToInt(target - entry);
                closePosition(profitLoss, TARGET_LONG_TEMPLATE);
                winners++;
            }
        } else {
            if (isShortStopTouched(usefulTickData)) {
                final int profitLoss = utils.convertTicksToInt(entry - stop);
                closePosition(profitLoss, STOPPED_SHORT_TEMPLATE);
                losers++;
            } else if (isShortTargetExceeded(usefulTickData)) {
                final int profitLoss = utils.convertTicksToInt(entry - target);
                closePosition(profitLoss, TARGET_SHORT_TEMPLATE);
                winners++;
            }
        }
    }

    private boolean isShortTargetExceeded(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() < target;
    }

    private boolean isShortStopTouched(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() >= stop;
    }

    private boolean isLongTargetExceeded(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() > target;
    }

    private boolean isLongStopTouched(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() <= stop;
    }

    private boolean isLongPosition() {
        return target > stop;
    }

    private void closePosition(int profitLoss, String template) {
        tickCounter += profitLoss;
        availableToTrade = true;
        System.out.printf(template, entryDate, entry, exitDate, stop, profitLoss, tickCounter);
    }

    Results getResults() {
        return new Results(tickCounter, winners, losers);
    }
}
