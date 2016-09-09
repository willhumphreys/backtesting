package matcha;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
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

    private final PositionExecutor positionExecutor;
    private final List<Position> positions;
    private final TickDataReader tickDataReader;
    private final SyncTicks syncTicks;

    private static final String fileHeader = "date,direction,entry,target_or_stop,exit_date,exit,ticks\n";

    Simulation(PositionExecutor positionExecutor, TickDataReader tickDataReader, SyncTicks syncTicks) {
        this.positionExecutor = positionExecutor;
        this.tickDataReader = tickDataReader;
        this.syncTicks = syncTicks;
        this.positions = newArrayList();
    }

    Results execute(Inputs inputs, final Path outputDirectory, BackTestingParameters backTestingParameters,
                    int decimalPointPlace) throws IOException {

        LOG.info(format("Starting: %s %s %s", backTestingParameters.getName(),
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
                .resolve(getOutputFile(inputs, backTestingParameters.getName())));
        dataWriter.write(fileHeader);

        final PositionStats positionStats = new PositionStats();

        LOG.info("All data loaded");
        int hourCounter = 0;
        for (int tickCounter = 1; tickCounter < tickData.size(); tickCounter++) {

            //If it the last tick skip trading.
            if (tickCounter == tickData.size() - 1) {
                continue;
            }

//            if(hourData[hourCounter][DATE].contains("2009-10-20")) {
//                System.out.println("here we are");
//            }

            //Get tick hour and the hour hour.
            LocalDateTime hourDateTime = LocalDateTime.parse(hourData.get(hourCounter).getDateTime());
            LocalDateTime tickDateTime = LocalDateTime.parse(tickData.get(tickCounter).getDateTime());
            LocalDateTime nextTickDateTime = LocalDateTime.parse(tickData.get(tickCounter + 1).getDateTime());


            int hourCandleHour = hourDateTime.getHour();
            final int tickCandleHour = tickDateTime.getHour();

            if (isNextTickANewHour(tickDateTime, nextTickDateTime)) {
                positionExecutor.setTimeToOpenPosition(true);
            }
            hourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(tickData, hourData, hourCounter, tickCounter,
                    hourCandleHour, tickCandleHour);

            if (hourCounter == 0) {
                continue;
            }

            UsefulTickData usefulTickData = new UsefulTickData(hourData, hourCounter, tickData, tickCounter);

            if (!positions.isEmpty()) {
                final Position position = positions.get(0);
                positionExecutor.managePosition(usefulTickData, position, dataWriter, positionStats,
                        decimalPointPlace);

                if (position.isClosed()) {
                    positions.clear();
                }
            } else {
                final Optional<Position> positionOptional = positionExecutor.placePositions(usefulTickData,
                        backTestingParameters, decimalPointPlace);

                if (positionOptional.isPresent()) {
                    final Position position = positionOptional.get();
                    positions.add(position);
                }
            }
            positionExecutor.setTimeToOpenPosition(false);
        }
        return positionExecutor.getResults(getOutputFile(inputs, backTestingParameters.getName()), dataWriter,
                positionStats);
    }

    private boolean isNextTickANewHour(LocalDateTime tickDateTime, LocalDateTime nextTickDateTime) {
        return tickDateTime.getHour() != nextTickDateTime.getHour();
    }

    private String getOutputFile(Inputs inputs, String executionName) {
        final Path file2 = inputs.getFile2();
        final String fileName = format("%s_%s.csv",
                file2.getName(file2.getNameCount() - 1).toString().split("\\.")[0].split("_")[0], executionName);
        LOG.info(fileName);
        return fileName;
    }
}
