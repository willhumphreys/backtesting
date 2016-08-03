package matcha;

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
    private boolean openBelowYesterdaysLow;
    private double todaysLow;
    private double todaysHigh;

    private double lowOfYesterday;
    private double highOfYesterday;

    UsefulTickData(String[][] hourData, int hourCounter) {
        this.hourData = hourData;
        this.hourCounter = hourCounter;
    }

    String getCandleDate() {
        return candleDate;
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
        return lowOfYesterday;
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
    boolean isOpenBelowYesterdaysLow() {
        return openBelowYesterdaysLow;
    }

    UsefulTickData invoke() {
        candleDate = hourData[hourCounter][DATE];
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


        takeOutYesterdaysLow = candleLow < yesterdaysLow;
        closePositive = candleClose > candleOpen;
        closeAboveYesterdaysLow = candleClose > yesterdaysLow;
        openAboveYesterdaysLow = candleOpen > yesterdaysLow;

        takeOutYesterdaysHigh = candleHigh > yesterdaysHigh;
        closeNegative = candleClose < candleOpen;
        closeBelowYesterdaysHigh = candleClose < yesterdaysHigh;
        openBelowYesterdaysLow = candleOpen < yesterdaysHigh;
        return this;
    }
}
