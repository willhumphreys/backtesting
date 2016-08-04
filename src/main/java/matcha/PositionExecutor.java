package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PositionExecutor {

    private static final String STOPPED_LONG_TEMPLATE =
            "%s Close long  %s @ %.5f stopped: %s %.5f ticks %d cumulative profit %d%n";
    private static final String TARGET_LONG_TEMPLATE =
            "%s Close long  %s @ %.5f target: %s %.5f ticks %d cumulative profit %d%n";
    private static final String STOPPED_SHORT_TEMPLATE =
            "%s Close short %s @ %.5f stopped: %s %.5f ticks %d cumulative profit %d%n";
    private static final String TARGET_SHORT_TEMPLATE =
            "%s Close short %s @ %.5f target: %s %.5f ticks %d cumulative profit %d%n";

    private final Utils utils;
    private final Signals signals;

    private String entryDate;
    private String exitDate;

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

    Optional<Position> placePositions(UsefulTickData usefulTickData, int extraTicksCount) {

        //1.094295
        double extraTicks = extraTicksCount / 100000.0;

        if (signals.isShortSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {
            double stop = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData
                    .getCandleLow() + extraTicks);
            double target = usefulTickData.getCandleLow() - extraTicks;
            double entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();

            Position position = new Position(entryDate, entry, target, stop);

            availableToTrade = false;

            return Optional.of(position);
        }

        if (signals.isLongSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

            double stop = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData
                    .getCandleClose() - extraTicks);
            double target = usefulTickData.getCandleHigh() + extraTicks;
            double entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();
            Position position = new Position(entryDate, entry, target, stop);

            availableToTrade = false;
            return Optional.of(position);
        }

        return Optional.empty();
    }

    void setTimeToOpenPosition(boolean timeToOpenPosition) {
        this.timeToOpenPosition = timeToOpenPosition;
    }

    void managePosition(UsefulTickData usefulTickData, Position position) {
        if (availableToTrade) {
            return;
        }

        exitDate = usefulTickData.getCandleDate();
        if (isLongPosition(position)) {

            if (isLongStopTouched(usefulTickData, position)) {
                int profitLoss = utils.convertTicksToInt(position.getStop() - position.getEntry());
                closePosition(profitLoss, STOPPED_LONG_TEMPLATE, position.getStop(), position);
                losers++;
            } else if (isLongTargetExceeded(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getTarget() - position.getEntry());
                closePosition(profitLoss, TARGET_LONG_TEMPLATE, position.getTarget(), position);
                winners++;
            }
        } else {
            if (isShortStopTouched(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getStop());
                closePosition(profitLoss, STOPPED_SHORT_TEMPLATE, position.getStop(), position);
                losers++;
            } else if (isShortTargetExceeded(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getTarget());
                closePosition(profitLoss, TARGET_SHORT_TEMPLATE, position.getTarget(), position);
                winners++;
            }
        }
    }

    private boolean isShortTargetExceeded(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getCandleLow() < position.getTarget();
    }

    private boolean isShortStopTouched(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getCandleHigh() >= position.getStop();
    }

    private boolean isLongTargetExceeded(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getCandleHigh() > position.getTarget();
    }

    private boolean isLongStopTouched(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getCandleLow() <= position.getStop();
    }

    private boolean isLongPosition(Position position) {
        return position.getTarget() > position.getStop();
    }

    private void closePosition(int profitLoss, String template, final double stopOrTarget, Position position) {
        tickCounter += profitLoss;
        availableToTrade = true;
        String winLose = "LOSE";
        if(profitLoss > 0) {
            winLose = "WIN";
        }


        System.out.printf(template, winLose, entryDate, position.getEntry(), exitDate, stopOrTarget, profitLoss, tickCounter);
        position.close();
    }

    Results getResults() {
        return new Results(tickCounter, winners, losers);
    }
}
