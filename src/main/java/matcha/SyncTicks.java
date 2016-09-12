package matcha;

import java.time.LocalDateTime;
import java.util.List;

class SyncTicks {

    int updateHourCounterToMatchMinuteCounter(List<DataRecord> tickData, List<DataRecord> hourData, int hourCounter,
                                              int tickCounter, int hourCandleHour, int tickCandleHour) {
        if (tickCandleHour != hourCandleHour) {
            hourCounter++;

            if(!checkHourMatchesTick(tickData.get(tickCounter), hourData.get(hourCounter))) {
                hourCounter++;
            }

            final LocalDateTime currentHourTime2 = hourData.get(hourCounter).getDateTime();
            final LocalDateTime currentMinuteTime2 = tickData.get(tickCounter).getDateTime();

            if (currentHourTime2.getHour() != currentMinuteTime2.getHour()) {
                throw new IllegalStateException("minutes not matching hours.");
            }
        }
        return hourCounter;
    }

    private boolean checkHourMatchesTick(DataRecord tickData, DataRecord hourData) {
        final LocalDateTime currentHourTime = hourData.getDateTime();
        final LocalDateTime currentMinuteTime = tickData.getDateTime();

        return currentHourTime.getHour() == currentMinuteTime.getHour();

    }
}
