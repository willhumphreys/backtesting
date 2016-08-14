package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static java.nio.file.Files.newBufferedWriter;

@Service
class Simulation {

    private static final int DATE = 0;

    private PositionExecutor positionExecutor;

    private Optional<Position> positionOptional;

    private final TickDataReader tickDataReader;

    private static final String fileHeader = "date,direction,entry,target_or_stop,exit_date,exit,ticks\n";


    @Autowired
    public Simulation(PositionExecutor positionExecutor, TickDataReader tickDataReader) {
        this.positionExecutor = positionExecutor;
        this.tickDataReader = tickDataReader;
        this.positionOptional = Optional.empty();
    }

    Results execute(Inputs inputs, final Path outputDirectory, BackTestingParameters backTestingParameters,
                    DecimalPointPlace decimalPointPlace) throws
            IOException {

        System.out.println("Starting: " + backTestingParameters.getName() + " " + inputs.getFile1() + " " + inputs
                .getFile2());

        String[][] tickData;
        try {
            tickData = tickDataReader.read(inputs.getFile1());
        } catch (IOException e) {
            System.out.println("Failed to parse " + inputs.getFile1());
            throw new IOException("Failed to parse " + inputs.getFile1(), e);
        } catch (DateTimeParseException e2) {
            System.out.println("Failed to parse " + inputs.getFile1());
            throw e2;
        }
        String[][] hourData;
        try {
            hourData = tickDataReader.read(inputs.getFile2());
        } catch (IOException e) {
            throw new IOException("Failed to parse " + inputs.getFile2(), e);
        } catch (DateTimeParseException e2) {
            System.out.println("Failed to parse " + inputs.getFile2());
            throw e2;
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


            if(tickDateTime.isAfter(LocalDateTime.of(2016, 7, 28, 0, 0, 0)) &&
                    tickDateTime.isBefore(LocalDateTime.of(2016,7,29,0,0,0))) {

              //  System.out.println("Check here");
            }

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

                UsefulTickData usefulTickData = new UsefulTickData(hourData, hourCounter).invoke();

                if (positionOptional.isPresent()) {
                    final Position position = positionOptional.get();
                    positionExecutor.managePosition(usefulTickData, position, dataWriter, positionStats,
                            backTestingParameters, decimalPointPlace);
                    if (position.isClosed()) {
                        this.positionOptional = Optional.empty();
                    }
                } else {
                    this.positionOptional = positionExecutor.placePositions(usefulTickData, backTestingParameters
                            .getExtraTicks(), backTestingParameters.getHighLowCheckPref(), backTestingParameters,
                            positionStats);
                }
            }

            positionExecutor.setTimeToOpenPosition(false);


        }

//        for (String[] line : hourData) {
//            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] +
//                    line[DAILY_HIGH]);
//        }

        return positionExecutor.getResults(getOutputFile(inputs, backTestingParameters.getName()), dataWriter,
                positionStats);
    }

    private String getOutputFile(Inputs inputs, String executionName) {
        final Path file2 = inputs.getFile2();
        final String fileName = file2.getName(file2.getNameCount() - 1).toString().split("\\.")[0] + executionName +
                ".csv";
        System.out.println(fileName);
        return fileName;
    }
}
