package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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

    private static final String STOPPED_LONG_CSV_TEMPLATE = "%s,long,%.5f,stopped,%s,%.5f,%d,%d%n";
    private static final String TARGET_LONG_CSV_TEMPLATE = "%s,long,%.5f,target,%s,%.5f,%d,%d%n";
    private static final String STOPPED_SHORT_CSV_TEMPLATE = "%s,short,%.5f,stopped,%s,%.5f,%d,%d%n";
    private static final String TARGET_SHORT_CSV_TEMPLATE = "%s,short,%.5f,target,%s,%.5f,%d,%d%n";

    private final Utils utils;
    private final Signals signals;

    private LocalDateTime entryDate;
    private LocalDateTime exitDate;

    private boolean availableToTrade;
    private boolean timeToOpenPosition;

    private boolean skipNextTrade;

    private boolean haveEdge;

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
                                      BackTestingParameters backTestingParameters, PositionStats positionStats) {

        //1.094295
        double extraTicks = extraTicksCount / 100000.0;

        if (backTestingParameters.isSkipIf4DownDays() && usefulTickData.isLast4DaysDown()) {
            return Optional.empty();
        }

        if (backTestingParameters.isSkipIf4UpDays() && usefulTickData.isLast4DaysUp()) {
            return Optional.empty();
        }

        if (backTestingParameters.isAfter4DaysOfPositiveCloses() && !usefulTickData.isLast4DaysUp()) {
            return Optional.empty();
        }

        if (backTestingParameters.isAfter4DaysOfNegativeCloses() && !usefulTickData.isLast4DaysDown()) {
            return Optional.empty();
        }

        boolean haveEdge = configureInitialEdgeValue(backTestingParameters);

        if(!haveEdge) {
            haveEdge = toggleEdges(backTestingParameters, positionStats);
        }

        double targetMultiplier = backTestingParameters.getTargetMultiplier();

        if (signals.isShortSignal(usefulTickData, highLowCheckPref) && timeToOpenPosition &&
                !backTestingParameters.isHighsOnly()) {



                if (backTestingParameters.isFadeTheBreakout()) {
                    return Optional.of(createLongPositionAtLows(usefulTickData, extraTicks, haveEdge, targetMultiplier));

                } else {
                    return Optional.of(createShortPositionAtLows(usefulTickData, extraTicks, haveEdge));
                }




        }

        if (signals.isLongSignal(usefulTickData, highLowCheckPref) && timeToOpenPosition  &&
                !backTestingParameters.isLowsOnly()) {
                if (backTestingParameters.isFadeTheBreakout()) {
                    return Optional.of(createShortPositionAtHighs(usefulTickData, extraTicks, haveEdge, targetMultiplier));

                } else {
                    return Optional.of(createLongPositionAtHighs(usefulTickData, extraTicks, haveEdge));
                }
        }

        return Optional.empty();
    }

    private boolean toggleEdges(BackTestingParameters backTestingParameters,
                                PositionStats positionStats) {
        boolean haveEdge = false;
        if (backTestingParameters.isWithEdge()) {
            haveEdge = toggleEdgeWithTimeBasedSMA(backTestingParameters, positionStats);
        } else if (backTestingParameters.isWithTradeCountEdge()) {
            haveEdge = toggleEdgeWithTradeCountSMA(backTestingParameters, positionStats);
        }
        return haveEdge;
    }

    private boolean toggleEdgeWithTradeCountSMA(BackTestingParameters backTestingParameters, PositionStats
            positionStats) {
        //boolean haveEdge = false;
        final double tradeCountSma30 = positionStats.getTradeCountSma(backTestingParameters
                .getMovingAverageTradeCount()).getMovingAverage();

        if(tradeCountSma30 == PositionStats.NOT_ENOUGH_DATA_FOR_EDGE) {
            haveEdge = false;
        } else if (tradeCountSma30 < backTestingParameters.getEdgeLevelCount() * -1) {
            haveEdge = true;
        } else if (tradeCountSma30 > backTestingParameters.getEdgeLevelCount() +
                backTestingParameters.getEdgeStopLevelCount()) {
            haveEdge = false;
        }
        return haveEdge;
    }

    private boolean toggleEdgeWithTimeBasedSMA(BackTestingParameters backTestingParameters, PositionStats
            positionStats) {
        final double sma30 = positionStats.getSma30(backTestingParameters.getMovingAverageDayCount());
        //boolean haveEdge = false;
        if(sma30 == PositionStats.NOT_ENOUGH_DATA_FOR_EDGE) {
            haveEdge = false;
        } else if (sma30 < backTestingParameters.getEdgeLevel() * -1) {
            haveEdge = true;
        } else if (sma30 > backTestingParameters.getEdgeLevel()) {
            haveEdge = false;
        }
        return haveEdge;
    }

    private boolean configureInitialEdgeValue(BackTestingParameters backTestingParameters) {
        boolean haveEdge = false;

        if(!backTestingParameters.isWithEdge() && !backTestingParameters.isWithTradeCountEdge()) {
            haveEdge = true;
        }
        return haveEdge;
    }

    private Position createShortPositionAtHighs(UsefulTickData usefulTickData, double extraTicks, boolean haveEdge,
                                                double targetMultiplier) {
        entryDate = usefulTickData.getCandleDate();
        availableToTrade = false;
        double entry = usefulTickData.getCandleClose();
        double target = usefulTickData.getCandleClose() - ((usefulTickData.getCandleHigh() - usefulTickData
                .getCandleClose() - extraTicks)) * targetMultiplier;
        double stop = usefulTickData.getCandleHigh() + extraTicks;
        return new Position(entryDate, entry, target, stop, haveEdge);
    }

    private Position createLongPositionAtLows(UsefulTickData usefulTickData, double extraTicks, boolean haveEdge,
                                              double targetMultiplier) {
        entryDate = usefulTickData.getCandleDate();
        availableToTrade = false;

        double entry = usefulTickData.getCandleClose();
        double target = usefulTickData.getCandleClose() + ((usefulTickData.getCandleClose() - usefulTickData
                .getCandleLow() + extraTicks)) * targetMultiplier;
        double stop = usefulTickData.getCandleLow() - extraTicks;

        return new Position(entryDate, entry, target, stop, haveEdge);
    }

    private Position createLongPositionAtHighs(UsefulTickData usefulTickData, double extraTicks, boolean haveEdge) {
        double stop = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData
                .getCandleClose() - extraTicks);
        double target = usefulTickData.getCandleHigh() + extraTicks;
        double entry = usefulTickData.getCandleClose();
        entryDate = usefulTickData.getCandleDate();
        availableToTrade = false;
        return new Position(entryDate, entry, target, stop, haveEdge);
    }

    private Position createShortPositionAtLows(UsefulTickData usefulTickData, double extraTicks, boolean haveEdge) {
        double stop = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData
                .getCandleLow() + extraTicks);
        double target = usefulTickData.getCandleLow() - extraTicks;
        double entry = usefulTickData.getCandleClose();
        entryDate = usefulTickData.getCandleDate();

        availableToTrade = false;

        return new Position(entryDate, entry, target, stop, haveEdge);
    }

    void setTimeToOpenPosition(boolean timeToOpenPosition) {
        this.timeToOpenPosition = timeToOpenPosition;
    }

    void managePosition(UsefulTickData usefulTickData, Position position, BufferedWriter dataWriter,
                        PositionStats stats, BackTestingParameters backTestingParameters, DecimalPointPlace
                                decimalPointPlace) throws
            IOException {

        stats.cleanLists(usefulTickData.getCandleDate(),
                backTestingParameters.getMovingAverageDayCount(),
                backTestingParameters.getMovingAverageTradeCount()
        );

        if (availableToTrade) {
            return;
        }

        exitDate = usefulTickData.getCandleDate();
        if (isLongPosition(position)) {

            if (isLongStopTouched(usefulTickData, position)) {
                int profitLoss = utils.convertTicksToInt(position.getStop() - position.getEntry(), decimalPointPlace);
                closePosition(profitLoss, STOPPED_LONG_TEMPLATE, position.getStop(), position,
                        STOPPED_LONG_CSV_TEMPLATE, dataWriter, stats, backTestingParameters);

                stats.incrementLosers();

                stats.addLoser(usefulTickData.getCandleDate());

            } else if (isLongTargetExceeded(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getTarget() - position.getEntry(), decimalPointPlace);
                closePosition(profitLoss, TARGET_LONG_TEMPLATE, position.getTarget(), position,
                        TARGET_LONG_CSV_TEMPLATE, dataWriter, stats, backTestingParameters);
                stats.incrementWinners();
                stats.addWinner(usefulTickData.getCandleDate());
            } else {
                final int longUnderWater = utils.convertTicksToInt(position.getEntry() - usefulTickData.getCandleLow(), decimalPointPlace);
                if(longUnderWater > position.getCouldOfBeenBetter() && longUnderWater > 0) {
                    position.setCouldOfBeenBetter(longUnderWater);
                }
            }
        } else {
            if (isShortStopTouched(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getStop(), decimalPointPlace);
                closePosition(profitLoss, STOPPED_SHORT_TEMPLATE, position.getStop(), position,
                        STOPPED_SHORT_CSV_TEMPLATE, dataWriter, stats, backTestingParameters);
                stats.incrementLosers();
                stats.addLoser(usefulTickData.getCandleDate());
            } else if (isShortTargetExceeded(usefulTickData, position)) {
                final int profitLoss = utils.convertTicksToInt(position.getEntry() - position.getTarget(), decimalPointPlace);
                closePosition(profitLoss, TARGET_SHORT_TEMPLATE, position.getTarget(), position,
                        TARGET_SHORT_CSV_TEMPLATE, dataWriter, stats, backTestingParameters);
                stats.incrementWinners();
                stats.addWinner(usefulTickData.getCandleDate());
            } else {
                final int shortUnderWater = utils.convertTicksToInt(usefulTickData.getCandleHigh() - position.getEntry(), decimalPointPlace);
                if(shortUnderWater > position.getCouldOfBeenBetter() && shortUnderWater > 0) {
                    position.setCouldOfBeenBetter(shortUnderWater);
                }
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
        if (position.isHaveEdge()) {
            positionStats.addToTickCounter(profitLoss);
            if (!skipNextTrade || !backTestingParameters.isSkipNextIfWinner()) {
                System.out.println("Closing Position from: " + entryDate + " to " + exitDate);
                dataWriter.write(String.format(csvTemplate, entryDate, position.getEntry(), exitDate, stopOrTarget,
                        profitLoss, position.getCouldOfBeenBetter()));
            }

        }
        availableToTrade = true;


        this.skipNextTrade = profitLoss > 0 && !skipNextTrade;

        position.close();
    }

    Results getResults(String outputFile, BufferedWriter dataWriter, PositionStats positionStats) throws IOException {
        dataWriter.close();
        return new Results(outputFile, positionStats);
    }

}
