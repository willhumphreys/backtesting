package matcha;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

class TickDataFactory {
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
        BigDecimal candleClose = hourData.get(hourCounter).getClose();
        BigDecimal candleOpen = hourData.get(hourCounter).getOpen();

        BigDecimal candleLow = hourData.get(hourCounter).getLow();

        BigDecimal tickLow = tickData.get(tickCounter).getLow();
        BigDecimal tickHigh = tickData.get(tickCounter).getHigh();
        BigDecimal previousCandleLow = hourData.get(hourCounter - 1).getLow();
        BigDecimal candleHigh = hourData.get(hourCounter).getHigh();

        final BigDecimal topBollingerBand = hourData.get(hourCounter).getTopBollingerBand();
        final BigDecimal movingAverage = hourData.get(hourCounter).getMovingAverage();
        final BigDecimal bottomBollingerBand = hourData.get(hourCounter).getBottomBollingerBand();

        BigDecimal previousCandleHigh = hourData.get(hourCounter - 1).getHigh();
        BigDecimal yesterdaysLow = hourData.get(hourCounter).getYesterdaysDailyLow();
        BigDecimal yesterdaysHigh = hourData.get(hourCounter).getYesterdaysDailyHigh();
        BigDecimal todaysLow = hourData.get(hourCounter).getTodaysLow();
        BigDecimal todaysHigh = hourData.get(hourCounter).getTodaysHigh();
        BigDecimal lowOfDayForPreviousHour = hourData.get(hourCounter - 1).getTodaysLow();
        BigDecimal highOfDayForPreviousHour = hourData.get(hourCounter - 1).getTodaysHigh();

        boolean takeOutYesterdaysLow = candleLow.compareTo(yesterdaysLow) < 0;
        boolean closePositive = candleClose.compareTo(candleOpen) > 0;
        boolean closeAboveYesterdaysLow = candleClose.compareTo(yesterdaysLow) > 0;
        boolean openAboveYesterdaysLow = candleOpen.compareTo(yesterdaysLow) > 0;

        boolean takeOutYesterdaysHigh = candleHigh.compareTo(yesterdaysHigh) > 0;
        boolean closeNegative = candleClose.compareTo(candleOpen) < 0;
        boolean closeBelowYesterdaysHigh = candleClose.compareTo(yesterdaysHigh) < 0;
        boolean openBelowYesterdaysHigh = candleOpen.compareTo(yesterdaysHigh) < 0;

        final boolean closeAboveTopBand = candleClose.compareTo(topBollingerBand) > 0;
        final boolean closeBelowBand = candleClose.compareTo(bottomBollingerBand) < 0;
        final boolean closeAboveMovingAverage = candleClose.compareTo(movingAverage) > 0;
        final boolean closeBelowMovingAverage = candleClose.compareTo(movingAverage) < 0;

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
                .setTopBollingerBand(topBollingerBand)
                .setMovingAverage(movingAverage)
                .setBottomBollingerBand(bottomBollingerBand)
                .setCloseAboveTopBand(closeAboveTopBand)
                .setCloseBelowBottomBand(closeBelowBand)
                .setCloseAboveMovingAverage(closeAboveMovingAverage)
                .setCloseBelowMovingAverage(closeBelowMovingAverage)
                .createUsefulTickData();
    }
}
