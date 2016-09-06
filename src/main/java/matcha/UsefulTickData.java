package matcha;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Double.parseDouble;

class UsefulTickData {

    private final int tickCounter;
    private final List<DataRecord> tickData;

    private List<DataRecord> hourData;
    private int hourCounter;
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

    private double lowOfYesterday;
    private double highOfYesterday;

    private boolean last4DaysDown;
    private boolean last4DaysUp;
    private double tickLow;
    private double tickHigh;

    UsefulTickData(List<DataRecord> hourData, int hourCounter, List<DataRecord> tickData, int tickCounter) {
        this.hourData = hourData;
        this.hourCounter = hourCounter;
        this.tickData = tickData;
        this.tickCounter = tickCounter;
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
    double getLowOfYesterday() {
        return lowOfYesterday;
    }
    double getHighOfYesterday() {
        return highOfYesterday;
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

    boolean isLast4DaysDown() {
        return last4DaysDown;
    }
    boolean isLast4DaysUp() {
        return last4DaysUp;
    }

    double getTickHigh() {
        return tickHigh;
    }

    double getTickLow() {
        return tickLow;
    }

    UsefulTickData invoke() {

        try {
            candleDate = hourData.get(hourCounter).getDateTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        lowOfYesterday = parseDouble(hourData.get(hourCounter - 1).getTodaysLow());
        highOfYesterday = parseDouble(hourData.get(hourCounter - 1).getTodaysHigh());

//        //TODO Put back last 4 days down.
//        last4DaysDown = parseDouble(hourData.get(hourCounter).get][LAST_4_DAYS_DOWN]) > 0;
//        last4DaysUp = parseDouble(hourData.get(hourCounter][LAST_4_DAYS_UP]) > 0;

        takeOutYesterdaysLow = candleLow < yesterdaysLow;
        closePositive = candleClose > candleOpen;
        closeAboveYesterdaysLow = candleClose > yesterdaysLow;
        openAboveYesterdaysLow = candleOpen > yesterdaysLow;

        takeOutYesterdaysHigh = candleHigh > yesterdaysHigh;
        closeNegative = candleClose < candleOpen;
        closeBelowYesterdaysHigh = candleClose < yesterdaysHigh;
        openBelowYesterdaysHigh = candleOpen < yesterdaysHigh;
        return this;
    }
}
