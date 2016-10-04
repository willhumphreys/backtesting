package matcha;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

import static java.lang.String.format;

class ResultsWriterImpl implements ResultsWriter {

    private Path outputPath;

    ResultsWriterImpl(Path outputPath) {
        this.outputPath = outputPath;

        if(outputPath.toFile().exists()) {
            try {
                Files.walk(outputPath, FileVisitOption.FOLLOW_LINKS)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);

            } catch (IOException e) {
                throw new CsvWritingException("Unable to delete the results file", e);
            }
        }
        try {
            Files.createDirectories(outputPath);
        } catch (IOException e) {
            throw new CsvWritingException("Unable to create output directories", e);
        }

        this.outputPath = outputPath.resolve("results.csv");

        writeLine("symbol,tick_profit_loss,winners,losers,long_trade_count,short_trade_count,above_below_moving_average," +
                "above_below_band\n");
    }

    @Override
    public void write(Results results) {
        final PositionStats positionStats = results.getPositionStats();
        String line = format("%s,%d,%d,%d,%d,%d,%b,%b\n", positionStats.getSymbol(), positionStats.getTickCounter(),
                positionStats.getWinners(), positionStats.getLosers(), positionStats.getLongTradeCount(),
                positionStats.getShortTradeCount(), results.getOpenOptions().isAboveBelowMovingAverages(),
                results.getOpenOptions().isAboveBelowBands());
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

