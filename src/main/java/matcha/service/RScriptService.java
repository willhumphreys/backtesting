package matcha.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class RScriptService {

    private static final Logger LOG = getLogger(RScriptService.class);

    private static final Path R_SCRIPT_LOCATION = Paths.get("/usr/bin/Rscript");
    private static final Path MAC_R_SCRIPT_LOCATION = Paths.get("/usr/bin/Rscript");

    public int executeScript(Path scriptPath, Path outputPath) {

        final Path lastElement = scriptPath.getName(scriptPath.getNameCount() - 1);

        List<String> commands = Lists.newArrayList();
        commands.add(getRLocation().toString());

        commands.add(scriptPath.toString());
        commands.add(outputPath.toString());

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

    private List<String> convertPathsToStrings(String runName, List<String> paths) {

        List<String> convertedPaths = Lists.newArrayList();

        for (String path : paths) {
            convertedPaths.add(runName + "/" + path);
        }

        if (convertedPaths.isEmpty()) {
            convertedPaths.add(runName);
        }

        return convertedPaths;
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
}
