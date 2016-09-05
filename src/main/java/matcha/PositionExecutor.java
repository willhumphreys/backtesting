package matcha;


import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class PositionExecutor {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private static final String STOPPED_LONG_CSV_TEMPLATE = "%s,long,%.5f,stopped,%s,%.5f,%d,%d%n";
    private static final String TARGET_LONG_CSV_TEMPLATE = "%s,long,%.5f,target,%s,%.5f,%d,%d%n";
    private static final String STOPPED_SHORT_CSV_TEMPLATE = "%s,short,%.5f,stopped,%s,%.5f,%d,%d%n";
    private static final String TARGET_SHORT_CSV_TEMPLATE = "%s,short,%.5f,target,%s,%.5f,%d,%d%n";

    private final Utils utils;
    private final Signals signals;

    private LocalDateTime entryDate;
    private LocalDateTime exitDate;

    private boolean timeToOpenPosition;

    private boolean skipNextTrade;

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
                                      BackTestingParameters backTestingParameters) {

        //1.094295
        double extraTicks = backTestingParameters.getExtraTicks() / 100000.0;

        double targetMultiplier = backTestingParameters.getTargetMultiplier();

        if (signals.isShortSignal(usefulTickData, backTestingParameters.getHighLowCheckPref()) && timeToOpenPosition) {
            return Optional.of(createLongPositionAtLows(usefulTickData, extraTicks, targetMultiplier));
        }

        if (signals.isLongSignal(usefulTickData, backTestingParameters.getHighLowCheckPref()) && timeToOpenPosition) {
            return Optional.of(createShortPositionAtHighs(usefulTickData, extraTicks, targetMultiplier));
        }

        return Optional.empty();
    }

    private Position createShortPositionAtHighs(UsefulTickData usefulTickData, double extraTicks,
                                                double targetMultiplier) {
        entryDate = usefulTickData.getCandleDate();
        double entry = usefulTickData.getCandleClose();
        double target = usefulTickData.getCandleClose() - ((usefulTickData.getCandleHigh() - usefulTickData
                .getCandleClose() - extraTicks)) * targetMultiplier;
        double stop = usefulTickData.getCandleHigh() + extraTicks;

        LOG.info("Opening short position at " + entry + " stop " + stop + " target " + target);

        return new Position(entryDate, entry, target, stop);
    }

    private Position createLongPositionAtLows(UsefulTickData usefulTickData, double extraTicks,
                                              double targetMultiplier) {
        entryDate = usefulTickData.getCandleDate();

        double entry = usefulTickData.getCandleClose();
        double target = usefulTickData.getCandleClose() + ((usefulTickData.getCandleClose() - usefulTickData
                .getCandleLow() + extraTicks)) * targetMultiplier;
        double stop = usefulTickData.getCandleLow() - extraTicks;

        LOG.info("Opening long position at " + entry + " stop " + stop + " target " + target);

        return new Position(entryDate, entry, target, stop);
    }

    void setTimeToOpenPosition(boolean timeToOpenPosition) {
        this.timeToOpenPosition = timeToOpenPosition;
    }

    void managePosition(UsefulTickData usefulTickData, Position position, BufferedWriter dataWriter,
                        PositionStats stats, int decimalPointPlace) throws IOException {

        exitDate = usefulTickData.getCandleDate();
        if (isLongPosition(position)) {

            final int longUnderWater = utils.convertTicksToInt(position.getEntry() - usefulTickData.getTickLow(), decimalPointPlace);


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
            } else if (longUnderWater > position.getCouldOfBeenBetter() && longUnderWater > 0) {
                LOG.info("New Better entry: " + longUnderWater + " entry: " + position.getEntry() + " candle low: " + usefulTickData.getTickLow());
                position.setCouldOfBeenBetter(longUnderWater);
            }
        } else {


            final int shortUnderWater = utils.convertTicksToInt(usefulTickData.getTickHigh() - position.getEntry(), decimalPointPlace);

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
            } else if (shortUnderWater > position.getCouldOfBeenBetter() && shortUnderWater > 0) {
                position.setCouldOfBeenBetter(shortUnderWater);
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

        LOG.info("Closing Position from: " + entryDate + " to " + exitDate + " at " + stopOrTarget);
        dataWriter.write(String.format(csvTemplate, entryDate, position.getEntry(), exitDate, stopOrTarget,
                profitLoss, position.getCouldOfBeenBetter()));

        this.skipNextTrade = profitLoss > 0 && !skipNextTrade;

        position.close();
    }

    Results getResults(String outputFile, BufferedWriter dataWriter, PositionStats positionStats) throws IOException {
        dataWriter.close();
        return new Results(outputFile, positionStats);
    }
}
