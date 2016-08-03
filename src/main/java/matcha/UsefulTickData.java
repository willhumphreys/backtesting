package matcha;

import static java.lang.Double.parseDouble;

class UsefulTickData {

    private static final int DATE = 0;
    private static final int OPEN = 1;
    private static final int LOW = 2;
    private static final int HIGH = 3;
    private static final int CLOSE = 4;
    private static final int DAILY_LOW = 5;
    private static final int DAILY_HIGH = 6;

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

    public UsefulTickData(String[][] hourData, int hourCounter) {
        this.hourData = hourData;
        this.hourCounter = hourCounter;
    }


    public String getCandleDate() {
        return candleDate;
    }
    public double getCandleClose() {
        return candleClose;
    }

    public double getCandleLow() {
        return candleLow;
    }

    public double getPreviousCandleLow() {
        return previousCandleLow;
    }

    public double getCandleHigh() {
        return candleHigh;
    }

    public double getPreviousCandleHigh() {
        return previousCandleHigh;
    }

    public boolean isTakeOutYesterdaysLow() {
        return takeOutYesterdaysLow;
    }

    public boolean isClosePositive() {
        return closePositive;
    }

    public boolean isCloseAboveYesterdaysLow() {
        return closeAboveYesterdaysLow;
    }

    public boolean isOpenAboveYesterdaysLow() {
        return openAboveYesterdaysLow;
    }

    public boolean isTakeOutYesterdaysHigh() {
        return takeOutYesterdaysHigh;
    }

    public boolean isCloseNegative() {
        return closeNegative;
    }

    public boolean isCloseBelowYesterdaysHigh() {
        return closeBelowYesterdaysHigh;
    }

    public boolean isOpenBelowYesterdaysLow() {
        return openBelowYesterdaysLow;
    }

    public UsefulTickData invoke() {
        candleDate = hourData[hourCounter][DATE];
        candleClose = parseDouble(hourData[hourCounter][CLOSE]);
        double candleOpen = parseDouble(hourData[hourCounter][OPEN]);
        candleLow = parseDouble(hourData[hourCounter][LOW]);
        previousCandleLow = parseDouble(hourData[hourCounter - 1][LOW]);
        candleHigh = parseDouble(hourData[hourCounter][HIGH]);
        previousCandleHigh = parseDouble(hourData[hourCounter - 1][HIGH]);
        double yesterdaysLow = parseDouble(hourData[hourCounter][DAILY_LOW]);
        double yesterdaysHigh = parseDouble(hourData[hourCounter][DAILY_HIGH]);


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
