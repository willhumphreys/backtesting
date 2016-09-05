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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.nio.file.Files.readAllLines;
import static org.slf4j.LoggerFactory.getLogger;

public class BacktestingApplication {

    private static final Logger LOG = getLogger(MethodHandles.lookup().lookupClass());

    private static final int EXTRA_TICKS = 10;

    public static void main(String[] args) throws Exception {


        Injector injector = Guice.createInjector((Module) binder -> {

        });

        final BacktestingApplication instance = injector.getInstance(BacktestingApplication.class);

        instance.run(args);
    }

    List<Results> run(String... args) throws Exception {

        Options options = new Options();
        options.addOption("scenario", true, "The scenario to use.");
        options.addOption("input", true, "The input file to use.");
        options.addOption("output", true, "Where to output the results.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        List<Results> allResults = newArrayList();

        Map<String, BackTestingParameters> parametersMap = createParametersMap(EXTRA_TICKS);

        Path outputDirectory = Paths.get(cmd.getOptionValue("output"));
        LOG.info(String.format("Using output directory '%s'", outputDirectory));

        final String backTestingParametersName = cmd.getOptionValue("scenario");

        final Path inputPath = Paths.get(cmd.getOptionValue("input"));
        LOG.info(String.format("Using input file '%s'", inputPath));
        final List<String> inputLines = readAllLines(inputPath);

        List<BackTestingParameters> backTestingParametersList = newArrayList();

        final BackTestingParameters backTestingParameters2 = parametersMap.get(backTestingParametersName);
        backTestingParametersList.add(backTestingParameters2);

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

    private int getDecimalPointPlace(String inputLine) {
        if (inputLine.contains("jpy")) {
            return 200;
        } else {
            return 2000;
        }
    }

    private Map<String, BackTestingParameters> createParametersMap(int extraTicks) {
        Map<String, BackTestingParameters> parametersMap = newHashMap();

        parametersMap.put("FadeTheBreakoutNormalDaily", new BackTestingParameters.Builder()
                .setName("FadeTheBreakoutNormalDaily")
                .setExtraTicks(extraTicks)
                .setHighLowCheckPref(1)
                .fadeTheBreakout()
                .createBackTestingParameters());

        return parametersMap;
    }
}
