package matcha;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private UsefulTickData(LocalDateTime candleDate, double candleClose, double candleLow, double previousCandleLow,
                           double candleHigh, double previousCandleHigh, boolean takeOutYesterdaysLow, boolean
                                   closePositive,
                           boolean closeAboveYesterdaysLow, boolean openAboveYesterdaysLow, boolean
                                   takeOutYesterdaysHigh,
                           boolean closeNegative, boolean closeBelowYesterdaysHigh, boolean openBelowYesterdaysHigh,
                           double todaysLow, double todaysHigh, double lowOfDayForPreviousHour, double
                                   highOfDayForPreviousHour,
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

    static class Builder {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");

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

        Builder setCandleDate(LocalDateTime candleDate) {
            this.candleDate = candleDate;
            return this;
        }

        Builder setCandleDate(String candleDate) {
            return setCandleDate(LocalDateTime.parse(candleDate, formatter));
        }

        Builder setCandleClose(double candleClose) {
            this.candleClose = candleClose;
            return this;
        }

        Builder setCandleLow(double candleLow) {
            this.candleLow = candleLow;
            return this;
        }

        Builder setPreviousCandleLow(double previousCandleLow) {
            this.previousCandleLow = previousCandleLow;
            return this;
        }

        Builder setCandleHigh(double candleHigh) {
            this.candleHigh = candleHigh;
            return this;
        }

        Builder setPreviousCandleHigh(double previousCandleHigh) {
            this.previousCandleHigh = previousCandleHigh;
            return this;
        }

        Builder setTakeOutYesterdaysLow(boolean takeOutYesterdaysLow) {
            this.takeOutYesterdaysLow = takeOutYesterdaysLow;
            return this;
        }

        Builder setClosePositive(boolean closePositive) {
            this.closePositive = closePositive;
            return this;
        }

        Builder setCloseAboveYesterdaysLow(boolean closeAboveYesterdaysLow) {
            this.closeAboveYesterdaysLow = closeAboveYesterdaysLow;
            return this;
        }

        Builder setOpenAboveYesterdaysLow(boolean openAboveYesterdaysLow) {
            this.openAboveYesterdaysLow = openAboveYesterdaysLow;
            return this;
        }

        Builder setTakeOutYesterdaysHigh(boolean takeOutYesterdaysHigh) {
            this.takeOutYesterdaysHigh = takeOutYesterdaysHigh;
            return this;
        }

        Builder setCloseNegative(boolean closeNegative) {
            this.closeNegative = closeNegative;
            return this;
        }

        Builder setCloseBelowYesterdaysHigh(boolean closeBelowYesterdaysHigh) {
            this.closeBelowYesterdaysHigh = closeBelowYesterdaysHigh;
            return this;
        }

        Builder setOpenBelowYesterdaysHigh(boolean openBelowYesterdaysHigh) {
            this.openBelowYesterdaysHigh = openBelowYesterdaysHigh;
            return this;
        }

        Builder setTodaysLow(double todaysLow) {
            this.todaysLow = todaysLow;
            return this;
        }

        Builder setTodaysHigh(double todaysHigh) {
            this.todaysHigh = todaysHigh;
            return this;
        }

        Builder setLowOfDayForPreviousHour(double lowOfDayForPreviousHour) {
            this.lowOfDayForPreviousHour = lowOfDayForPreviousHour;
            return this;
        }

        Builder setHighOfDayForPreviousHour(double highOfDayForPreviousHour) {
            this.highOfDayForPreviousHour = highOfDayForPreviousHour;
            return this;
        }

        Builder setTickLow(double tickLow) {
            this.tickLow = tickLow;
            return this;
        }

        Builder setTickHigh(double tickHigh) {
            this.tickHigh = tickHigh;
            return this;
        }

        UsefulTickData createUsefulTickData() {
            return new UsefulTickData(candleDate, candleClose, candleLow, previousCandleLow, candleHigh,
                    previousCandleHigh, takeOutYesterdaysLow, closePositive, closeAboveYesterdaysLow,
                    openAboveYesterdaysLow, takeOutYesterdaysHigh, closeNegative, closeBelowYesterdaysHigh,
                    openBelowYesterdaysHigh, todaysLow, todaysHigh, lowOfDayForPreviousHour, highOfDayForPreviousHour,
                    tickLow, tickHigh);
        }
    }
}
