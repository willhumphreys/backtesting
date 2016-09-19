package matcha;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ResultsWriterImplTest {

    private ResultsWriterImpl resultsWriter;

    @Before
    public void setUp() throws Exception {
        resultsWriter = new ResultsWriterImpl(Paths.get("testResults.csv"));
    }

    @Test
    public void shouldWriteALine() throws Exception {

        final PositionStats positionStats = new PositionStats("eurjpy");
        positionStats.addLoser(LocalDateTime.parse("2015-07-01T10:00:00"));
        final OpenOptions openOptions = new OpenOptions.Builder()
                .setHighLowPref(1)
                .setAboveBelowBands(true)
                .setAboveBelowMovingAverages(false)
                .createOpenOptions();
        resultsWriter.write(new Results("output.csv", positionStats, openOptions));
    }
}
