package matcha;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static java.nio.file.Files.newBufferedWriter;
import static org.slf4j.LoggerFactory.getLogger;

class Simulation {

    private static final Logger LOG = getLogger(lookup().lookupClass());
    private static final String fileHeader = "date,direction,entry,target_or_stop,exit_date,exit,ticks\n";

    private final PositionExecutor positionExecutor;
    private final List<Position> positions;
    private final TickDataReader tickDataReader;
    private final SyncTicks syncTicks;
    private LocalDate lastTradeDate;
    private boolean timeToOpenPosition;
    private final PositionPlacer positionPlacer;
    private final TickDataFactory tickDataFactory;

    Simulation(PositionExecutor positionExecutor, TickDataReader tickDataReader, SyncTicks syncTicks,
               PositionPlacer positionPlacer, TickDataFactory tickDataFactory) {
        this.positionExecutor = positionExecutor;
        this.tickDataReader = tickDataReader;
        this.syncTicks = syncTicks;
        this.positionPlacer = positionPlacer;
        this.tickDataFactory = tickDataFactory;
        this.positions = newArrayList();

        lastTradeDate = LocalDate.of(1900, 1, 1);
    }

    Results execute(Inputs inputs, final Path outputDirectory,
                    int decimalPointPlace) throws IOException {

        LOG.info(format("Starting: %s %s",
                inputs.getFile1(), inputs.getFile2()));

        List<DataRecord> tickData;
        List<DataRecord> hourData;
        try {
            tickData = tickDataReader.read(inputs.getFile1());
            hourData = tickDataReader.read(inputs.getFile2());
        } catch (IOException | DateTimeParseException e) {
            throw new IOException("Failed to parse " + inputs.getFile2(), e);
        }

        BufferedWriter dataWriter = newBufferedWriter(positionExecutor.createResultsDirectory(outputDirectory)
                .resolve(getOutputFile(inputs)));
        dataWriter.write(fileHeader);

        final PositionStats positionStats = new PositionStats();

        LOG.info("All data loaded");
        int hourCounter = 0;
        for (int tickCounter = 0; tickCounter < tickData.size(); tickCounter++) {

            //If it the last tick skip trading.
            if (tickCounter == tickData.size() - 1) {
                continue;
            }

//            if(hourData[hourCounter][DATE].contains("2009-10-20")) {
//                System.out.println("here we are");
//            }

            //Get tick hour and the hour hour.
            LocalDateTime hourDateTime = hourData.get(hourCounter).getDateTime();
            LocalDateTime tickDateTime = tickData.get(tickCounter).getDateTime();
            LocalDateTime nextTickDateTime = tickData.get(tickCounter + 1).getDateTime();


            int hourCandleHour = hourDateTime.getHour();
            final int tickCandleHour = tickDateTime.getHour();

            if (isNextTickANewHour(tickDateTime, nextTickDateTime)) {
                this.timeToOpenPosition = true;
            }
            hourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(tickData, hourData, hourCounter, tickCounter,
                    hourCandleHour, tickCandleHour);

            if (hourCounter == 0) {
                continue;
            }

            final UsefulTickData usefulTickData = tickDataFactory.buildTickData(hourData, hourCounter,
                    tickData, tickCounter);

            if (!positions.isEmpty()) {
                final Position position = positions.get(0);
                positionExecutor.managePosition(usefulTickData, position, dataWriter, positionStats,
                        decimalPointPlace);

                if (position.isClosed()) {
                    positions.clear();
                }
            } else {
                if (timeToOpenPosition) {
                    final Optional<Position> positionOptional = positionPlacer.placePositions(usefulTickData,
                            decimalPointPlace);

                    if (positionOptional.isPresent()) {
                        final Position position = positionOptional.get();
                        final LocalDate tradeDate = position.getEntryDate().toLocalDate();
                        if (lastTradeDate.equals(tradeDate)) {
                            LOG.info("Trying to trade twice " + tradeDate);
                        } else {
                            lastTradeDate = tradeDate;
                            positions.add(position);
                        }
                    }
                }
            }
            this.timeToOpenPosition = false;
        }
        return positionExecutor.getResults(getOutputFile(inputs), dataWriter,
                positionStats);
    }

    private boolean isNextTickANewHour(LocalDateTime tickDateTime, LocalDateTime nextTickDateTime) {
        return tickDateTime.getHour() != nextTickDateTime.getHour();
    }

    private String getOutputFile(Inputs inputs) {
        final Path file2 = inputs.getFile2();
        final String fileName = format("%s.csv",
                file2.getName(file2.getNameCount() - 1).toString().split("\\.")[0].split("_")[0]);
        LOG.info(fileName);
        return fileName;
    }
}
