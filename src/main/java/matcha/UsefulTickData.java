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

    public static class Builder {

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

        public Builder setCandleDate(LocalDateTime candleDate) {
            this.candleDate = candleDate;
            return this;
        }

        public Builder setCandleDate(String candleDate) {
            return setCandleDate(LocalDateTime.parse(candleDate, formatter));
        }

        public Builder setCandleClose(double candleClose) {
            this.candleClose = candleClose;
            return this;
        }

        public Builder setCandleLow(double candleLow) {
            this.candleLow = candleLow;
            return this;
        }

        public Builder setPreviousCandleLow(double previousCandleLow) {
            this.previousCandleLow = previousCandleLow;
            return this;
        }

        public Builder setCandleHigh(double candleHigh) {
            this.candleHigh = candleHigh;
            return this;
        }

        public Builder setPreviousCandleHigh(double previousCandleHigh) {
            this.previousCandleHigh = previousCandleHigh;
            return this;
        }

        public Builder setTakeOutYesterdaysLow(boolean takeOutYesterdaysLow) {
            this.takeOutYesterdaysLow = takeOutYesterdaysLow;
            return this;
        }

        public Builder setClosePositive(boolean closePositive) {
            this.closePositive = closePositive;
            return this;
        }

        public Builder setCloseAboveYesterdaysLow(boolean closeAboveYesterdaysLow) {
            this.closeAboveYesterdaysLow = closeAboveYesterdaysLow;
            return this;
        }

        public Builder setOpenAboveYesterdaysLow(boolean openAboveYesterdaysLow) {
            this.openAboveYesterdaysLow = openAboveYesterdaysLow;
            return this;
        }

        public Builder setTakeOutYesterdaysHigh(boolean takeOutYesterdaysHigh) {
            this.takeOutYesterdaysHigh = takeOutYesterdaysHigh;
            return this;
        }

        public Builder setCloseNegative(boolean closeNegative) {
            this.closeNegative = closeNegative;
            return this;
        }

        public Builder setCloseBelowYesterdaysHigh(boolean closeBelowYesterdaysHigh) {
            this.closeBelowYesterdaysHigh = closeBelowYesterdaysHigh;
            return this;
        }

        public Builder setOpenBelowYesterdaysHigh(boolean openBelowYesterdaysHigh) {
            this.openBelowYesterdaysHigh = openBelowYesterdaysHigh;
            return this;
        }

        public Builder setTodaysLow(double todaysLow) {
            this.todaysLow = todaysLow;
            return this;
        }

        public Builder setTodaysHigh(double todaysHigh) {
            this.todaysHigh = todaysHigh;
            return this;
        }

        public Builder setLowOfDayForPreviousHour(double lowOfDayForPreviousHour) {
            this.lowOfDayForPreviousHour = lowOfDayForPreviousHour;
            return this;
        }

        public Builder setHighOfDayForPreviousHour(double highOfDayForPreviousHour) {
            this.highOfDayForPreviousHour = highOfDayForPreviousHour;
            return this;
        }

        public Builder setTickLow(double tickLow) {
            this.tickLow = tickLow;
            return this;
        }

        public Builder setTickHigh(double tickHigh) {
            this.tickHigh = tickHigh;
            return this;
        }

        public UsefulTickData createUsefulTickData() {
            return new UsefulTickData(candleDate, candleClose, candleLow, previousCandleLow, candleHigh,
                    previousCandleHigh, takeOutYesterdaysLow, closePositive, closeAboveYesterdaysLow,
                    openAboveYesterdaysLow, takeOutYesterdaysHigh, closeNegative, closeBelowYesterdaysHigh,
                    openBelowYesterdaysHigh, todaysLow, todaysHigh, lowOfDayForPreviousHour, highOfDayForPreviousHour,
                    tickLow, tickHigh);
        }
    }
}
