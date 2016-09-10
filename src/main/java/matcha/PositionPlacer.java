package matcha;

import org.slf4j.Logger;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class PositionPlacer {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private final Utils utils;

    public PositionPlacer(Utils utils) {
        this.utils = utils;
    }

    public Position createShortPositionAtHighs(UsefulTickData usefulTickData, int decimalPointPlace) {
        LocalDateTime entryDate = usefulTickData.getCandleDate();
        double entry = usefulTickData.getCandleClose();
        final double distanceToTarget = usefulTickData.getCandleHigh() - usefulTickData.getCandleClose();
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        double target = entry - distanceToTarget;
        double stop = usefulTickData.getCandleHigh();
        final int ticksToStop = utils.convertTicksToInt(stop - entry, decimalPointPlace);

        LOG.info(format("Opening short position at: %.5f stop: %.5f target: %.5f ticks to target: %d ticks to stop: %d",
                entry, stop, target, ticksToTarget, ticksToStop));
        return new Position(entryDate, entry, target, stop);
    }

    public Position createLongPositionAtLows(UsefulTickData usefulTickData, int decimalPointPlace) {
        LocalDateTime entryDate = usefulTickData.getCandleDate();

        double entry = usefulTickData.getCandleClose();
        final double distanceToTarget = usefulTickData.getCandleClose() - usefulTickData.getCandleLow();
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        double target = entry + distanceToTarget;
        double stop = usefulTickData.getCandleLow();
        final int ticksToStop = utils.convertTicksToInt(entry - stop, decimalPointPlace);

        LOG.info(format("Opening long position at: %.5f stop: %.5f target: %.5f ticks to target: %d ticks to stop: %d",
                entry, stop, target, ticksToTarget, ticksToStop));

        return new Position(entryDate, entry, target, stop);
    }
}
