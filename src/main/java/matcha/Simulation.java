package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class Simulation {

    private static final int DATE = 0;

    private DateTimeFormatter formatter;

    private PositionExecutor positionExecutor;

    private Optional<Position> positionOptional;

    private final TickDataReader tickDataReader;

    @Autowired
    public Simulation(PositionExecutor positionExecutor, TickDataReader tickDataReader) {
        this.positionExecutor = positionExecutor;
        this.tickDataReader = tickDataReader;
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
        this.positionOptional = Optional.empty();
    }

    Results execute(Inputs inputs, int extraTicks) throws IOException {

        String[][] hourData = tickDataReader.read(inputs.getFile1());
        String[][] tickData = tickDataReader.read(inputs.getFile2());

        final Path file2 = inputs.getFile2();
        String outputFile = file2.getName(file2.getNameCount() -1).toString().split("\\.")[0] + "Out.csv";

        positionExecutor.createResultsFile(outputFile);

        int hourCounter = 0;
        for (int i = 1; i < tickData.length; i++) {

            //If it the last tick skip trading.
            if (i == tickData.length - 1) {
                continue;
            }

            //Get tick hour and the hour hour.
            LocalDateTime hourDateTime = LocalDateTime.parse(hourData[hourCounter][DATE], formatter);
            LocalDateTime tickDateTime = LocalDateTime.parse(tickData[i][DATE], formatter);
            LocalDateTime nextTickDateTime = LocalDateTime.parse(tickData[i + 1][DATE], formatter);

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

                if(positionOptional.isPresent()) {
                    final Position position = positionOptional.get();
                    positionExecutor.managePosition(usefulTickData, position);
                    if(position.isClosed()) {
                        this.positionOptional = Optional.empty();
                    }
                } else {
                    this.positionOptional = positionExecutor.placePositions(usefulTickData, extraTicks);
                }
            }

            positionExecutor.setTimeToOpenPosition(false);

        }

//        for (String[] line : hourData) {
//            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] +
//                    line[DAILY_HIGH]);
//        }

        return positionExecutor.getResults();
    }

}
