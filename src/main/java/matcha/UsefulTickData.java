package matcha;

import java.time.LocalDateTime;

import static java.lang.Double.parseDouble;

class UsefulTickData {

    private static final int DATE = 0;
    private static final int OPEN = 1;
    private static final int LOW = 2;
    private static final int HIGH = 3;
    private static final int CLOSE = 4;
    private static final int YESTERDAYS_DAILY_LOW = 5;
    private static final int YESTERDAYS_DAILY_HIGH = 6;
    private static final int TODAYS_LOW = 7;
    private static final int TODAYS_HIGH = 8;
    private static final int LAST_4_DAYS_DOWN = 9;
    private static final int LAST_4_DAYS_UP = 10;

    private String[][] hourData;
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

    UsefulTickData(String[][] hourData, int hourCounter) {
        this.hourData = hourData;
        this.hourCounter = hourCounter;
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

    UsefulTickData invoke() {

        try {
            candleDate = hourData[hourCounter][DATE];
        } catch (Exception e) {
            e.printStackTrace();
        }
        candleClose = parseDouble(hourData[hourCounter][CLOSE]);
        double candleOpen = parseDouble(hourData[hourCounter][OPEN]);
        candleLow = parseDouble(hourData[hourCounter][LOW]);
        previousCandleLow = parseDouble(hourData[hourCounter - 1][LOW]);
        candleHigh = parseDouble(hourData[hourCounter][HIGH]);
        previousCandleHigh = parseDouble(hourData[hourCounter - 1][HIGH]);
        double yesterdaysLow = parseDouble(hourData[hourCounter][YESTERDAYS_DAILY_LOW]);
        double yesterdaysHigh = parseDouble(hourData[hourCounter][YESTERDAYS_DAILY_HIGH]);
        todaysLow = parseDouble(hourData[hourCounter][TODAYS_LOW]);
        todaysHigh = parseDouble(hourData[hourCounter][TODAYS_HIGH]);
        lowOfYesterday = parseDouble(hourData[hourCounter - 1][TODAYS_LOW]);
        highOfYesterday = parseDouble(hourData[hourCounter - 1][TODAYS_HIGH]);

        //TODO Put back last 4 days down.
//        last4DaysDown = parseDouble(hourData[hourCounter][LAST_4_DAYS_DOWN]) > 0;
//        last4DaysUp = parseDouble(hourData[hourCounter][LAST_4_DAYS_UP]) > 0;

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
