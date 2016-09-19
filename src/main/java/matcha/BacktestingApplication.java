package matcha;


import org.apache.commons.cli.*;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.Files.readAllLines;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Typical program arguments
 * -output results -input inputFileList.csv
 */
class BacktestingApplication {

    private static final Logger LOG = getLogger(MethodHandles.lookup().lookupClass());
    private ResultsWriter resultsWriter;

    public static void main(String[] args) throws Exception {
        BacktestingApplication backtestingApplication = new BacktestingApplication();
        backtestingApplication.run(args);
    }

    List<Results> run(String... args) throws Exception {
        CommandLine cmd = getCmdLineOptions(args);

        Utils utils = new Utils();

        if (!cmd.hasOption("input")) {
            throw new IllegalArgumentException("We need an input argument");
        }

        if (!cmd.hasOption("output_dir")) {
            throw new IllegalArgumentException("We need an output argument");
        }

        final Path inputPath = Paths.get(cmd.getOptionValue("input"));

        resultsWriter = new ResultsWriterImpl(Paths.get("results.csv"));

        LOG.info(String.format("Using input file '%s'", inputPath));
        final List<String> inputLines = readAllLines(inputPath);

        Path outputDirectory = Paths.get(cmd.getOptionValue("output_dir"));
        LOG.info(String.format("Using output directory '%s'", outputDirectory));

        resultsWriter = new ResultsWriterImpl(outputDirectory.resolve("results.csv"));

        final int highLowPref =  Integer.valueOf(cmd.getOptionValue("high_low_pref", "1"));
        final boolean aboveBelowMovingAverages =  cmd.hasOption("moving_averages");
        final boolean aboveBelowBands =  cmd.hasOption("bands");

        List<Results> allResults = newArrayList();

        OpenOptions openOptions = new OpenOptions.Builder()
                .setHighLowPref(highLowPref)
                .setAboveBelowMovingAverages(aboveBelowMovingAverages)
                .setAboveBelowBands(aboveBelowBands)
                .createOpenOptions();

        final Simulation simulation = new Simulation(new PositionExecutor(utils), new TickDataReaderImpl(),
                new SyncTicks(), new FadeTheExtremesPositionPlacer(utils, openOptions), new TickDataFactory(),
                openOptions, new TickDataMinuteReaderImpl());

        for (String inputLine : inputLines) {
            if (inputLine.trim().length() == 0) {
                continue;
            }

            final Results results = simulation.execute(convertLineToInputs(inputLine), outputDirectory,
                    getDecimalPointPlace(inputLine));

            allResults.add(results);
            resultsWriter.write(results);
            LOG.info(results.toString());
        }
        return allResults;
    }

    private Inputs convertLineToInputs(String inputLine) {
        String[] lineParts = inputLine.split(",");
        final String symbol = lineParts[0];
        final Path oneMinutePath = Paths.get(lineParts[1]);
        final Path sixtyMinutePath = Paths.get(lineParts[2]);
        return new Inputs(symbol, oneMinutePath, sixtyMinutePath);
    }

    private CommandLine getCmdLineOptions(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("input", true, "The input file to use.");
        options.addOption("output_dir", true, "Where to output the results.");
        options.addOption("high_low_pref", true, "New low for the day is 1");
        options.addOption("moving_averages", false, "Buy below the moving average and sell above");
        options.addOption("bands", false, "Buy below the lower band and sell above the higher band");

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private int getDecimalPointPlace(String inputLine) {
        if (inputLine.contains("jpy")) {
            return 100;
        } else {
            return 10000;
        }
    }
}
