package matcha;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class PositionExecutorTest {

    @Test
    public void shouldOpenAShortPositionAtCloseIfPreviousDaysLowIsExceeded() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

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
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(false);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionIfWeAreNotAvailableToTrade() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "2", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(true));

        final Optional<Position> alreadyInAPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(alreadyInAPosition.isPresent(), is(false));
    }


    @Test
    public void shouldNotOpenAShortPositionACloseIfPreviousDaysLowIsNotExceeded() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8

        //Target is equal to todays Low
        String todaysLow = "3";

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", todaysLow, "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionACloseIfWeDoNotClosePositive() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8

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
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionAtCloseIfWeDoNotCloseAboveYesterdaysLow() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8



        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "2", "2", "6", "3", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionAtCloseIfWeDoNotCloseAboveYesterdaysOpen() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        //Target = CandleLow = 3
        //Stop = Candle Close 8 + (8 - 3) = 13
        //Entry = Candle Close = 8

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenAShortPositionIfTodaysLowDoesNotTakeOutYesterdaysLow() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "5", "4", "9", "8", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }


    //usefulTickData.isTakeOutYesterdaysHigh() &&
    //        usefulTickData.isCloseNegative() &&
    //        usefulTickData.isCloseBelowYesterdaysHigh() &&
    //        usefulTickData.isOpenBelowYesterdaysHigh() &&
    //getHighCheck(usefulTickData, 0);
    @Test
    public void shouldOpenALongPositionAtCloseIfPreviousDaysHighIsExceeded() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

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
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(false);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }


    //usefulTickData.isTakeOutYesterdaysHigh() &&
    //        usefulTickData.isCloseNegative() &&
    //        usefulTickData.isCloseBelowYesterdaysHigh() &&
    //        usefulTickData.isOpenBelowYesterdaysHigh() &&
    //getHighCheck(usefulTickData, 0);
    @Test
    public void shouldNotOpenALongPositionAtCloseIfTheCloseIsPositive() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "4", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenALongPositionAtCloseIfWeDoNotTakeOutYesterdaysHigh() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "4", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenALongPositionAtCloseIfWeOpenAboveYesterdaysHigh() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "8", "2", "9", "2", "4", "7", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }

    @Test
    public void shouldNotOpenALongPositionIfWeDoNotTakeOutTheHighOfThePreviousCandle() throws Exception {
        final PositionExecutor positionExecutor = new PositionExecutor(new Signals(), new Utils());

        positionExecutor.setTimeToOpenPosition(true);

        final String[][] data = {
                {"2015-8-4T8:0:0", "5", "3", "6", "4", "10", "20", "10", "20"},
                //open, low, high, close, yesterdays, low, yesterdays high, todays low, todays high
                {"2015-8-4T9:0:0", "3", "2", "9", "2", "4", "10", "10", "20"}
        };
        final UsefulTickData usefulTickData = new UsefulTickData(data, 1);
        usefulTickData.invoke();
        final Optional<Position> optionalPosition = positionExecutor.placePositions(usefulTickData);

        assertThat(optionalPosition.isPresent(), is(false));
    }
}
