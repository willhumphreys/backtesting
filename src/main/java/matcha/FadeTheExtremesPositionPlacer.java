package matcha;

import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class FadeTheExtremesPositionPlacer implements PositionPlacer {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private final Utils utils;
    private final int highLowCheckPref;

    FadeTheExtremesPositionPlacer(Utils utils, int highLowCheckPref) {
        this.utils = utils;
        this.highLowCheckPref = highLowCheckPref;
    }

    private Position createShort(UsefulTickData usefulTickData, int decimalPointPlace) {
        LocalDateTime entryDate = usefulTickData.getCandleDate();
        BigDecimal entry = usefulTickData.getCandleClose();
        final BigDecimal distanceToTarget = usefulTickData.getCandleHigh().subtract(usefulTickData.getCandleClose());
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        BigDecimal target = entry.subtract(distanceToTarget);
        BigDecimal stop = usefulTickData.getCandleHigh();
        final int ticksToStop = utils.convertTicksToInt(stop.subtract(entry), decimalPointPlace);

        LOG.info(format("Opening short position at: %.5f stop: %.5f target: %.5f ticks " +
                        "to target: %d ticks to stop: %d",
                entry, stop, target, ticksToTarget, ticksToStop));
        return new Position(entryDate, entry, target, stop);
    }

    private Position createLong(UsefulTickData usefulTickData, int decimalPointPlace) {
        LocalDateTime entryDate = usefulTickData.getCandleDate();

        BigDecimal entry = usefulTickData.getCandleClose();
        final BigDecimal distanceToTarget = usefulTickData.getCandleClose().subtract(usefulTickData.getCandleLow());
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        BigDecimal target = entry.add(distanceToTarget);
        BigDecimal stop = usefulTickData.getCandleLow();
        final int ticksToStop = utils.convertTicksToInt(entry.subtract(stop), decimalPointPlace);

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
                lowCheck = usefulTickData.getCandleLow().compareTo(usefulTickData.getPreviousCandleLow()) < 0;
                break;
            case 1:
                //New low for the day is put in.
                lowCheck = usefulTickData.getTodaysLow().compareTo(usefulTickData.getLowOfDayForPreviousHour()) < 0;
                break;
            case 2:
                //We don't put in a new low.
                lowCheck = usefulTickData.getTodaysLow().compareTo(usefulTickData.getLowOfDayForPreviousHour()) >= 0;
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
                highCheck = usefulTickData.getCandleHigh().compareTo(usefulTickData.getPreviousCandleHigh()) > 0;
                break;
            case 1:
                highCheck = usefulTickData.getTodaysHigh().compareTo(usefulTickData.getHighOfDayForPreviousHour()) > 0;
                break;
            case 2:
                //We don't put in a new high.
                highCheck = usefulTickData.getTodaysHigh().compareTo(usefulTickData.getHighOfDayForPreviousHour()) <= 0;
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
                                             int decimalPointPlace) {

        if (isALongSignal(usefulTickData, highLowCheckPref)) {
            return Optional.of(createLong(usefulTickData, decimalPointPlace));
        }

        if (isAShortSignal(usefulTickData, highLowCheckPref)) {
            return Optional.of(createShort(usefulTickData, decimalPointPlace));
        }
        return Optional.empty();
    }
}
