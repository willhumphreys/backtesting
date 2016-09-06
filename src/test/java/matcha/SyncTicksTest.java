package matcha;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SyncTicksTest {

    @Test()
    public void shouldReturnSameHourIfTickMatchesHour() throws Exception {

        List<DataRecord> tickDataRecords = Lists.newArrayList(new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"));
        List<DataRecord> hourDataRecords = Lists.newArrayList(new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"));

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 2;
        final int currentTickCounter = 10;
        final int hourOfHourData = 18;
        final int tickOfHourData = 18;
        final int updatedHourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(
                tickDataRecords, hourDataRecords, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);

        assertThat(updatedHourCounter, is(equalTo(currentHourCounter)));
    }

    @Test()
    public void shouldUpdateHourIfTickDoNotMatchTick() throws Exception {

        List<DataRecord> tickDataRecords = Lists.newArrayList(
                new DataRecord("2007-12-13T19:19:00", "0", "0","0", "0", "0", "0", "0", "0")
        );
        List<DataRecord> hourDataRecords = Lists.newArrayList(
                new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"),
                new DataRecord("2007-12-13T19:19:00", "0", "0","0", "0", "0", "0", "0", "0")
        );

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 0;
        final int currentTickCounter = 0;
        final int hourOfHourData = 18;
        final int tickOfHourData = 19;
        final int updatedHourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(
                tickDataRecords, hourDataRecords, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);

        assertThat(updatedHourCounter, is(equalTo(1)));
    }

    @Test()
    public void shouldUpdateHourTwiceIfTicksDoNotMatchOnFirstTry() throws Exception {

        List<DataRecord> tickDataRecords = Lists.newArrayList(
                new DataRecord("2007-12-13T20:19:00", "0", "0","0", "0", "0", "0", "0", "0")
        );
        List<DataRecord> hourDataRecords = Lists.newArrayList(
                new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"),
                new DataRecord("2007-12-13T19:19:00", "0", "0","0", "0", "0", "0", "0", "0"),
                new DataRecord("2007-12-13T20:19:00", "0", "0","0", "0", "0", "0", "0", "0")
        );

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 0;
        final int currentTickCounter = 0;
        final int hourOfHourData = 18;
        final int tickOfHourData = 19;
        final int updatedHourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(
                tickDataRecords, hourDataRecords, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);

        assertThat(updatedHourCounter, is(equalTo(2)));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfHourDoesNotMatchTickAfterTwoIncrements() throws Exception {

        List<DataRecord> tickDataRecords = Lists.newArrayList(
                new DataRecord("2007-12-13T20:19:00", "0", "0","0", "0", "0", "0", "0", "0")
        );
        List<DataRecord> hourDataRecords = Lists.newArrayList(
                new DataRecord("2007-12-13T17:19:00", "0", "0","0", "0", "0", "0", "0", "0"),
                new DataRecord("2007-12-13T18:19:00", "0", "0","0", "0", "0", "0", "0", "0"),
                new DataRecord("2007-12-13T19:19:00", "0", "0","0", "0", "0", "0", "0", "0")
        );

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 0;
        final int currentTickCounter = 0;
        final int hourOfHourData = 18;
        final int tickOfHourData = 19;
        syncTicks.updateHourCounterToMatchMinuteCounter(
                tickDataRecords, hourDataRecords, currentHourCounter, currentTickCounter, hourOfHourData, tickOfHourData);
    }
}
