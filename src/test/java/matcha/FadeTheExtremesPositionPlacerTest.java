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
                .setCandleClose(7)
                .setCandleLow(0)
                .setPreviousCandleLow(0)
                .setCandleHigh(9)
                .setPreviousCandleHigh(0)
                .setTakeOutYesterdaysLow(false)
                .setClosePositive(false)
                .setCloseAboveYesterdaysLow(false)
                .setOpenAboveYesterdaysLow(false)
                .setTakeOutYesterdaysHigh(true)
                .setCloseNegative(true)
                .setCloseBelowYesterdaysHigh(true)
                .setOpenBelowYesterdaysHigh(true)
                .setTodaysLow(0)
                .setTodaysHigh(1)
                .setLowOfDayForPreviousHour(0)
                .setHighOfDayForPreviousHour(0.5)
                .setTickLow(0)
                .setTickHigh(0)
                .createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5, 1);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getEntry(), is(equalTo(7.0)));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop() > position.get().getTarget(), is(true));
        assertThat(position.get().getStop(), is(equalTo(9.0)));
        assertThat(position.get().getTarget(), is(equalTo(5.0)));
    }

    @Test
    public void shouldCreateALongPosition() throws Exception {
        final UsefulTickData usefulTickData = new UsefulTickDataBuilder()
                .setCandleDate("2007-12-13T18:19:00")
                .setCandleClose(7)
                .setCandleLow(2)
                .setPreviousCandleLow(0)
                .setCandleHigh(9)
                .setPreviousCandleHigh(0)
                .setTakeOutYesterdaysLow(true)
                .setClosePositive(true)
                .setCloseAboveYesterdaysLow(true)
                .setOpenAboveYesterdaysLow(true)
                .setTakeOutYesterdaysHigh(false)
                .setCloseNegative(false)
                .setCloseBelowYesterdaysHigh(false)
                .setOpenBelowYesterdaysHigh(false)
                .setTodaysLow(0)
                .setTodaysHigh(1)
                .setLowOfDayForPreviousHour(4)
                .setHighOfDayForPreviousHour(0.5)
                .setTickLow(0)
                .setTickHigh(0)
                .createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5, 1);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getStop() > position.get().getTarget(), is(false));
        assertThat(position.get().getEntry(), is(equalTo(7.0)));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop(), is(equalTo(2.0)));
        assertThat(position.get().getTarget(), is(equalTo(12.0)));
    }
}