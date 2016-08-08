package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
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

    Results execute(Inputs inputs, final Path outputDirectory, BackTestingParameters backTestingParameters) throws IOException {

        String[][] tickData = tickDataReader.read(inputs.getFile1());
        String[][] hourData = tickDataReader.read(inputs.getFile2());

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
            }


            if (hourCounter != 0) {

                UsefulTickData usefulTickData = new UsefulTickData(hourData, hourCounter).invoke();

                if (positionOptional.isPresent()) {
                    final Position position = positionOptional.get();
                    positionExecutor.managePosition(usefulTickData, position, dataWriter, positionStats, backTestingParameters);
                    if (position.isClosed()) {
                        this.positionOptional = Optional.empty();
                    }
                } else {
                    this.positionOptional = positionExecutor.placePositions(usefulTickData, backTestingParameters.getExtraTicks());
                }
            }

            positionExecutor.setTimeToOpenPosition(false);


        }

//        for (String[] line : hourData) {
//            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] +
//                    line[DAILY_HIGH]);
//        }

        return positionExecutor.getResults(getOutputFile(inputs, backTestingParameters.getName()), dataWriter, positionStats);
    }

    private String getOutputFile(Inputs inputs, String executionName) {
        final Path file2 = inputs.getFile2();
        return file2.getName(file2.getNameCount() - 1).toString().split("\\.")[0] + executionName + "Out.csv";
    }

}
