package matcha;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SyncTicksTest {

    private DataRecord.Builder dataRecordBuilder;

    @Before
    public void setUp() throws Exception {
        dataRecordBuilder = new DataRecord.Builder()
                .setDateTime("2007-12-13T18:19:00")
                .setOpen(0)
                .setLow(0)
                .setHigh(0)
                .setClose(0)
                .setYesterdaysDailyLow(0)
                .setYesterdaysDailyHigh(0)
                .setTodaysLow(0)
                .setTodaysHigh(0);
    }

    @Test()
    public void shouldReturnSameHourIfTickMatchesHour() throws Exception {

        final SyncTicks syncTicks = new SyncTicks();

        final int currentHourCounter = 2;
        final int currentTickCounter = 10;
        final int hourOfHourData = 18;
        final int tickOfHourData = 18;
        final int updatedHourCounter = syncTicks.updateHourCounterToMatchMinuteCounter(
               newArrayList(dataRecordBuilder.createDataRecord()),
                newArrayList(dataRecordBuilder.createDataRecord()),
                currentHourCounter,
                currentTickCounter,
                hourOfHourData,
                tickOfHourData);

        assertThat(updatedHourCounter, is(equalTo(currentHourCounter)));
    }

    @Test()
    public void shouldUpdateHourIfTickDoNotMatchTick() throws Exception {
        List<DataRecord> tickDataRecords = newArrayList(
                dataRecordBuilder.setDateTime("2007-12-13T19:19:00").createDataRecord()
        );
        List<DataRecord> hourDataRecords = newArrayList(
                dataRecordBuilder.setDateTime("2007-12-13T18:19:00").createDataRecord(),
                dataRecordBuilder.setDateTime("2007-12-13T19:19:00").createDataRecord()
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

        List<DataRecord> tickDataRecords = newArrayList(
                dataRecordBuilder.setDateTime("2007-12-13T20:19:00").createDataRecord()
        );
        List<DataRecord> hourDataRecords = newArrayList(
                dataRecordBuilder.setDateTime("2007-12-13T18:19:00").createDataRecord(),
                dataRecordBuilder.setDateTime("2007-12-13T19:19:00").createDataRecord(),
                dataRecordBuilder.setDateTime("2007-12-13T20:19:00").createDataRecord()
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

        List<DataRecord> tickDataRecords = newArrayList(
                dataRecordBuilder.setDateTime("2007-12-13T20:19:00").createDataRecord()
        );
        List<DataRecord> hourDataRecords = newArrayList(
                dataRecordBuilder.setDateTime("2007-12-13T17:19:00").createDataRecord(),
                dataRecordBuilder.setDateTime("2007-12-13T18:19:00").createDataRecord(),
                dataRecordBuilder.setDateTime("2007-12-13T19:19:00").createDataRecord()
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
