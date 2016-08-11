package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final String fileHeader = "date,direction,entry,target_or_stop,exit_date,exit,ticks,cumulative_profit\n";

    private static final String STOPPED_LONG_CSV_TEMPLATE = "%s,long,%.5f,stopped,%s,%.5f,%d%n";
    private static final String TARGET_LONG_CSV_TEMPLATE = "%s,long,%.5f,target,%s,%.5f,%d%n";
    private static final String STOPPED_SHORT_CSV_TEMPLATE = "%s,short,%.5f,stopped,%s,%.5f,%d%n";
    private static final String TARGET_SHORT_CSV_TEMPLATE = "%s,short,%.5f,target,%s,%.5f,%d%n";

    private final Utils utils;
    private final Signals signals;

    private String entryDate;
    private String exitDate;

    private boolean availableToTrade;
    private boolean timeToOpenPosition;

    private boolean skipNextTrade;

    @Autowired
    public PositionExecutor(Signals signals, Utils utils) throws IOException {
        this.signals = signals;
        this.utils = utils;
        availableToTrade = true;
    }

    Path createResultsDirectory(final Path outputDirectory) throws IOException {

        if (!Files.exists(outputDirectory)) {
            Files.createDirectory(outputDirectory);
        }

        return outputDirectory;
    }

    Optional<Position> placePositions(UsefulTickData usefulTickData, int extraTicksCount, int highLowCheckPref,
                                      BackTestingParameters backTestingParameters) {

        //1.094295
        double extraTicks = extraTicksCount / 100000.0;

        if(backTestingParameters.isSkipIf4DownDays() && usefulTickData.isLast4DaysDown()) {
            return Optional.empty();
        }

        if(backTestingParameters.isSkipIf4UpDays() && usefulTickData.isLast4DaysUp()) {
            return Optional.empty();
        }

        if(backTestingParameters.isAfter4DaysOfPositiveCloses() && !usefulTickData.isLast4DaysUp()) {
            return Optional.empty();
        }

        if(backTestingParameters.isAfter4DaysOfNegativeCloses() && !usefulTickData.isLast4DaysDown()) {
            return Optional.empty();
        }

        if (signals.isShortSignal(usefulTickData, highLowCheckPref) && timeToOpenPosition && availableToTrade && !backTestingParameters.isHighsOnly()) {
            if(backTestingParameters.isFadeTheBreakout()) {
                return Optional.of(createLongPositionAtLows(usefulTickData, extraTicks));
            } else {
                return Optional.of(createShortPositionAtLows(usefulTickData, extraTicks));
            }



        }

        if (signals.isLongSignal(usefulTickData, highLowCheckPref) && timeToOpenPosition && availableToTrade && !backTestingParameters.isLowsOnly()) {
            if(backTestingParameters.isFadeTheBreakout()) {
                return Optional.of(createShortPositionAtHighs(usefulTickData, extraTicks));
            } else {
                return Optional.of(createLongPositionAtHighs(usefulTickData, extraTicks));
            }

        }

        return Optional.empty();
    }

    private Position createShortPositionAtHighs(UsefulTickData usefulTickData, double extraTicks) {
        entryDate = usefulTickData.getCandleDate();
        availableToTrade = false;
        double entry = usefulTickData.getCandleClose();
        double target = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData
                .getCandleClose() - extraTicks);
        double stop = usefulTickData.getCandleHigh() + extraTicks;
        return new Position(entryDate, entry, target, stop);
    }

    private Position createLongPositionAtLows(UsefulTickData usefulTickData, double extraTicks) {
        entryDate = usefulTickData.getCandleDate();
        availableToTrade = false;

        double entry = usefulTickData.getCandleClose();
        double target = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData
                .getCandleLow() + extraTicks);
        double stop = usefulTickData.getCandleLow() - extraTicks;

        return new Position(entryDate, entry, target, stop);
    }

    private Position createLongPositionAtHighs(UsefulTickData usefulTickData, double extraTicks) {
        double stop = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData
                .getCandleClose() - extraTicks);
        double target = usefulTickData.getCandleHigh() + extraTicks;
        double entry = usefulTickData.getCandleClose();
        entryDate = usefulTickData.getCandleDate();
        availableToTrade = false;
        return new Position(entryDate, entry, target, stop);
    }

    private Position createShortPositionAtLows(UsefulTickData usefulTickData, double extraTicks) {
        double stop = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData
                .getCandleLow() + extraTicks);
        double target = usefulTickData.getCandleLow() - extraTicks;
        double entry = usefulTickData.getCandleClose();
        entryDate = usefulTickData.getCandleDate();

        availableToTrade = false;

        return new Position(entryDate, entry, target, stop);
    }

    void setTimeToOpenPosition(boolean timeToOpenPosition) {
        this.timeToOpenPosition = timeToOpenPosition;
    }

    void managePosition(UsefulTickData usefulTickData, Position position, BufferedWriter dataWriter,
                        PositionStats stats, BackTestingParameters backTestingParameters) throws
            IOException {
        if (availableToTrade) {
            return;
        }

        exitDate = usefulTickData.getCandleDate();
        if (isLongPosition(position)) {

            if (isLongStopTouched(usefulTickData, position)) {
                int profitLoss = utils.convertTicksToInt(position.getStop() - position.getEntry());
                closePosition(profitLoss, STOPPED_LONG_TEMPLATE, position.getStop(), position,
                        STOPPED_LONG_CSV_TEMPLATE, dataWriter, stats, backTestingParameters);

                stats.incrementLosers();

            } else if (isLongTargetExceeded(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getTarget() - position.getEntry());
                closePosition(profitLoss, TARGET_LONG_TEMPLATE, position.getTarget(), position,
                        TARGET_LONG_CSV_TEMPLATE, dataWriter, stats, backTestingParameters);
                stats.incrementWinners();
            }
        } else {
            if (isShortStopTouched(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getStop());
                closePosition(profitLoss, STOPPED_SHORT_TEMPLATE, position.getStop(), position,
                        STOPPED_SHORT_CSV_TEMPLATE, dataWriter,stats, backTestingParameters);
                stats.incrementLosers();
            } else if (isShortTargetExceeded(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getTarget());
                closePosition(profitLoss, TARGET_SHORT_TEMPLATE, position.getTarget(), position,
                        TARGET_SHORT_CSV_TEMPLATE, dataWriter, stats, backTestingParameters);
                stats.incrementWinners();
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

    private void closePosition(int profitLoss, String template, final double stopOrTarget, Position position, String
            csvTemplate, BufferedWriter dataWriter, PositionStats positionStats, BackTestingParameters
            backTestingParameters) throws IOException {
        positionStats.addToTickCounter(profitLoss);
        availableToTrade = true;

        if(!skipNextTrade || !backTestingParameters.isSkipNextIfWinner()) {
            dataWriter.write(String.format(csvTemplate, entryDate, position.getEntry(), exitDate, stopOrTarget,
                    profitLoss));
        }


        this.skipNextTrade = profitLoss > 0 && !skipNextTrade;

        position.close();
    }

    Results getResults(String outputFile, BufferedWriter dataWriter, PositionStats positionStats) throws IOException {
        dataWriter.close();
        return new Results(outputFile, positionStats);
    }
}
