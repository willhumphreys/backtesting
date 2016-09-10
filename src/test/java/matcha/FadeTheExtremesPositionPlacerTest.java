package matcha;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class FadeTheExtremesPositionPlacerTest {

    private FadeTheExtremesPositionPlacer fadeTheExtremesPositionPlacer;

    @Before
    public void setUp() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils());
    }

    @Test
    public void shouldCreateAShortPosition() throws Exception {

        List<DataRecord> tickDataRecords = Lists.newArrayList(new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"));
        List<DataRecord> hourDataRecords = Lists.newArrayList(
                new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"),
                new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"));

        final UsefulTickData usefulTickData = new UsefulTickData
                (hourDataRecords, 1, tickDataRecords, 0);
        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5, 1);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getEntry(), is(equalTo(5)));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop(), is(equalTo(3343)));
        assertThat(position.get().getTarget(), is(equalTo(3422)));
    }
}