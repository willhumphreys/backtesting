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

            final LocalDateTime currentHourTime2 = LocalDateTime.parse(hourData.get(hourCounter).getDateTime());
            final LocalDateTime currentMinuteTime2 = LocalDateTime.parse(tickData.get(tickCounter).getDateTime());

            if (currentHourTime2.getHour() != currentMinuteTime2.getHour()) {
                throw new IllegalStateException("minutes not matching hours.");
            }
        }
        return hourCounter;
    }

    private boolean checkHourMatchesTick(DataRecord tickData, DataRecord hourData) {
        final LocalDateTime currentHourTime = LocalDateTime.parse(hourData.getDateTime());
        final LocalDateTime currentMinuteTime = LocalDateTime.parse(tickData.getDateTime());

        return currentHourTime.getHour() == currentMinuteTime.getHour();

    }
}
