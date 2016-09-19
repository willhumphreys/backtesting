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

    BacktestingApplication() {
        resultsWriter = new ResultsWriterImpl(Paths.get("results.csv"));
    }

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

        if (!cmd.hasOption("output")) {
            throw new IllegalArgumentException("We need an output argument");
        }

        final Path inputPath = Paths.get(cmd.getOptionValue("input"));
        LOG.info(String.format("Using input file '%s'", inputPath));
        final List<String> inputLines = readAllLines(inputPath);

        Path outputDirectory = Paths.get(cmd.getOptionValue("output"));
        LOG.info(String.format("Using output directory '%s'", outputDirectory));

        List<Results> allResults = newArrayList();

        final Simulation simulation = new Simulation(new PositionExecutor(utils), new TickDataReaderImpl(),
                new SyncTicks(), new FadeTheExtremesPositionPlacer(utils, 1), new TickDataFactory());

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
        options.addOption("output", true, "Where to output the results.");

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
