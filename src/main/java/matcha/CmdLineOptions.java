package matcha;

import java.nio.file.Path;

public class CmdLineOptions {
    private final Path inputPath;
    private final Path outputDirectory;
    private final int highLowPref;
    private final boolean aboveBelowMovingAverages;
    private final boolean aboveBelowBands;

    public CmdLineOptions(Path inputPath, Path outputDirectory, int highLowPref, boolean aboveBelowMovingAverages,
                          boolean aboveBelowBands) {
        this.inputPath = inputPath;
        this.outputDirectory = outputDirectory;
        this.highLowPref = highLowPref;
        this.aboveBelowMovingAverages = aboveBelowMovingAverages;
        this.aboveBelowBands = aboveBelowBands;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public int getHighLowPref() {
        return highLowPref;
    }

    public boolean isAboveBelowMovingAverages() {
        return aboveBelowMovingAverages;
    }

    public boolean isAboveBelowBands() {
        return aboveBelowBands;
    }

    public static class Builder {
        private Path inputPath;
        private Path outputDirectory;
        private int highLowPref;
        private boolean aboveBelowMovingAverages;
        private boolean aboveBelowBands;

        public Builder setInputPath(Path inputPath) {
            this.inputPath = inputPath;
            return this;
        }

        public Builder setOutputDirectory(Path outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public Builder setHighLowPref(int highLowPref) {
            this.highLowPref = highLowPref;
            return this;
        }

        public Builder setAboveBelowMovingAverages(boolean aboveBelowMovingAverages) {
            this.aboveBelowMovingAverages = aboveBelowMovingAverages;
            return this;
        }

        public Builder setAboveBelowBands(boolean aboveBelowBands) {
            this.aboveBelowBands = aboveBelowBands;
            return this;
        }

        public CmdLineOptions createCmdLineOptions() {
            return new CmdLineOptions(inputPath, outputDirectory, highLowPref, aboveBelowMovingAverages, aboveBelowBands);
        }
    }
}
