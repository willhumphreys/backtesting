package matcha;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.lang.String.format;

class ResultsWriterImpl implements ResultsWriter {

    private Path outputPath;

    ResultsWriterImpl(Path outputPath) {
        this.outputPath = outputPath;

        try {
            Files.deleteIfExists(outputPath);
        } catch (IOException e) {
            throw new CsvWritingException("Unable to delete the results file", e);
        }

        writeLine("symbol,tick_profit_loss,winners,loses,long_trade_count,short_trade_count\n");
    }

    @Override
    public void write(Results results) {

        final PositionStats positionStats = results.getPositionStats();
        String line = format("%s,%d,%d,%d,%d,%d\n", positionStats.getSymbol(), positionStats.getTickCounter(),
                positionStats.getWinners(), positionStats.getLosers(), positionStats.getLongTradeCount(),
                positionStats.getShortTradeCount());
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

