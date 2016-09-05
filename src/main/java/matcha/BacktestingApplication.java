package matcha;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.cartesianProduct;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.nio.file.Files.readAllLines;
import static org.slf4j.LoggerFactory.getLogger;

public class BacktestingApplication {

    private static final Logger LOG = getLogger(MethodHandles.lookup().lookupClass());

    private static final int EXTRA_TICKS = 10;
    private static final Path DEFAULT_OUTPUT_DIRECTORY = Paths.get("results");

    public static void main(String[] args) throws Exception {


        Injector injector = Guice.createInjector((Module) binder -> {

        });

        final BacktestingApplication instance = injector.getInstance(BacktestingApplication.class);

        instance.run(args);
    }

    List<Results> run(String... args) throws Exception {

        Options options = new Options();
        options.addOption("scenario", true, "The scenario to use.");
        options.addOption("input", true, "The input file to use");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        List<Results> allResults = newArrayList();

        Map<String, BackTestingParameters> parametersMap = createParametersMap(EXTRA_TICKS);

        Path outputDirectory = getOutputDirectory(args);

        final String backTestingParametersName = cmd.getOptionValue("scenario");

        final Path inputPath = Paths.get(cmd.getOptionValue("input"));
        LOG.info(String.format("Using input file '%s'", inputPath));
        final List<String> inputLines = readAllLines(inputPath);

        List<BackTestingParameters> backTestingParametersList = newArrayList();

        if (backTestingParametersName.equals("allFadeCounts")) {
            final Set<Double> smas = newLinkedHashSet(newArrayList(30.0, 40.0));
            final Set<Double> levels = newLinkedHashSet(newArrayList(0.2, 0.3, 0.4));
            final Set<Double> stopEdgeModifiers = newLinkedHashSet(newArrayList(0.0));

            final Set<List<Double>> smaLevelCombinations = cartesianProduct(smas, levels, stopEdgeModifiers);


            for (int x = 1; x < 5; x++) {


                for (List<Double> smaLevelCombination : smaLevelCombinations) {
                    final Double sma = smaLevelCombination.get(0);
                    final Double level = smaLevelCombination.get(1);
                    final Double stopEdgeModifier = smaLevelCombination.get(2);

                    if (Math.abs(stopEdgeModifier) >= level) {
                        continue;
                    }

                    final BackTestingParameters backTestingParameters = new BackTestingParameters.Builder()
                            .setName("FadeTheBreakoutNormalWithTradeCountEdgeSEM-TM-2-level:" + level + "-sma:" + sma +
                                    "-stopEdgeModifier:" + stopEdgeModifier + "-targetMultiplier:" + x)
                            .setExtraTicks(EXTRA_TICKS)
                            .setHighLowCheckPref(0)
                            .fadeTheBreakout()
                            .withTradeCountEdge(level, sma.intValue())
                            .withEdgeStopLevelCount(stopEdgeModifier)
                            .withTargetMultiplier(x)
                            .createBackTestingParameters();

                    backTestingParametersList.add(backTestingParameters);
                }
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
            LOG.info("Executing " + backTestingParameters.getName());
            final Simulation simulation = new Simulation(new PositionExecutor(new Signals(), new Utils()), new TickDataReaderImpl(), new SyncTicks());

            for (String inputLine : inputLines) {
                if (inputLine.trim().length() == 0) {
                    continue;
                }

                String[] lineParts = inputLine.split(",");
                final Path oneMinutePath = Paths.get(lineParts[0]);
                final Path sixtyMinutePath = Paths.get(lineParts[1]);
                Inputs input = new Inputs(oneMinutePath, sixtyMinutePath);

                final Results results = simulation.execute(input, outputDirectory, backTestingParameters,
                        getDecimalPointPlace(inputLine));

                allResults.add(results);

                LOG.info(results.toString());
            }
        }

        return allResults;
    }

    private Path getOutputDirectory(String[] args) {
        Path outputDirectory = DEFAULT_OUTPUT_DIRECTORY;
        if (args.length > 2) {
            outputDirectory = Paths.get(args[2]);
        }
        LOG.info(String.format("Using output directory '%s'", outputDirectory));
        return outputDirectory;
    }

    private DecimalPointPlace getDecimalPointPlace(String inputLine) {
        DecimalPointPlace decimalPointPlace;
        if (inputLine.contains("jpy")) {
            decimalPointPlace = DecimalPointPlace.JPY;
        } else {
            decimalPointPlace = DecimalPointPlace.NORMAL;
        }
        return decimalPointPlace;
    }

    private Map<String, BackTestingParameters> createParametersMap(int extraTicks) {
        Map<String, BackTestingParameters> parametersMap = newHashMap();
        parametersMap.put("SNormalWithTradeCountEdge-03-10SMA", new BackTestingParameters.Builder()
                .setName("SNormalWithTradeCountEdge-03-10SMA")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.3, 10)
                .createBackTestingParameters());

        parametersMap.put("NormalDaily", new BackTestingParameters.Builder()
                .setName("NormalDaily")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(1)
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

        parametersMap.put("FadeTheBreakoutNormalDaily", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalDaily")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(1)
                .fadeTheBreakout()
                .createBackTestingParameters());


        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge_01_20SMA", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge_01_20SMA")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.1, 20)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge-02-30SMA", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge-02-30SMA")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.2, 30)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge-05-30SMA-0", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge-05-30SMA-0")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.5, 30)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge-03-30SMA-0", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge-03-30SMA-0")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.3, 30)
                .createBackTestingParameters());

        parametersMap.put("FadeTheBreakoutNormalWithTradeCountEdge-04-30SMA--1", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalWithTradeCountEdge-04-30SMA--1")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(0)
                .fadeTheBreakout()
                .withTradeCountEdge(0.4, 30)
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
