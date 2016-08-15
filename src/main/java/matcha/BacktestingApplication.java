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
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.cartesianProduct;
import static com.google.common.collect.Sets.newLinkedHashSet;

@SpringBootApplication
public class BacktestingApplication implements CommandLineRunner {

    private static final String DATA_DIRECTORY = "copied-data";
    private static final String FILES_TO_EXECUTE_LIST = "inputFileList.csv";
    private static final int EXTRA_TICKS = 10;

    private final Simulation simulation;
    private Map<String, BackTestingParameters> parametersMap;

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

        List<BackTestingParameters> backTestingParametersList = newArrayList();

        if (backTestingParametersName.equals("allFadeCounts")) {
            final Set<Double> smas = newLinkedHashSet(newArrayList(10.0, 20.0, 30.0, 40.0));
            final Set<Double> levels = newLinkedHashSet(newArrayList(0.1, 0.2, 0.3, 0.4, 0.5));
            final Set<Double> stopEdgeModifiers = newLinkedHashSet(newArrayList(-0.2, -0.1, 0.0, 0.1, 0.2));

            final Set<List<Double>> smaLevelCombinations = cartesianProduct(smas, levels, stopEdgeModifiers);


            for (List<Double> smaLevelCombination : smaLevelCombinations) {
                final Double sma = smaLevelCombination.get(0);
                final Double level = smaLevelCombination.get(1);
                final Double stopEdgeModifier = smaLevelCombination.get(2);

                if (Math.abs(stopEdgeModifier) >= level) {
                    continue;
                }

                final BackTestingParameters backTestingParameters = new BackTestingParameters.Builder()
                        .setName("FadeTheBreakoutNormalWithTradeCountEdgeSEM-level:" + level + "_sma:" + sma +
                                "_stopEdgeModifier:" + stopEdgeModifier)
                        .setExtraTicks(EXTRA_TICKS)
                        .setHighLowCheckPref(0)
                        .fadeTheBreakout()
                        .withTradeCountEdge(level, sma.intValue())
                        .withEdgeStopLevelCount(stopEdgeModifier)
                        .withTargetMultiplier(2)
                        .createBackTestingParameters();

                backTestingParametersList.add(backTestingParameters);
            }


        } else {
            final BackTestingParameters backTestingParameters = parametersMap.get(backTestingParametersName);
            backTestingParametersList.add(backTestingParameters);
        }

        if (backTestingParametersList.isEmpty()) {
            throw new IllegalArgumentException("Couldn't find the backTestingParameters for " +
                    backTestingParametersName);
        }

        for (BackTestingParameters backTestingParameters : backTestingParametersList) {
            System.out.println("Executing " + backTestingParameters.getName());
            simulation.reset();
            for (String inputLine : inputLines) {
                if (inputLine.trim().length() == 0) {
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

        parametersMap.put("FadeTheBreakoutNormalWithEdge_015_30SMA", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithEdge_015_30SMA")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withEdge(0.15, 30)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge-015-30SMA", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge-015-30SMA")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.15, 30)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge-05-30SMA", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge-05-30SMA")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.5, 30)
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
