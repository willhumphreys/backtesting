package matcha;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class FadeTheExtremesPositionPlacerTest {

    private FadeTheExtremesPositionPlacer fadeTheExtremesPositionPlacer;
    private UsefulTickData.Builder shortPositionData;
    private UsefulTickData.Builder longPositionData;

    @Before
    public void setUp() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(false).setAboveBelowBands(false).createOpenOptions());

        shortPositionData = new UsefulTickData.Builder()
                .setCandleDate("2007-12-13T18:19:00")
                .setCandleClose(BigDecimal.valueOf(7))
                .setCandleLow(BigDecimal.valueOf(0))
                .setPreviousCandleLow(BigDecimal.valueOf(0))
                .setCandleHigh(BigDecimal.valueOf(9))
                .setPreviousCandleHigh(BigDecimal.valueOf(0))
                .setTakeOutYesterdaysLow(false)
                .setClosePositive(false)
                .setCloseAboveYesterdaysLow(false)
                .setOpenAboveYesterdaysLow(false)
                .setTakeOutYesterdaysHigh(true)
                .setCloseNegative(true)
                .setCloseBelowYesterdaysHigh(true)
                .setOpenBelowYesterdaysHigh(true)
                .setTodaysLow(BigDecimal.valueOf(0))
                .setTodaysHigh(BigDecimal.valueOf(1))
                .setLowOfDayForPreviousHour(BigDecimal.valueOf(0))
                .setHighOfDayForPreviousHour(BigDecimal.valueOf(0.5))
                .setTickLow(BigDecimal.valueOf(0))
                .setTickHigh(BigDecimal.valueOf(0));

         longPositionData = new UsefulTickData.Builder()
                .setCandleDate("2007-12-13T18:19:00")
                .setCandleClose(BigDecimal.valueOf(7))
                .setCandleLow(BigDecimal.valueOf(2))
                .setPreviousCandleLow(BigDecimal.valueOf(0))
                .setCandleHigh(BigDecimal.valueOf(9))
                .setPreviousCandleHigh(BigDecimal.valueOf(0))
                .setTakeOutYesterdaysLow(true)
                .setClosePositive(true)
                .setCloseAboveYesterdaysLow(true)
                .setOpenAboveYesterdaysLow(true)
                .setTakeOutYesterdaysHigh(false)
                .setCloseNegative(false)
                .setCloseBelowYesterdaysHigh(false)
                .setOpenBelowYesterdaysHigh(false)
                .setTodaysLow(BigDecimal.valueOf(0))
                .setTodaysHigh(BigDecimal.valueOf(1))
                .setLowOfDayForPreviousHour(BigDecimal.valueOf(4))
                .setHighOfDayForPreviousHour(BigDecimal.valueOf(0.5))
                .setTickLow(BigDecimal.valueOf(0))
                .setTickHigh(BigDecimal.valueOf(0));

    }

    @Test
    public void shouldCreateAShortPosition() throws Exception {

        final UsefulTickData usefulTickData = shortPositionData.createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getEntry(), is(equalTo(BigDecimal.valueOf(7))));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop().compareTo(position.get().getTarget()) > 0, is(true));
        assertThat(position.get().getStop(), is(equalTo(BigDecimal.valueOf(9))));
        assertThat(position.get().getTarget(), is(equalTo(BigDecimal.valueOf(5))));
    }

    @Test
    public void shouldNotCreateAShortPosition() throws Exception {
        final UsefulTickData usefulTickData = shortPositionData.setCloseNegative(false).createUsefulTickData();
        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);
        assertThat(position.isPresent(), is(false));
    }

    @Test
    public void shouldCreateALongPosition() throws Exception {
        final UsefulTickData usefulTickData = longPositionData.createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getStop().compareTo(position.get().getTarget()) > 0, is(false));
        assertThat(position.get().getEntry(), is(equalTo(BigDecimal.valueOf(7))));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop(), is(equalTo(BigDecimal.valueOf(2))));
        assertThat(position.get().getTarget(), is(equalTo(BigDecimal.valueOf(12))));
    }

    @Test
    public void shouldCreateALongPositionOnlyIfWeAreBelowTheMovingAverage() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(true).setAboveBelowBands(false).createOpenOptions());
        final UsefulTickData usefulTickData = longPositionData.setCloseBelowMovingAverage(true).createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getStop().compareTo(position.get().getTarget()) > 0, is(false));
        assertThat(position.get().getEntry(), is(equalTo(BigDecimal.valueOf(7))));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop(), is(equalTo(BigDecimal.valueOf(2))));
        assertThat(position.get().getTarget(), is(equalTo(BigDecimal.valueOf(12))));
    }

    @Test
    public void shouldNotCreateALongPositionOnlyIfWeAreNotBelowTheMovingAverage() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(true).setAboveBelowBands(false).createOpenOptions());
        final UsefulTickData usefulTickData = longPositionData.createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(false));
    }

    @Test
    public void shouldCreateAShortPositionOnlyIfWeAreAboveTheMovingAverage() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(true).setAboveBelowBands(false).createOpenOptions());
        final UsefulTickData usefulTickData = shortPositionData.setCloseAboveMovingAverage(true).createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getEntry(), is(equalTo(BigDecimal.valueOf(7))));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop().compareTo(position.get().getTarget()) > 0, is(true));
        assertThat(position.get().getStop(), is(equalTo(BigDecimal.valueOf(9))));
        assertThat(position.get().getTarget(), is(equalTo(BigDecimal.valueOf(5))));
    }

    @Test
    public void shouldNotCreateAShortPositionOnlyIfWeAreNotAboveTheMovingAverage() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(true).setAboveBelowBands(false).createOpenOptions());
        final UsefulTickData usefulTickData = shortPositionData.createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(false));
    }

    @Test
    public void shouldNotCreateALongPosition() throws Exception {
        final UsefulTickData usefulTickData = longPositionData.setClosePositive(false).createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(false));
    }

    //Bollinger tests
    @Test
    public void shouldCreateALongPositionOnlyIfWeAreBelowTheLowBand() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(false).setAboveBelowBands(true).createOpenOptions());
        final UsefulTickData usefulTickData = longPositionData.setCloseBelowBottomBand(true).createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getStop().compareTo(position.get().getTarget()) > 0, is(false));
        assertThat(position.get().getEntry(), is(equalTo(BigDecimal.valueOf(7))));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop(), is(equalTo(BigDecimal.valueOf(2))));
        assertThat(position.get().getTarget(), is(equalTo(BigDecimal.valueOf(12))));
    }

    @Test
    public void shouldNotCreateALongPositionOnlyIfWeAreNotBelowTheLowBand() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(false).setAboveBelowBands(true).createOpenOptions());
        final UsefulTickData usefulTickData = longPositionData.createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(false));
    }

    @Test
    public void shouldCreateAShortPositionOnlyIfWeAreAboveTheHighBand() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(false).setAboveBelowBands(true).createOpenOptions());
        final UsefulTickData usefulTickData = shortPositionData.setCloseAboveTopBand(true).createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(true));
        assertThat(position.get().getEntry(), is(equalTo(BigDecimal.valueOf(7))));
        assertThat(position.get().getEntryDate(), is(equalTo(LocalDateTime.parse("2007-12-13T18:19:00"))));
        assertThat(position.get().getStop().compareTo(position.get().getTarget()) > 0, is(true));
        assertThat(position.get().getStop(), is(equalTo(BigDecimal.valueOf(9))));
        assertThat(position.get().getTarget(), is(equalTo(BigDecimal.valueOf(5))));
    }

    @Test
    public void shouldNotCreateAShortPositionOnlyIfWeAreNotAboveTheHighBand() throws Exception {
        fadeTheExtremesPositionPlacer = new FadeTheExtremesPositionPlacer(new Utils(), new OpenOptions.Builder().setHighLowPref(1).setAboveBelowMovingAverages(false).setAboveBelowBands(true).createOpenOptions());
        final UsefulTickData usefulTickData = shortPositionData.createUsefulTickData();

        final Optional<Position> position = fadeTheExtremesPositionPlacer.placePositions(usefulTickData, 5);

        assertThat(position.isPresent(), is(false));
    }
}
