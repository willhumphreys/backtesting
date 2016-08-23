package matcha;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PositionExecutorTest {

    private PositionExecutor positionExecutor;

    @Mock
    private BufferedWriter mockBufferedWriter;
    private BackTestingParameters backTestingParameters;
    private DecimalPointPlace decimalPointPlace;

    @Before
    public void setUp() throws Exception {
        positionExecutor = new PositionExecutor(new Signals(), new Utils());
        positionExecutor.setTimeToOpenPosition(true);
        backTestingParameters = new BackTestingParameters.Builder().setExtraTicks(10).setName("test")
                .createBackTestingParameters();

        decimalPointPlace = DecimalPointPlace.NORMAL;
    }


    @Test
    public void shouldOpenAShortPositionAtCloseIfPreviousDaysLowIsExceeded() throws Exception {
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        int extraTicks = 0;
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(true));

        final Position position = optionalPosition.get();

        //close + (close - low)
        //8 + (8 - 2)
        assertThat(position.getStop(), is(equalTo(14.0)));
        assertThat(position.getTarget(), is(equalTo(2.0)));
        assertThat(position.getEntryDate(), is(equalTo("2015-8-4T9:0:0")));
        assertThat(position.getEntry(), is(equalTo(8.0)));
    }

    @Test
    public void shouldNotOpenAShortPositionIfItIsNotTimeToOpenAPosition() throws Exception {
        positionExecutor.setTimeToOpenPosition(false);
        int extraTicks = 0;

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionIfWeAreNotAvailableToTrade() throws Exception {
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"}
        };
        int extraTicks = 0;
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(true));

        final Optional<Position> alreadyInAPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(alreadyInAPosition.isPresent(), is(false));
    }


    @Test
    public void shouldNotOpenAShortPositionACloseIfPreviousDaysLowIsNotExceeded() throws Exception {
        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8

        //Target is equal to todays Low
        String todaysLow = "3";
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", todaysLow, "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionACloseIfWeDoNotClosePositive() throws Exception {
        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8
        int extraTicks = 0;
        //Target is equal to todays Low
        String todaysLow = "2";
        String todaysClose = "5";

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", todaysLow, "9", todaysClose, "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionAtCloseIfWeDoNotCloseAboveYesterdaysLow() throws Exception {
        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8
        int extraTicks = 0;

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "2", "2", "6", "3", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionAtCloseIfWeDoNotCloseAboveYesterdaysOpen() throws Exception {
        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionIfTodaysLowDoesNotTakeOutYesterdaysLow() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "4", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }


    //usefulTickData.isTakeOutYesterdaysHigh() &&
    //        usefulTickData.isCloseNegative() &&
    //        usefulTickData.isCloseBelowYesterdaysHigh() &&
    //        usefulTickData.isOpenBelowYesterdaysHigh() &&
    //getHighCheck(usefulTickData, 0);
    @Test
    public void shouldOpenALongPositionAtCloseIfPreviousDaysHighIsExceeded() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(true));

        final Position position = optionalPosition.get();

        //close - (high - close)
        //2 - (9 - 2)
        //stop low
        //target high
        //entry close
        assertThat(position.getStop(), is(equalTo(-5.0)));
        assertThat(position.getTarget(), is(equalTo(9.0)));
        assertThat(position.getEntryDate(), is(equalTo("2015-8-4T9:0:0")));
        assertThat(position.getEntry(), is(equalTo(2.0)));
    }

    @Test
    public void shouldNotOpenALongPositionAtCloseIfItIsNotTimeToOpen() throws Exception {
        int extraTicks = 0;
        positionExecutor.setTimeToOpenPosition(false);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }


    //usefulTickData.isTakeOutYesterdaysHigh() &&
    //        usefulTickData.isCloseNegative() &&
    //        usefulTickData.isCloseBelowYesterdaysHigh() &&
    //        usefulTickData.isOpenBelowYesterdaysHigh() &&
    //getHighCheck(usefulTickData, 0);
    @Test
    public void shouldNotOpenALongPositionAtCloseIfTheCloseIsPositive() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "4", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenALongPositionAtCloseIfWeDoNotTakeOutYesterdaysHigh() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "4", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenALongPositionAtCloseIfWeOpenAboveYesterdaysHigh() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "8", "2", "9", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenALongPositionIfWeDoNotTakeOutTheHighOfThePreviousCandle() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "10", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldCloseShortPositionIfTargetExceeded() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"},
                {"2015-8-4T10:0:0", "5", "1", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        final Position position = optionalPosition.get();

        //Target 2.0
        //Entry 8.0
        //Stop 14.0
        final UsefulTickData usefulTickData2 = new UsefulTickData(data, 2);
        usefulTickData2.invoke();
        final PositionStats stats = new PositionStats();
;
        positionExecutor.managePosition(usefulTickData2, position, mockBufferedWriter, stats, backTestingParameters,
                decimalPointPlace);

        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getWinners(), is(equalTo(1)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getLosers(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getTickCounter(), is(equalTo(120000)));
    }

    @Test
    public void shouldCloseShortPositionIfStopTouched() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"},
                {"2015-8-4T10:0:0", "5", "1", "14", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        final Position position = optionalPosition.get();

        //Target 2.0
        //Entry 8.0
        //Stop 14.0
        final PositionStats stats = new PositionStats();

        final UsefulTickData usefulTickData2 = new UsefulTickData(data, 2);
        usefulTickData2.invoke();
        positionExecutor.managePosition(usefulTickData2, position, mockBufferedWriter, stats, backTestingParameters,
                decimalPointPlace);

        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getLosers(), is(equalTo(1)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getTickCounter(), is(equalTo(-120000)));
    }

    @Test
    public void shouldNotCloseShortPositionIfTargetOrStopNotHit() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"},
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"}
        };
        final PositionStats stats = new PositionStats();

        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        final Position position = optionalPosition.get();

        //Target 2.0
        //Entry 8.0
        //Stop 14.0
        final UsefulTickData usefulTickData2 = new UsefulTickData(data, 2);
        usefulTickData2.invoke();
        positionExecutor.managePosition(usefulTickData2, position, mockBufferedWriter, stats, backTestingParameters, decimalPointPlace);

        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getLosers(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getTickCounter(), is(equalTo(0)));
    }

    @Test
    public void shouldCloseLongPositionIfTargetExceeded() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "7", "10", "20"},
                {"2015-8-4T10:0:0", "3", "2", "20", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(true));

        final Position position = optionalPosition.get();
        final PositionStats stats = new PositionStats();

        //Target 2.0
        //Entry 8.0
        //Stop 14.0
        final UsefulTickData usefulTickData2 = new UsefulTickData(data, 2);
        usefulTickData2.invoke();
        positionExecutor.managePosition(usefulTickData2, position, mockBufferedWriter, stats, backTestingParameters, decimalPointPlace);

        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getWinners(), is(equalTo(1)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getLosers(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getTickCounter(), is(equalTo(140000)));
    }

    @Test
    public void shouldCloseLongPositionIfStopTouched() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "7", "10", "20"},
                {"2015-8-4T10:0:0", "3", "-6", "9", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(true));

        final Position position = optionalPosition.get();

        //Target 2.0
        //Entry 8.0
        //Stop 14.0
        final UsefulTickData usefulTickData2 = new UsefulTickData(data, 2);
        usefulTickData2.invoke();
        final PositionStats stats = new PositionStats();
        positionExecutor.managePosition(usefulTickData2, position, mockBufferedWriter, stats, backTestingParameters, decimalPointPlace);

        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getLosers(), is(equalTo(1)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getTickCounter(), is(equalTo(-140000)));
    }

    @Test
    public void shouldNotCloseLongPositionIfTargetOrStopNotHit() throws Exception {
        int extraTicks = 0;
        final String[][] data = {
                {"2015-8-4T8:0:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0:0", "3", "2", "9", "2", "4", "7", "10", "20"},
                {"2015-8-4T10:0:0:0", "3", "2", "9", "2", "4", "7", "10", "20"}
        };

        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData, extraTicks, 0, backTestingParameters, new PositionStats()
        );

        assertThat(optionalPosition.isPresent(), is(true));

        final Position position = optionalPosition.get();

        //Target 2.0
        //Entry 8.0
        //Stop 14.0
        final UsefulTickData usefulTickData2 = new UsefulTickData(data, 2);
        usefulTickData2.invoke();
        final PositionStats stats = new PositionStats();
        positionExecutor.managePosition(usefulTickData2, position, mockBufferedWriter, stats, backTestingParameters, decimalPointPlace);

        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getLosers(), is(equalTo(0)));
        assertThat(positionExecutor.getResults("test", mockBufferedWriter, stats).getPositionStats().getTickCounter(), is(equalTo(0)));
    }

}
