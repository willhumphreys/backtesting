package matcha;

import java.time.LocalDateTime;

class SyncTicks {

    private static final int DATE = 0;

    int updateHourCounterToMatchMinuteCounter(String[][] tickData, String[][] hourData, int hourCounter, int i, int hourCandleHour, int tickCandleHour) {
        if (tickCandleHour != hourCandleHour) {
            hourCounter++;

            if(!checkHourMatchesTick(tickData[i], hourData[hourCounter])) {
                hourCounter++;
            }

            final LocalDateTime currentHourTime2 = LocalDateTime.parse(hourData[hourCounter][DATE]);
            final LocalDateTime currentMinuteTime2 = LocalDateTime.parse(tickData[i][DATE]);

            if (currentHourTime2.getHour() != currentMinuteTime2.getHour()) {
                throw new IllegalStateException("minutes not matching hours.");
            }
        }
        return hourCounter;
    }

    private boolean checkHourMatchesTick(String[] strings, String[] strings1) {
        final LocalDateTime currentHourTime = LocalDateTime.parse(strings1[DATE]);
        final LocalDateTime currentMinuteTime = LocalDateTime.parse(strings[DATE]);

        return currentHourTime.getHour() == currentMinuteTime.getHour();

    }
}
