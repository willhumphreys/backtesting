package matcha;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

public class TickDataFactory {
    UsefulTickData buildTickData(List<DataRecord> hourData, int hourCounter, List<DataRecord> tickData, int tickCounter) {
        if(hourCounter < 1) {
            throw new IllegalStateException(format("Hour counter is: %d. The hour counter must be at least one" +
                    " so we can get the previous hour.", hourCounter));
        }

        if(hourCounter >= hourData.size()) {
            throw new IllegalStateException(format("The hour counter references hourData that does not exist. " +
                    "hourCounter: %d hourData size: %d", hourCounter, hourData.size()));
        }

        LocalDateTime candleDate = hourData.get(hourCounter).getDateTime();
        double candleClose = hourData.get(hourCounter).getClose();
        double candleOpen = hourData.get(hourCounter).getOpen();

        double candleLow = hourData.get(hourCounter).getLow();

        double tickLow = tickData.get(tickCounter).getLow();
        double tickHigh = tickData.get(tickCounter).getHigh();
        double previousCandleLow = hourData.get(hourCounter - 1).getLow();
        double candleHigh = hourData.get(hourCounter).getHigh();

        double previousCandleHigh = hourData.get(hourCounter - 1).getHigh();
        double yesterdaysLow = hourData.get(hourCounter).getYesterdaysDailyLow();
        double yesterdaysHigh = hourData.get(hourCounter).getYesterdaysDailyHigh();
        double todaysLow = hourData.get(hourCounter).getTodaysLow();
        double todaysHigh = hourData.get(hourCounter).getTodaysHigh();
        double lowOfDayForPreviousHour = hourData.get(hourCounter - 1).getTodaysLow();
        double highOfDayForPreviousHour = hourData.get(hourCounter - 1).getTodaysHigh();

        boolean takeOutYesterdaysLow = candleLow < yesterdaysLow;
        boolean closePositive = candleClose > candleOpen;
        boolean closeAboveYesterdaysLow = candleClose > yesterdaysLow;
        boolean openAboveYesterdaysLow = candleOpen > yesterdaysLow;

        boolean takeOutYesterdaysHigh = candleHigh > yesterdaysHigh;
        boolean closeNegative = candleClose < candleOpen;
        boolean closeBelowYesterdaysHigh = candleClose < yesterdaysHigh;
        boolean openBelowYesterdaysHigh = candleOpen < yesterdaysHigh;

        return new UsefulTickData.Builder()
                .setCandleDate(candleDate)
                .setCandleClose(candleClose)
                .setCandleLow(candleLow)
                .setPreviousCandleLow(previousCandleLow)
                .setCandleHigh(candleHigh)
                .setPreviousCandleHigh(previousCandleHigh)
                .setTakeOutYesterdaysLow(takeOutYesterdaysLow)
                .setClosePositive(closePositive)
                .setCloseAboveYesterdaysLow(closeAboveYesterdaysLow)
                .setOpenAboveYesterdaysLow(openAboveYesterdaysLow)
                .setTakeOutYesterdaysHigh(takeOutYesterdaysHigh)
                .setCloseNegative(closeNegative)
                .setCloseBelowYesterdaysHigh(closeBelowYesterdaysHigh)
                .setOpenBelowYesterdaysHigh(openBelowYesterdaysHigh)
                .setTodaysLow(todaysLow).setTodaysHigh(todaysHigh)
                .setLowOfDayForPreviousHour(lowOfDayForPreviousHour)
                .setHighOfDayForPreviousHour(highOfDayForPreviousHour)
                .setTickLow(tickLow)
                .setTickHigh(tickHigh)
                .createUsefulTickData();
    }
}
