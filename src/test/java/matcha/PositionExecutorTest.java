package matcha;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class PositionExecutorTest {

    @Test
    public void shouldCloseALongPositionAtTargetIfTargetIsExceeded() throws Exception {
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
}
