package matcha;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@SpringBootApplication
public class BacktestingApplication implements CommandLineRunner {

    private static final String DATA_DIRECTORY = "copied-data";
    private static final String FILES_TO_EXECUTE_LIST = "inputFileList.csv";
    private static final int EXTRA_TICKS = 10;

    private final Simulation simulation;
    private Map<String,BackTestingParameters> parametersMap;

    @Autowired
    public BacktestingApplication(Simulation simulation) {
        this.simulation = simulation;
    }

    public static void main(String[] args) {
        SpringApplication.run(BacktestingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        this.parametersMap = createParametersMap(EXTRA_TICKS);

        Path dataDirectory = Paths.get(DATA_DIRECTORY);

        final List<String> inputLines = Files.readAllLines(Paths.get(FILES_TO_EXECUTE_LIST));

        final String backTestingParametersName = args[0];
        final BackTestingParameters backTestingParameters = parametersMap.get(backTestingParametersName);

        if(backTestingParameters == null) {
            throw new IllegalArgumentException("Couldn't find the backTestingParameters for " + backTestingParametersName);
        }

        for (String inputLine : inputLines) {
            if(inputLine.trim().length() == 0 ) {
                continue;
            }
            String[] lineParts = inputLine.split(",");
            final Path oneMinutePath = dataDirectory.resolve(lineParts[0]);
            final Path sixtyMinutePath = dataDirectory.resolve(lineParts[1]);
            Inputs input = new Inputs(oneMinutePath, sixtyMinutePath);

            final Results results = simulation.execute(input, Paths.get("results"), backTestingParameters);

            System.out.println(results);
        }
    }

    private Map<String, BackTestingParameters> createParametersMap(int extraTicks) {
        Map<String, BackTestingParameters> parametersMap = newHashMap();
        parametersMap.put("Normal", new BackTestingParameters.Builder()
                .setName("Normal")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .createBackTestingParameters());
        parametersMap.put("NormalLowsOnly", new BackTestingParameters.Builder()
                .setName("NormalLowsOnly")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .setLowsOnly()
                .createBackTestingParameters());

        parametersMap.put("NormalLowsOnlySkipIf4NegativeCloses", new BackTestingParameters.Builder()
                .setName("NormalLowsOnlySkipIf4NegativeCloses")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .skipIf4DownDays()
                .setLowsOnly()
                .createBackTestingParameters());

        parametersMap.put("NormalHighsOnlySkipIf4PositiveCloses", new BackTestingParameters.Builder()
                .setName("NormalHighsOnlySkipIf4PositiveCloses")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .skipIf4UpDays()
                .setHighsOnly()
                .createBackTestingParameters());

        parametersMap.put("NormalHighsOnly", new BackTestingParameters.Builder()
                .setName("NormalHighsOnly")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .setHighsOnly()
                .createBackTestingParameters());
        parametersMap.put("NoNewLow", new BackTestingParameters.Builder()
                .setName("NoNewLow")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(2)
                .createBackTestingParameters());
        parametersMap.put("IfWinnerSkipNext", new BackTestingParameters.Builder().
                setName("IfWinnerSkipNext")
                .setExtraTicks(extraTicks)
                .skipNextIfWinner()
                .setHighLowCheckPref(0)
                .createBackTestingParameters());
        parametersMap.put("NewDayLow", new BackTestingParameters.Builder().
                setName("NewDayLow")
                .setExtraTicks(extraTicks)
                .skipNextIfWinner()
                .setHighLowCheckPref(1)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormal", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormal")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithEdge", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithEdge")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withEdge(0.15)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge()
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalLowsOnly", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalLowsOnly")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .setLowsOnly()
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalHighsOnly", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalHighsOnly")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .setHighsOnly()
                .fadeTheBreakout()
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalLowOnlyAfter4DaysOfLows", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalLowOnlyAfter4DaysOfLows")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .setLowsOnly()
                .onlyAfter4DaysOfNegativeCloses()
                .fadeTheBreakout()
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalHighOnlyAfter4DaysOfHighs", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalHighOnlyAfter4DaysOfHighs")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .setLowsOnly()
                .onlyAfter4DaysOfPositiveCloses()
                .fadeTheBreakout()
                .createBackTestingParameters());
        return parametersMap;
    }
}
