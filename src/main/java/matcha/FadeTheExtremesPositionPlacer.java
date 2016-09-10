package matcha;

import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class FadeTheExtremesPositionPlacer implements PositionPlacer {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private final Utils utils;

    FadeTheExtremesPositionPlacer(Utils utils) {
        this.utils = utils;
    }

    private Position createShort(UsefulTickData usefulTickData, int decimalPointPlace) {
        LocalDateTime entryDate = usefulTickData.getCandleDate();
        double entry = usefulTickData.getCandleClose();
        final double distanceToTarget = usefulTickData.getCandleHigh() - usefulTickData.getCandleClose();
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        double target = entry - distanceToTarget;
        double stop = usefulTickData.getCandleHigh();
        final int ticksToStop = utils.convertTicksToInt(stop - entry, decimalPointPlace);

        LOG.info(format("Opening short position at: %.5f stop: %.5f target: %.5f ticks " +
                        "to target: %d ticks to stop: %d",
                entry, stop, target, ticksToTarget, ticksToStop));
        return new Position(entryDate, entry, target, stop);
    }

    private Position createLong(UsefulTickData usefulTickData, int decimalPointPlace) {
        LocalDateTime entryDate = usefulTickData.getCandleDate();

        double entry = usefulTickData.getCandleClose();
        final double distanceToTarget = usefulTickData.getCandleClose() - usefulTickData.getCandleLow();
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        double target = entry + distanceToTarget;
        double stop = usefulTickData.getCandleLow();
        final int ticksToStop = utils.convertTicksToInt(entry - stop, decimalPointPlace);

        LOG.info(format("Opening long position at: %.5f stop: %.5f target: %.5f ticks " +
                        "to target: %d ticks to stop: %d",
                entry, stop, target, ticksToTarget, ticksToStop));

        return new Position(entryDate, entry, target, stop);
    }

    private boolean getLowCheck(UsefulTickData usefulTickData, int highLowCheckPref) {
        boolean lowCheck;
        switch (highLowCheckPref) {
            case 0:
                //Current Candle is below last candle.
                lowCheck = usefulTickData.getCandleLow() < usefulTickData.getPreviousCandleLow();
                break;
            case 1:
                //New low for the day is put in.
                lowCheck = usefulTickData.getTodaysLow() < usefulTickData.getLowOfDayForPreviousHour();
                break;
            case 2:
                //We don't put in a new low.
                lowCheck = usefulTickData.getTodaysLow() >= usefulTickData.getLowOfDayForPreviousHour();
                break;
            default:
                throw new IllegalArgumentException("Invalid lowCheck value");
        }
        return lowCheck;
    }

    private boolean getHighCheck(UsefulTickData usefulTickData, int highCheckPref) {
        boolean highCheck;
        switch (highCheckPref) {
            case 0:
                highCheck = usefulTickData.getCandleHigh() > usefulTickData.getPreviousCandleHigh();
                break;
            case 1:
                highCheck = usefulTickData.getTodaysHigh() > usefulTickData.getHighOfDayForPreviousHour();
                break;
            case 2:
                //We don't put in a new high.
                highCheck = usefulTickData.getTodaysHigh() <= usefulTickData.getHighOfDayForPreviousHour();
                break;
            default:
                throw new IllegalArgumentException("Invalid highCheck value");
        }
        return highCheck;
    }

    private boolean isAShortSignal(UsefulTickData usefulTickData, int highLowCheckPref) {
        final boolean takeOutYesterdaysHigh = usefulTickData.isTakeOutYesterdaysHigh();
        final boolean closeNegative = usefulTickData.isCloseNegative();
        final boolean closeBelowYesterdaysHigh = usefulTickData.isCloseBelowYesterdaysHigh();
        final boolean openBelowYesterdaysHigh = usefulTickData.isOpenBelowYesterdaysHigh();
        final boolean highCheck = getHighCheck(usefulTickData, highLowCheckPref);
        return takeOutYesterdaysHigh &&
                closeNegative &&
                closeBelowYesterdaysHigh &&
                openBelowYesterdaysHigh &&
                highCheck;
    }

    private boolean isALongSignal(UsefulTickData usefulTickData, int highLowCheckPref) {
        final boolean takeOutYesterdaysLow = usefulTickData.isTakeOutYesterdaysLow();
        final boolean closePositive = usefulTickData.isClosePositive();
        final boolean closeAboveYesterdaysLow = usefulTickData.isCloseAboveYesterdaysLow();
        return takeOutYesterdaysLow &&
                closePositive &&
                closeAboveYesterdaysLow &&
                usefulTickData.isOpenAboveYesterdaysLow() &&
                getLowCheck(usefulTickData, highLowCheckPref);
    }

    @Override
    public Optional<Position> placePositions(UsefulTickData usefulTickData,
                                             BackTestingParameters backTestingParameters,
                                             int decimalPointPlace) {

        if (isALongSignal(usefulTickData, backTestingParameters.getHighLowCheckPref())) {
            final Position longPositionAtLows = createLong(usefulTickData, decimalPointPlace);
            return Optional.of(longPositionAtLows);
        }

        if (isAShortSignal(usefulTickData, backTestingParameters.getHighLowCheckPref())) {
            final Position shortPositionAtHighs = createShort(usefulTickData, decimalPointPlace);
            return Optional.of(shortPositionAtHighs);
        }
        return Optional.empty();
    }
}
