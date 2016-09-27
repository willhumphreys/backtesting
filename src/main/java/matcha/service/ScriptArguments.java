package matcha.service;

import java.nio.file.Path;

public class ScriptArguments {
    private final Path scriptPath;
    private final Path outputPath;
    private final Path inputPath;

    public ScriptArguments(Path scriptPath, Path outputPath, Path inputPath) {
        this.scriptPath = scriptPath;
        this.outputPath = outputPath;
        this.inputPath = inputPath;
    }

    /**
     * Gets scriptPath
     *
     * @return value of scriptPath
     */
    public Path getScriptPath() {
        return scriptPath;
    }

    /**
     * Gets outputPath
     *
     * @return value of outputPath
     */
    public Path getOutputPath() {
        return outputPath;
    }

    /**
     * Gets inputPath
     *
     * @return value of inputPath
     */
    public Path getInputPath() {
        return inputPath;
    }

    public static class Builder {
        private Path scriptPath;
        private Path outputPath;
        private Path inputPath;

        public Builder setScriptPath(Path scriptPath) {
            this.scriptPath = scriptPath;
            return this;
        }

        public Builder setOutputPath(Path outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder setInputPath(Path inputPath) {
            this.inputPath = inputPath;
            return this;
        }

        public ScriptArguments createScriptArguments() {
            return new ScriptArguments(scriptPath, outputPath, inputPath);
        }
    }
}
