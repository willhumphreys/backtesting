package matcha;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
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

        final UsefulTickData usefulTickData = new UsefulTickDataBuilder()
                .setCandleDate("2007-12-13T18:19:00")
                .setCandleClose(0)
                .setCandleLow(0)
                .setPreviousCandleLow(0)
                .setCandleHigh(0)
                .setPreviousCandleHigh(0)
                .setTakeOutYesterdaysLow(false)
                .setClosePositive(false)
                .setCloseAboveYesterdaysLow(false)
                .setOpenAboveYesterdaysLow(false)
                .setTakeOutYesterdaysHigh(false)
                .setCloseNegative(false)
                .setCloseBelowYesterdaysHigh(false)
                .setOpenBelowYesterdaysHigh(false)
                .setTodaysLow(0)
                .setTodaysHigh(0)
                .setLowOfDayForPreviousHour(0)
                .setHighOfDayForPreviousHour(0)
                .setTickLow(0)
                .setTickHigh(0)
                .createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5, 1);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getEntry(), is(equalTo(5)));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop(), is(equalTo(3343)));
        assertThat(position.get().getTarget(), is(equalTo(3422)));
    }
}