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
import static java.lang.invoke.MethodHandles.lookup;
import static java.nio.file.Files.newBufferedWriter;
import static org.slf4j.LoggerFactory.getLogger;

class Simulation {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private static final int DATE = 0;

    private PositionExecutor positionExecutor;

    private List<Position> positions;

    private final TickDataReader tickDataReader;

    private static final String fileHeader = "date,direction,entry,target_or_stop,exit_date,exit,ticks," +
            "could_of_been_better\n";


    public Simulation(PositionExecutor positionExecutor, TickDataReader tickDataReader) {
        this.positionExecutor = positionExecutor;
        this.tickDataReader = tickDataReader;
        this.positions = newArrayList();
    }

    Results execute(Inputs inputs, final Path outputDirectory, BackTestingParameters backTestingParameters,
                    DecimalPointPlace decimalPointPlace) throws
            IOException {

        LOG.info("Starting: " + backTestingParameters.getName() + " " + inputs.getFile1() + " " + inputs
                .getFile2());

        String[][] tickData;
        String[][] hourData;
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

        int hourCounter = 0;
        for (int i = 1; i < tickData.length; i++) {

            //If it the last tick skip trading.
            if (i == tickData.length - 1) {
                continue;
            }

            //Get tick hour and the hour hour.
            LocalDateTime hourDateTime = LocalDateTime.parse(hourData[hourCounter][DATE]);
            LocalDateTime tickDateTime = LocalDateTime.parse(tickData[i][DATE]);
            LocalDateTime nextTickDateTime = LocalDateTime.parse(tickData[i + 1][DATE]);


            int hourCandleHour = hourDateTime.getHour();
            final int tickCandleHour = tickDateTime.getHour();

            //If this is the last tick then it is time to open our positionOptional if we have one.
            if (tickDateTime.getHour() != nextTickDateTime.getHour()) {
                positionExecutor.setTimeToOpenPosition(true);
            }

            //If the hour has changed we need to update the hour counter.
            if (tickCandleHour != hourCandleHour) {
                hourCounter++;


                final LocalDateTime currentHourTime = LocalDateTime.parse(hourData[hourCounter][DATE]);
                final LocalDateTime currentMinuteTime = LocalDateTime.parse(tickData[i][DATE]);


                if (currentHourTime.getHour() != currentMinuteTime.getHour()) {
                    hourCounter++;
                }

                final LocalDateTime currentHourTime2 = LocalDateTime.parse(hourData[hourCounter][DATE]);
                final LocalDateTime currentMinuteTime2 = LocalDateTime.parse(tickData[i][DATE]);

                if (currentHourTime2.getHour() != currentMinuteTime2.getHour()) {
                    throw new IllegalStateException("minutes not matching hours.");
                }
            }


            if (hourCounter != 0) {

                UsefulTickData usefulTickData = new UsefulTickData(hourData, hourCounter, tickData, i).invoke();

                if (!positions.isEmpty() && positions.get(0).isFilled()) {
                    final Position position = positions.get(0);
                    positionExecutor.managePosition(usefulTickData, position, dataWriter, positionStats,
                            backTestingParameters, decimalPointPlace);
                    if (position.isClosed()) {
                        positions.clear();
                    }
                } else {
                    final Optional<Position> positionOptional = positionExecutor.placePositions(usefulTickData, backTestingParameters
                                    .getExtraTicks(), backTestingParameters.getHighLowCheckPref(),
                            backTestingParameters,
                            positionStats);



                    if(positionOptional.isPresent()) {
                        final Position position = positionOptional.get();
                        positions.add(position);
                        position.fill();
                    }

                }
            }

            positionExecutor.setTimeToOpenPosition(false);


        }

//        for (String[] line : hourData) {
//            LOG.info(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] +
//                    line[DAILY_HIGH]);
//        }

        return positionExecutor.getResults(getOutputFile(inputs, backTestingParameters.getName()), dataWriter,
                positionStats);
    }

    private String getOutputFile(Inputs inputs, String executionName) {
        final Path file2 = inputs.getFile2();
        final String fileName = file2.getName(file2.getNameCount() - 1).toString().split("\\.")[0].split("_")[0] + "_" + executionName +
                ".csv";
        LOG.info(fileName);
        return fileName;
    }
}
