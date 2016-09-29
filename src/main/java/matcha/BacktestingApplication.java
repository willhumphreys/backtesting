package matcha;


import matcha.service.RScriptService;
import matcha.service.ScriptArguments;
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
    public static final Path ROOT_OUTPUT_PATH = Paths.get("results");

    public static void main(String[] args) throws Exception {

        final CmdLineOptions cmdLineOptions = getCmdLineOptions(args);

        BacktestingApplication backtestingApplication = new BacktestingApplication();
        backtestingApplication.run(cmdLineOptions);
        backtestingApplication.executeRScripts(cmdLineOptions);
    }

    private void executeRScripts(CmdLineOptions cmdLineOptions) {
        final RScriptService rScriptService = new RScriptService();
        rScriptService.executeScript(new ScriptArguments.Builder()
                .setScriptPath(Paths.get("RScripts/cumulativeProfit.r"))
                .setOutputPath(cmdLineOptions.getOutputDirectory())
                .setInputPath(cmdLineOptions.getOutputDirectory().resolve("data"))
                .createScriptArguments());
    }

    List<Results> run(CmdLineOptions args) throws Exception {


        Utils utils = new Utils();

        final List<String> inputLines = readAllLines(args.getInputPath());

        ResultsWriter resultsWriter = new ResultsWriterImpl(args.getOutputDirectory());

        List<Results> allResults = newArrayList();

        OpenOptions openOptions = new OpenOptions.Builder()
                .setHighLowPref(args.getHighLowPref())
                .setAboveBelowMovingAverages(args.isAboveBelowMovingAverages())
                .setAboveBelowBands(args.isAboveBelowBands())
                .createOpenOptions();

        final Simulation simulation = new Simulation(new PositionExecutor(utils), new TickDataReaderImpl(),
                new SyncTicks(), new FadeTheExtremesPositionPlacer(utils, openOptions), new TickDataFactory(),
                openOptions, new TickDataMinuteReaderImpl());

        for (String inputLine : inputLines) {
            if (inputLine.trim().length() == 0) {
                continue;
            }

            final Results results = simulation.execute(convertLineToInputs(inputLine), args.getOutputDirectory(),
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

    private static CmdLineOptions getCmdLineOptions(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("input", true, "The input file to use.");
        options.addOption("output_dir", true, "Where to output the results.");
        options.addOption("high_low_pref", true, "New low for the day is 1");
        options.addOption("moving_averages", false, "Buy below the moving average and sell above");
        options.addOption("bands", false, "Buy below the lower band and sell above the higher band");

        CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, args);


        if (!cmd.hasOption("input")) {
            throw new IllegalArgumentException("We need an input argument");
        }

        if (!cmd.hasOption("output_dir")) {
            throw new IllegalArgumentException("We need an output argument");
        }

        final Path inputPath = Paths.get(cmd.getOptionValue("input"));

        LOG.info(String.format("Using input file '%s'", inputPath));

        Path outputDirectory = Paths.get(cmd.getOptionValue("output_dir"));
        LOG.info(String.format("Using output directory '%s'", outputDirectory));

        final int highLowPref =  Integer.valueOf(cmd.getOptionValue("high_low_pref", "1"));
        final boolean aboveBelowMovingAverages =  cmd.hasOption("moving_averages");
        final boolean aboveBelowBands =  cmd.hasOption("bands");

        return new CmdLineOptions.Builder()
                .setInputPath(inputPath)
                .setOutputDirectory(outputDirectory)
                .setHighLowPref(highLowPref)
                .setAboveBelowMovingAverages(aboveBelowMovingAverages)
                .setAboveBelowBands(aboveBelowBands)
                .createCmdLineOptions();
    }

    private int getDecimalPointPlace(String inputLine) {
        if (inputLine.contains("jpy")) {
            return 100;
        } else {
            return 10000;
        }
    }
}
