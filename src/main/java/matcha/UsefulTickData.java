package matcha;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class UsefulTickData {

    private final LocalDateTime candleDate;
    private final BigDecimal candleClose;
    private final BigDecimal candleLow;
    private final BigDecimal previousCandleLow;
    private final BigDecimal candleHigh;
    private final BigDecimal previousCandleHigh;
    private final boolean takeOutYesterdaysLow;
    private final boolean closePositive;
    private final boolean closeAboveYesterdaysLow;
    private final boolean openAboveYesterdaysLow;
    private final boolean takeOutYesterdaysHigh;
    private final boolean closeNegative;
    private final boolean closeBelowYesterdaysHigh;
    private final boolean openBelowYesterdaysHigh;
    private final BigDecimal todaysLow;
    private final BigDecimal todaysHigh;
    private final BigDecimal lowOfDayForPreviousHour;
    private final BigDecimal highOfDayForPreviousHour;
    private final BigDecimal tickLow;
    private final BigDecimal tickHigh;

    private UsefulTickData(LocalDateTime candleDate, BigDecimal candleClose, BigDecimal candleLow, BigDecimal previousCandleLow,
                           BigDecimal candleHigh, BigDecimal previousCandleHigh, boolean takeOutYesterdaysLow,
                           boolean closePositive, boolean closeAboveYesterdaysLow, boolean openAboveYesterdaysLow,
                           boolean takeOutYesterdaysHigh, boolean closeNegative, boolean closeBelowYesterdaysHigh,
                           boolean openBelowYesterdaysHigh, BigDecimal todaysLow, BigDecimal todaysHigh,
                           BigDecimal lowOfDayForPreviousHour, BigDecimal highOfDayForPreviousHour, BigDecimal tickLow,
                           BigDecimal tickHigh) {
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

    BigDecimal getCandleClose() {
        return candleClose;
    }

    BigDecimal getCandleLow() {
        return candleLow;
    }

    BigDecimal getPreviousCandleLow() {
        return previousCandleLow;
    }

    BigDecimal getCandleHigh() {
        return candleHigh;
    }

    BigDecimal getPreviousCandleHigh() {
        return previousCandleHigh;
    }

    BigDecimal getTodaysLow() {
        return todaysLow;
    }

    BigDecimal getTodaysHigh() {
        return todaysHigh;
    }

    BigDecimal getLowOfDayForPreviousHour() {
        return lowOfDayForPreviousHour;
    }

    BigDecimal getHighOfDayForPreviousHour() {
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

    BigDecimal getTickHigh() {
        return tickHigh;
    }

    BigDecimal getTickLow() {
        return tickLow;
    }

    static class Builder {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");

        private LocalDateTime candleDate;
        private BigDecimal candleClose;
        private BigDecimal candleLow;
        private BigDecimal previousCandleLow;
        private BigDecimal candleHigh;
        private BigDecimal previousCandleHigh;
        private boolean takeOutYesterdaysLow;
        private boolean closePositive;
        private boolean closeAboveYesterdaysLow;
        private boolean openAboveYesterdaysLow;
        private boolean takeOutYesterdaysHigh;
        private boolean closeNegative;
        private boolean closeBelowYesterdaysHigh;
        private boolean openBelowYesterdaysHigh;
        private BigDecimal todaysLow;
        private BigDecimal todaysHigh;
        private BigDecimal lowOfDayForPreviousHour;
        private BigDecimal highOfDayForPreviousHour;
        private BigDecimal tickLow;
        private BigDecimal tickHigh;
        private BigDecimal topBollingerBand;
        private BigDecimal movingAverage;
        private BigDecimal bottomBollingerBand;
        private boolean closeAboveTopBand;
        private boolean closeBelowBand;
        private boolean closeAboveMovingAverage;
        private boolean closeBelowMovingAverage;

        Builder setCandleDate(LocalDateTime candleDate) {
            this.candleDate = candleDate;
            return this;
        }

        Builder setCandleDate(String candleDate) {
            return setCandleDate(LocalDateTime.parse(candleDate, formatter));
        }

        Builder setCandleClose(BigDecimal candleClose) {
            this.candleClose = candleClose;
            return this;
        }

        Builder setCandleLow(BigDecimal candleLow) {
            this.candleLow = candleLow;
            return this;
        }

        Builder setPreviousCandleLow(BigDecimal previousCandleLow) {
            this.previousCandleLow = previousCandleLow;
            return this;
        }

        Builder setCandleHigh(BigDecimal candleHigh) {
            this.candleHigh = candleHigh;
            return this;
        }

        Builder setPreviousCandleHigh(BigDecimal previousCandleHigh) {
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

        Builder setTodaysLow(BigDecimal todaysLow) {
            this.todaysLow = todaysLow;
            return this;
        }

        Builder setTodaysHigh(BigDecimal todaysHigh) {
            this.todaysHigh = todaysHigh;
            return this;
        }

        Builder setLowOfDayForPreviousHour(BigDecimal lowOfDayForPreviousHour) {
            this.lowOfDayForPreviousHour = lowOfDayForPreviousHour;
            return this;
        }

        Builder setHighOfDayForPreviousHour(BigDecimal highOfDayForPreviousHour) {
            this.highOfDayForPreviousHour = highOfDayForPreviousHour;
            return this;
        }

        Builder setTickLow(BigDecimal tickLow) {
            this.tickLow = tickLow;
            return this;
        }

        Builder setTickHigh(BigDecimal tickHigh) {
            this.tickHigh = tickHigh;
            return this;
        }

        Builder setTopBollingerBand(BigDecimal topBollingerBand) {
            this.topBollingerBand = topBollingerBand;
            return this;
        }

        Builder setMovingAverage(BigDecimal movingAverage) {
            this.movingAverage = movingAverage;
            return this;
        }

        Builder setBottomBollingerBand(BigDecimal bottomBollingerBand) {
            this.bottomBollingerBand = bottomBollingerBand;
            return this;
        }

        Builder setCloseAboveTopBand(boolean closeAboveTopBand) {
            this.closeAboveTopBand = closeAboveTopBand;
            return this;
        }

        Builder setCloseBelowBand(boolean closeBelowBand) {
            this.closeBelowBand = closeBelowBand;
            return this;
        }

        Builder setCloseAboveMovingAverage(boolean closeAboveMovingAverage) {
            this.closeAboveMovingAverage = closeAboveMovingAverage;
            return this;
        }

        Builder setCloseBelowMovingAverage(boolean closeBelowMovingAverage) {
            this.closeBelowMovingAverage = closeBelowMovingAverage;
            return this;
        }

        UsefulTickData createUsefulTickData() {

            if(closePositive && closeNegative) {
                throw new IllegalStateException("Can't close both position and negative");
            }

            return new UsefulTickData(candleDate, candleClose, candleLow, previousCandleLow, candleHigh,
                    previousCandleHigh, takeOutYesterdaysLow, closePositive, closeAboveYesterdaysLow,
                    openAboveYesterdaysLow, takeOutYesterdaysHigh, closeNegative, closeBelowYesterdaysHigh,
                    openBelowYesterdaysHigh, todaysLow, todaysHigh, lowOfDayForPreviousHour, highOfDayForPreviousHour,
                    tickLow, tickHigh);
        }
    }
}
