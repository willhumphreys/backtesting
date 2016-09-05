package matcha;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SyncTicksTest {

    @Test()
    public void shouldReturnSameHourIfTickMatchesHour() throws Exception {

        String[][] tickData = new String[][]{
                {"2007-12-13T18:19:00"}
        };

        //Already in sync. So Nothing to do.
        String[][] hourData = new String[][]{
                {"2007-12-13T18:19:00"}
        };

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 2;
        final int currentTickCounter = 10;
        final int hourOfHourData = 18;
        final int tickOfHourData = 18;
        final int updatedHourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(
                tickData, hourData, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);

        assertThat(updatedHourCounter, is(equalTo(currentHourCounter)));
    }

    @Test()
    public void shouldUpdateHourIfTickDoNotMatchTick() throws Exception {

        String[][] tickData = new String[][]{
                {"2007-12-13T19:19:00"}
        };

        //Catches up on the first increment
        String[][] hourData = new String[][]{
                {"2007-12-13T18:19:00"},
                {"2007-12-13T19:19:00"}
        };

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 0;
        final int currentTickCounter = 0;
        final int hourOfHourData = 18;
        final int tickOfHourData = 19;
        final int updatedHourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(
                tickData, hourData, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);

        assertThat(updatedHourCounter, is(equalTo(1)));
    }

    @Test()
    public void shouldUpdateHourTwiceIfTicksDoNotMatchOnFirstTry() throws Exception {

        String[][] tickData = new String[][]{
                {"2007-12-13T20:19:00"}
        };

        //Catches up on the second increment.
        String[][] hourData = new String[][]{
                {"2007-12-13T18:19:00"},
                {"2007-12-13T19:19:00"},
                {"2007-12-13T20:19:00"}
        };

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 0;
        final int currentTickCounter = 0;
        final int hourOfHourData = 18;
        final int tickOfHourData = 19;
        final int updatedHourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(
                tickData, hourData, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);

        assertThat(updatedHourCounter, is(equalTo(2)));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfHourDoesNotMatchTickAfterTwoIncrements() throws Exception {

        String[][] tickData = new String[][]{
                {"2007-12-13T20:19:00"}
        };

        //Tick hour is 20. After two increments the hour of the hour data still hasn't caught up.
        String[][] hourData = new String[][]{
                {"2007-12-13T17:19:00"},
                {"2007-12-13T18:19:00"},
                {"2007-12-13T19:19:00"}
        };

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 0;
        final int currentTickCounter = 0;
        final int hourOfHourData = 18;
        final int tickOfHourData = 19;
        syncTicks.updateHourCounterToMatchMinuteCounter(
                tickData, hourData, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);
    }
}
