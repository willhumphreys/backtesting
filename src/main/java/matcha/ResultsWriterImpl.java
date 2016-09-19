package matcha;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class ResultsWriterImpl implements ResultsWriter {

    private Path outputPath;

    ResultsWriterImpl(Path outputPath) {
        this.outputPath = outputPath;

        try {
            Files.deleteIfExists(outputPath);
        } catch (IOException e) {
            throw new CsvWritingException("Unable to delete the results file", e);
        }

        writeLine("symbol,winners,loses,long_trade_count,short_trade_count\n");
    }

    @Override
    public void write(Results results) {

        String line = results.getPositionStats().getWinners() + "," + results.getPositionStats().getLosers() + "\n";
        writeLine(line);
    }

    private void writeLine(String line) {
        try {
            Files.write(outputPath, line.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new CsvWritingException("Filed to write a line to the results file", e);
        }
    }

}

