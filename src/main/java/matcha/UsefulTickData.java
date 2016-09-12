package matcha;

import java.time.LocalDateTime;

class UsefulTickData {

    private LocalDateTime candleDate;
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

    UsefulTickData(LocalDateTime candleDate, double candleClose, double candleLow, double previousCandleLow,
                   double candleHigh, double previousCandleHigh, boolean takeOutYesterdaysLow, boolean closePositive,
                   boolean closeAboveYesterdaysLow, boolean openAboveYesterdaysLow, boolean takeOutYesterdaysHigh,
                   boolean closeNegative, boolean closeBelowYesterdaysHigh, boolean openBelowYesterdaysHigh,
                   double todaysLow, double todaysHigh, double lowOfDayForPreviousHour, double highOfDayForPreviousHour,
                   double tickLow, double tickHigh) {
        this.candleDate = candleDate;
        this.candleClose = candleClose;
        this.candleLow = candleLow;
        this.previousCandleLow = previousCandleLow;
        this.candleHigh = candleHigh;
        this.previousCandleHigh = previousCandleHigh;
        this.takeOutYesterdaysLow = takeOutYesterdaysLow;
        this.closePositive = closePositive;
        this.closeAboveYesterdaysLow = closeAboveYesterdaysLow;
        this.openAboveYesterdaysLow = openAboveYesterdaysLow;
        this.takeOutYesterdaysHigh = takeOutYesterdaysHigh;
        this.closeNegative = closeNegative;
        this.closeBelowYesterdaysHigh = closeBelowYesterdaysHigh;
        this.openBelowYesterdaysHigh = openBelowYesterdaysHigh;
        this.todaysLow = todaysLow;
        this.todaysHigh = todaysHigh;
        this.lowOfDayForPreviousHour = lowOfDayForPreviousHour;
        this.highOfDayForPreviousHour = highOfDayForPreviousHour;
        this.tickLow = tickLow;
        this.tickHigh = tickHigh;
    }

    LocalDateTime getCandleDate() {
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
