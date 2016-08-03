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
    private Position position;

    private boolean availableToTrade;
    private boolean timeToOpenPosition;

    private int tickCounter;
    private int winners;
    private int losers;

    @Autowired
    public PositionExecutor(Signals signals, Utils utils) {
        this.signals = signals;
        this.utils = utils;
        availableToTrade = true;
    }

    void placePositions(UsefulTickData usefulTickData) {
        if (signals.isShortSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {



            double stop = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData.getCandleLow());
            double target = usefulTickData.getCandleLow();
            double entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();

            this.position = new Position(entryDate, entry, target, stop);

            availableToTrade = false;
        }

        if (signals.isLongSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

            double stop = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData.getCandleClose());
            double target = usefulTickData.getCandleHigh();
            double entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();
            this.position = new Position(entryDate, entry, target, stop);

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
                int profitLoss = utils.convertTicksToInt(this.position.getStop() - this.position.getEntry());
                closePosition(profitLoss, STOPPED_LONG_TEMPLATE);
                losers++;
            } else if (isLongTargetExceeded(usefulTickData)) {
                final int profitLoss = utils.convertTicksToInt(this.position.getTarget() - this.position.getEntry());
                closePosition(profitLoss, TARGET_LONG_TEMPLATE);
                winners++;
            }
        } else {
            if (isShortStopTouched(usefulTickData)) {
                final int profitLoss = utils.convertTicksToInt(this.position.getEntry() - this.position.getStop());
                closePosition(profitLoss, STOPPED_SHORT_TEMPLATE);
                losers++;
            } else if (isShortTargetExceeded(usefulTickData)) {
                final int profitLoss = utils.convertTicksToInt(this.position.getEntry() - this.position.getTarget());
                closePosition(profitLoss, TARGET_SHORT_TEMPLATE);
                winners++;
            }
        }
    }

    private boolean isShortTargetExceeded(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() < this.position.getTarget();
    }

    private boolean isShortStopTouched(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() >= this.position.getStop();
    }

    private boolean isLongTargetExceeded(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() > this.position.getTarget();
    }

    private boolean isLongStopTouched(UsefulTickData usefulTickData) {
        return usefulTickData.getCandleClose() <= this.position.getStop();
    }

    private boolean isLongPosition() {
        return this.position.getTarget() > this.position.getStop();
    }

    private void closePosition(int profitLoss, String template) {
        tickCounter += profitLoss;
        availableToTrade = true;
        System.out.printf(template, entryDate, this.position.getEntry(), exitDate, this.position.getStop(), profitLoss, tickCounter);
    }

    Results getResults() {
        return new Results(tickCounter, winners, losers);
    }
}
