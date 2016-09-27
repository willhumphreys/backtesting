package matcha;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class PositionExecutor {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private static final String STOPPED_LONG_CSV_TEMPLATE = "%s,long,%.5f,stopped,%s,%.5f,%d,%b,%b,%b,%b%n";
    private static final String TARGET_LONG_CSV_TEMPLATE = "%s,long,%.5f,target,%s,%.5f,%d,%b,%b,%b,%b%n";
    private static final String STOPPED_SHORT_CSV_TEMPLATE = "%s,short,%.5f,stopped,%s,%.5f,%d,%b,%b,%b,%b%n";
    private static final String TARGET_SHORT_CSV_TEMPLATE = "%s,short,%.5f,target,%s,%.5f,%d,%b,%b,%b,%b%n";

    private final Utils utils;

    PositionExecutor(Utils utils) {
        this.utils = utils;
    }

    Path createResultsDirectory(final Path outputDirectory) throws IOException {
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }
        return outputDirectory;
    }

    void managePosition(UsefulTickData usefulTickData, Position position, BufferedWriter dataWriter,
                        PositionStats stats, int decimalPointPlace) throws IOException {

        if (isLongPosition(position)) {

            if (isLongStopTouched(usefulTickData, position)) {
                stats.incrementLongTrades();
                int profitLoss = utils.convertTicksToInt(position.getStop().subtract(position.getEntry()), decimalPointPlace);
                closePosition(profitLoss, position.getStop(), position,
                        STOPPED_LONG_CSV_TEMPLATE, dataWriter, stats, usefulTickData);

                stats.incrementLosers();
                stats.addLoser(usefulTickData.getCandleDate());

            } else if (isLongTargetExceeded(usefulTickData, position)) {
                stats.incrementLongTrades();
                final int profitLoss = utils.convertTicksToInt(position.getTarget().subtract(position.getEntry()), decimalPointPlace);
                closePosition(profitLoss, position.getTarget(), position,
                        TARGET_LONG_CSV_TEMPLATE, dataWriter, stats, usefulTickData);
                stats.incrementWinners();
                stats.addWinner(usefulTickData.getCandleDate());
            }
        } else {

            if (isShortStopTouched(usefulTickData, position)) {
                stats.incrementShortTrades();
                final int profitLoss = utils.convertTicksToInt(position.getEntry().subtract(position.getStop()), decimalPointPlace);
                closePosition(profitLoss, position.getStop(), position,
                        STOPPED_SHORT_CSV_TEMPLATE, dataWriter, stats, usefulTickData);
                stats.incrementLosers();
                stats.addLoser(usefulTickData.getCandleDate());
            } else if (isShortTargetExceeded(usefulTickData, position)) {
                stats.incrementShortTrades();
                final int profitLoss = utils.convertTicksToInt(position.getEntry().subtract(position.getTarget()), decimalPointPlace);
                closePosition(profitLoss, position.getTarget(), position,
                        TARGET_SHORT_CSV_TEMPLATE, dataWriter, stats, usefulTickData);
                stats.incrementWinners();
                stats.addWinner(usefulTickData.getCandleDate());
            }
        }
    }

    private boolean isShortTargetExceeded(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickLow().compareTo(position.getTarget()) < 0;
    }

    private boolean isShortStopTouched(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickHigh().compareTo(position.getStop()) >= 0;
    }

    private boolean isLongTargetExceeded(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickHigh().compareTo(position.getTarget()) > 0;
    }

    private boolean isLongStopTouched(UsefulTickData usefulTickData, Position position) {
        return usefulTickData.getTickLow().compareTo(position.getStop()) <= 0;
    }

    private boolean isLongPosition(Position position) {
        return position.getTarget().compareTo(position.getStop()) > 0;
    }

    private void closePosition(int profitLoss, final BigDecimal stopOrTarget, Position position, String
            csvTemplate, BufferedWriter dataWriter, PositionStats positionStats, UsefulTickData usefulTickData) throws IOException {

        positionStats.addToTickCounter(profitLoss);

        final LocalDateTime exitDate = usefulTickData.getCandleDate();
        final boolean closeAboveMovingAverage = usefulTickData.isCloseAboveMovingAverage();
        final boolean closeBelowMovingAverage = usefulTickData.isCloseBelowMovingAverage();
        final boolean closeAboveTopBand = usefulTickData.isCloseAboveTopBand();
        final boolean closeBelowBottomBand = usefulTickData.isCloseBelowBottomBand();
        LOG.info(format("Closing Position from: %s to %s at %.5f for profit of %d", position.getEntryDate(), exitDate, stopOrTarget,
                profitLoss));
        dataWriter.write(format(csvTemplate, position.getEntryDate(), position.getEntry(), exitDate, stopOrTarget,
                profitLoss, closeAboveMovingAverage, closeBelowMovingAverage, closeAboveTopBand, closeBelowBottomBand));

        position.close();
    }

    Results getResults(String outputFile, BufferedWriter dataWriter, PositionStats positionStats, OpenOptions openOptions) throws IOException {
        dataWriter.close();
        return new Results(outputFile, positionStats, openOptions);
    }
}
