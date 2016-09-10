package matcha;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Double.parseDouble;

class UsefulTickData {

    private String candleDate;
    private double candleClose;
    private double candleLow;
    private double previousCandleLow;
    private double candleHigh;
    private double previousCandleHigh;
    private boolean takeOutYesterdaysLow;
    private boolean closePositive;
    private boolean closeAboveYesterdaysLow;
    private boolean openAboveYesterdaysLow;
    private boolean takeOutYesterdaysHigh;
    private boolean closeNegative;
    private boolean closeBelowYesterdaysHigh;
    private boolean openBelowYesterdaysHigh;
    private double todaysLow;
    private double todaysHigh;

    private double lowOfDayForPreviousHour;
    private double highOfDayForPreviousHour;

    private double tickLow;
    private double tickHigh;

    UsefulTickData(List<DataRecord> hourData, int hourCounter, List<DataRecord> tickData, int tickCounter) {

        if(hourCounter < 1) {
            throw new IllegalStateException("Hour counter is: " + hourCounter + ". " +
                    "The hour counter must be at least one so we can get the previous hour.");
        }

        candleDate = hourData.get(hourCounter).getDateTime();
        candleClose = parseDouble(hourData.get(hourCounter).getClose());
        double candleOpen = parseDouble(hourData.get(hourCounter).getOpen());
        candleLow = parseDouble(hourData.get(hourCounter).getLow());
        tickLow = parseDouble(tickData.get(tickCounter).getLow());
        tickHigh = parseDouble(tickData.get(tickCounter).getHigh());
        previousCandleLow = parseDouble(hourData.get(hourCounter - 1).getLow());
        candleHigh = parseDouble(hourData.get(hourCounter).getHigh());
        previousCandleHigh = parseDouble(hourData.get(hourCounter - 1).getHigh());
        double yesterdaysLow = parseDouble(hourData.get(hourCounter).getYesterdaysDailyLow());
        double yesterdaysHigh = parseDouble(hourData.get(hourCounter).getYesterdaysDailyHigh());
        todaysLow = parseDouble(hourData.get(hourCounter).getTodaysLow());
        todaysHigh = parseDouble(hourData.get(hourCounter).getTodaysHigh());
        lowOfDayForPreviousHour = parseDouble(hourData.get(hourCounter - 1).getTodaysLow());
        highOfDayForPreviousHour = parseDouble(hourData.get(hourCounter - 1).getTodaysHigh());

        takeOutYesterdaysLow = candleLow < yesterdaysLow;
        closePositive = candleClose > candleOpen;
        closeAboveYesterdaysLow = candleClose > yesterdaysLow;
        openAboveYesterdaysLow = candleOpen > yesterdaysLow;

        takeOutYesterdaysHigh = candleHigh > yesterdaysHigh;
        closeNegative = candleClose < candleOpen;
        closeBelowYesterdaysHigh = candleClose < yesterdaysHigh;
        openBelowYesterdaysHigh = candleOpen < yesterdaysHigh;
    }

    LocalDateTime getCandleDate() {
        return LocalDateTime.parse(candleDate);
    }

    double getCandleClose() {
        return candleClose;
    }

    double getCandleLow() {
        return candleLow;
    }

    double getPreviousCandleLow() {
        return previousCandleLow;
    }

    double getCandleHigh() {
        return candleHigh;
    }

    double getPreviousCandleHigh() {
        return previousCandleHigh;
    }

    double getTodaysLow() {
        return todaysLow;
    }

    double getTodaysHigh() {
        return todaysHigh;
    }

    double getLowOfDayForPreviousHour() {
        return lowOfDayForPreviousHour;
    }

    double getHighOfDayForPreviousHour() {
        return highOfDayForPreviousHour;
    }

    boolean isTakeOutYesterdaysLow() {
        return takeOutYesterdaysLow;
    }

    boolean isClosePositive() {
        return closePositive;
    }

    boolean isCloseAboveYesterdaysLow() {
        return closeAboveYesterdaysLow;
    }

    boolean isOpenAboveYesterdaysLow() {
        return openAboveYesterdaysLow;
    }

    boolean isTakeOutYesterdaysHigh() {
        return takeOutYesterdaysHigh;
    }

    boolean isCloseNegative() {
        return closeNegative;
    }

    boolean isCloseBelowYesterdaysHigh() {
        return closeBelowYesterdaysHigh;
    }

    boolean isOpenBelowYesterdaysHigh() {
        return openBelowYesterdaysHigh;
    }

    double getTickHigh() {
        return tickHigh;
    }

    double getTickLow() {
        return tickLow;
    }
}
