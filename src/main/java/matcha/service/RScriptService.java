package matcha.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import matcha.CmdLineOptions;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static matcha.BacktestingApplication.ROOT_OUTPUT_PATH;
import static org.slf4j.LoggerFactory.getLogger;

public class RScriptService {

    private static final Logger LOG = getLogger(RScriptService.class);

    private static final Path R_SCRIPT_LOCATION = Paths.get("/usr/bin/Rscript");
    private static final Path MAC_R_SCRIPT_LOCATION = Paths.get("/usr/bin/Rscript");

    int executeScript(ScriptArguments scriptArguments) {

        List<String> commands = Lists.newArrayList();
        commands.add(getRLocation().toString());

        commands.add(scriptArguments.getScriptPath().toString());
        commands.add(scriptArguments.getInputPath().toString());
        commands.add(scriptArguments.getOutputPath().toString());

        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
//            pb.redirectOutput(new File("/tmp/graphOutput_" + lastElement + ".log"));
//            pb.redirectError(new File("/tmp/graphOutputError_" + lastElement + ".log"));
            Process p = pb.start();
            return p.waitFor();
        } catch (Exception e) {
            LOG.error("Problem executing script", e);
            return 1;
        }
    }



    private List<ScriptArguments> getAllScriptArguments(CmdLineOptions cmdLineOptions) {
        final ScriptArguments cumulativeProfitScriptArguments = new ScriptArguments.Builder()
                .setScriptPath(Paths.get("RScripts/cumulativeProfit.r"))
                .setOutputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory()))
                .setInputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory().resolve("data")))
                .createScriptArguments();

        final ScriptArguments resultsImprovementArguments = new ScriptArguments.Builder()
                .setScriptPath(Paths.get("RScripts/results_improvement.r"))
                .setOutputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory().resolve("results_improved.csv")))
                .setInputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory()))
                .createScriptArguments();

        final ScriptArguments winners_by_year = new ScriptArguments.Builder()
                .setScriptPath(Paths.get("RScripts/winners_by_year.r"))
                .setOutputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory()))
                .setInputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory()))
                .createScriptArguments();

        final ScriptArguments summaryArguments = new ScriptArguments.Builder()
                .setScriptPath(Paths.get("RScripts/summary.r"))
                .setOutputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory().resolve("summary.txt")))
                .setInputPath(ROOT_OUTPUT_PATH.resolve(cmdLineOptions.getOutputDirectory()))
                .createScriptArguments();

        return ImmutableList.of(cumulativeProfitScriptArguments, resultsImprovementArguments, summaryArguments);
    }


    private Path getRLocation() {
        final String os = System.getProperty("os.name");
        switch (os) {
            case "Linux":
                return R_SCRIPT_LOCATION;
            case "Mac OS X":
                return MAC_R_SCRIPT_LOCATION;
            default:
                throw new IllegalStateException("Don't recognise the os " + os);
        }
    }

    public void executeAll(CmdLineOptions cmdLineOptions) {
        getAllScriptArguments(cmdLineOptions).forEach(this::executeScript);
    }
}
