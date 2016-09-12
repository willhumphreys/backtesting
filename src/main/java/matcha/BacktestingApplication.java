package matcha;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
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
 * -scenario FadeTheBreakoutNormalDaily -output results -input inputFileList.csv
 */
public class BacktestingApplication {

    private static final Logger LOG = getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {


        Injector injector = Guice.createInjector((Module) binder -> {
        });
        final BacktestingApplication instance = injector.getInstance(BacktestingApplication.class);
        instance.run(args);
    }

    List<Results> run(String... args) throws Exception {
        CommandLine cmd = getCmdLineOptions(args);

        Utils utils = new Utils();

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

                String[] lineParts = inputLine.split(",");
                final Path oneMinutePath = Paths.get(lineParts[0]);
                final Path sixtyMinutePath = Paths.get(lineParts[1]);
                Inputs input = new Inputs(oneMinutePath, sixtyMinutePath);

                final Results results = simulation.execute(input, outputDirectory,
                        getDecimalPointPlace(inputLine));

                allResults.add(results);

                LOG.info(results.toString());
            }


        return allResults;
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
