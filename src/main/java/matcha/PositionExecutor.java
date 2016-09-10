package matcha;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class PositionExecutor {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private static final String STOPPED_LONG_CSV_TEMPLATE = "%s,long,%.5f,stopped,%s,%.5f,%d%n";
    private static final String TARGET_LONG_CSV_TEMPLATE = "%s,long,%.5f,target,%s,%.5f,%d%n";
    private static final String STOPPED_SHORT_CSV_TEMPLATE = "%s,short,%.5f,stopped,%s,%.5f,%d%n";
    private static final String TARGET_SHORT_CSV_TEMPLATE = "%s,short,%.5f,target,%s,%.5f,%d%n";

    private final Utils utils;
    private final Signals signals;
    private LocalDateTime entryDate;
    private LocalDateTime exitDate;
    private boolean timeToOpenPosition;

    PositionExecutor(Signals signals, Utils utils) throws IOException {
        this.signals = signals;
        this.utils = utils;
    }

    Path createResultsDirectory(final Path outputDirectory) throws IOException {

        if (!Files.exists(outputDirectory)) {
            Files.createDirectory(outputDirectory);
        }

        return outputDirectory;
    }

    Optional<Position> placePositions(UsefulTickData usefulTickData,
                                      BackTestingParameters backTestingParameters, int decimalPointPlace) {

        if (signals.isALongSignal(usefulTickData, backTestingParameters.getHighLowCheckPref()) && timeToOpenPosition) {
            return Optional.of(createLongPositionAtLows(usefulTickData, 0, decimalPointPlace));
        }

        if (signals.isAShortSignal(usefulTickData, backTestingParameters.getHighLowCheckPref()) && timeToOpenPosition) {
            return Optional.of(createShortPositionAtHighs(usefulTickData, 0, decimalPointPlace));
        }
        return Optional.empty();
    }

    private Position createShortPositionAtHighs(UsefulTickData usefulTickData, double extraTicks, int decimalPointPlace) {
        entryDate = usefulTickData.getCandleDate();
        double entry = usefulTickData.getCandleClose();
        final double distanceToTarget = usefulTickData.getCandleHigh() - usefulTickData.getCandleClose() - extraTicks;
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        double target = entry - distanceToTarget;
        double stop = usefulTickData.getCandleHigh() + extraTicks;
        final int ticksToStop = utils.convertTicksToInt(stop - entry, decimalPointPlace);

        LOG.info(format("Opening short position at: %.5f stop: %.5f target: %.5f ticks to target: %d ticks to stop: %d",
                entry, stop, target, ticksToTarget, ticksToStop));
        return new Position(entryDate, entry, target, stop);
    }

    private Position createLongPositionAtLows(UsefulTickData usefulTickData, double extraTicks, int decimalPointPlace) {
        entryDate = usefulTickData.getCandleDate();

        double entry = usefulTickData.getCandleClose();
        final double distanceToTarget = usefulTickData.getCandleClose() - usefulTickData.getCandleLow() + extraTicks;
        final int ticksToTarget = utils.convertTicksToInt(distanceToTarget, decimalPointPlace);
        double target = entry + distanceToTarget;
        double stop = usefulTickData.getCandleLow() - extraTicks;
        final int ticksToStop = utils.convertTicksToInt(entry - stop, decimalPointPlace);

        LOG.info(format("Opening long position at: %.5f stop: %.5f target: %.5f ticks to target: %d ticks to stop: %d",
                entry, stop, target, ticksToTarget, ticksToStop));

        return new Position(entryDate, entry, target, stop);
    }

    void setTimeToOpenPosition(boolean timeToOpenPosition) {
        this.timeToOpenPosition = timeToOpenPosition;
    }

    void managePosition(UsefulTickData usefulTickData, Position position, BufferedWriter dataWriter,
                        PositionStats stats, int decimalPointPlace) throws IOException {

        exitDate = usefulTickData.getCandleDate();
        if (isLongPosition(position)) {

            if (isLongStopTouched(usefulTickData, position)) {
                stats.incrementLongTrades();
                int profitLoss = utils.convertTicksToInt(position.getStop() - position.getEntry(), decimalPointPlace);
                closePosition(profitLoss, position.getStop(), position,
                        STOPPED_LONG_CSV_TEMPLATE, dataWriter, stats);

                stats.incrementLosers();
                stats.addLoser(usefulTickData.getCandleDate());

            } else if (isLongTargetExceeded(usefulTickData, position)) {
                stats.incrementLongTrades();
                final int profitLoss = utils.convertTicksToInt(position.getTarget() - position.getEntry(), decimalPointPlace);
                closePosition(profitLoss, position.getTarget(), position,
                        TARGET_LONG_CSV_TEMPLATE, dataWriter, stats);
                stats.incrementWinners();
                stats.addWinner(usefulTickData.getCandleDate());
            }
        } else {

            if (isShortStopTouched(usefulTickData, position)) {
                stats.incrementShortTrades();
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getStop(), decimalPointPlace);
                closePosition(profitLoss, position.getStop(), position,
                        STOPPED_SHORT_CSV_TEMPLATE, dataWriter, stats);
                stats.incrementLosers();
                stats.addLoser(usefulTickData.getCandleDate());
            } else if (isShortTargetExceeded(usefulTickData, position)) {
                stats.incrementShortTrades();
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getTarget(), decimalPointPlace);
                closePosition(profitLoss, position.getTarget(), position,
                        TARGET_SHORT_CSV_TEMPLATE, dataWriter, stats);
                stats.incrementWinners();
                stats.addWinner(usefulTickData.getCandleDate());
            }
        }
    }

    private boolean isShortTargetExceeded(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickLow() < position.getTarget();
    }

    private boolean isShortStopTouched(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickHigh() >= position.getStop();
    }

    private boolean isLongTargetExceeded(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickHigh() > position.getTarget();
    }

    private boolean isLongStopTouched(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickLow() <= position.getStop();
    }

    private boolean isLongPosition(Position position) {
        return position.getTarget() > position.getStop();
    }

    private void closePosition(int profitLoss, final double stopOrTarget, Position position, String
            csvTemplate, BufferedWriter dataWriter, PositionStats positionStats) throws IOException {

        positionStats.addToTickCounter(profitLoss);

        LOG.info(format("Closing Position from: %s to %s at %.5f for profit of %d", entryDate, exitDate, stopOrTarget,
                profitLoss));
        dataWriter.write(format(csvTemplate, entryDate, position.getEntry(), exitDate, stopOrTarget,
                profitLoss));

        position.close();
    }

    Results getResults(String outputFile, BufferedWriter dataWriter, PositionStats positionStats) throws IOException {
        dataWriter.close();
        return new Results(outputFile, positionStats);
    }
}
