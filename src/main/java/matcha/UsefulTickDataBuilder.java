package matcha;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UsefulTickDataBuilder {

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

    public UsefulTickDataBuilder setCandleDate(LocalDateTime candleDate) {
        this.candleDate = candleDate;
        return this;
    }

    public UsefulTickDataBuilder setCandleDate(String candleDate) {
        return setCandleDate(LocalDateTime.parse(candleDate, formatter));
    }

    public UsefulTickDataBuilder setCandleClose(double candleClose) {
        this.candleClose = candleClose;
        return this;
    }

    public UsefulTickDataBuilder setCandleLow(double candleLow) {
        this.candleLow = candleLow;
        return this;
    }

    public UsefulTickDataBuilder setPreviousCandleLow(double previousCandleLow) {
        this.previousCandleLow = previousCandleLow;
        return this;
    }

    public UsefulTickDataBuilder setCandleHigh(double candleHigh) {
        this.candleHigh = candleHigh;
        return this;
    }

    public UsefulTickDataBuilder setPreviousCandleHigh(double previousCandleHigh) {
        this.previousCandleHigh = previousCandleHigh;
        return this;
    }

    public UsefulTickDataBuilder setTakeOutYesterdaysLow(boolean takeOutYesterdaysLow) {
        this.takeOutYesterdaysLow = takeOutYesterdaysLow;
        return this;
    }

    public UsefulTickDataBuilder setClosePositive(boolean closePositive) {
        this.closePositive = closePositive;
        return this;
    }

    public UsefulTickDataBuilder setCloseAboveYesterdaysLow(boolean closeAboveYesterdaysLow) {
        this.closeAboveYesterdaysLow = closeAboveYesterdaysLow;
        return this;
    }

    public UsefulTickDataBuilder setOpenAboveYesterdaysLow(boolean openAboveYesterdaysLow) {
        this.openAboveYesterdaysLow = openAboveYesterdaysLow;
        return this;
    }

    public UsefulTickDataBuilder setTakeOutYesterdaysHigh(boolean takeOutYesterdaysHigh) {
        this.takeOutYesterdaysHigh = takeOutYesterdaysHigh;
        return this;
    }

    public UsefulTickDataBuilder setCloseNegative(boolean closeNegative) {
        this.closeNegative = closeNegative;
        return this;
    }

    public UsefulTickDataBuilder setCloseBelowYesterdaysHigh(boolean closeBelowYesterdaysHigh) {
        this.closeBelowYesterdaysHigh = closeBelowYesterdaysHigh;
        return this;
    }

    public UsefulTickDataBuilder setOpenBelowYesterdaysHigh(boolean openBelowYesterdaysHigh) {
        this.openBelowYesterdaysHigh = openBelowYesterdaysHigh;
        return this;
    }

    public UsefulTickDataBuilder setTodaysLow(double todaysLow) {
        this.todaysLow = todaysLow;
        return this;
    }

    public UsefulTickDataBuilder setTodaysHigh(double todaysHigh) {
        this.todaysHigh = todaysHigh;
        return this;
    }

    public UsefulTickDataBuilder setLowOfDayForPreviousHour(double lowOfDayForPreviousHour) {
        this.lowOfDayForPreviousHour = lowOfDayForPreviousHour;
        return this;
    }

    public UsefulTickDataBuilder setHighOfDayForPreviousHour(double highOfDayForPreviousHour) {
        this.highOfDayForPreviousHour = highOfDayForPreviousHour;
        return this;
    }

    public UsefulTickDataBuilder setTickLow(double tickLow) {
        this.tickLow = tickLow;
        return this;
    }

    public UsefulTickDataBuilder setTickHigh(double tickHigh) {
        this.tickHigh = tickHigh;
        return this;
    }

    public UsefulTickData createUsefulTickData() {
        return new UsefulTickData(candleDate, candleClose, candleLow, previousCandleLow, candleHigh, previousCandleHigh, takeOutYesterdaysLow, closePositive, closeAboveYesterdaysLow, openAboveYesterdaysLow, takeOutYesterdaysHigh, closeNegative, closeBelowYesterdaysHigh, openBelowYesterdaysHigh, todaysLow, todaysHigh, lowOfDayForPreviousHour, highOfDayForPreviousHour, tickLow, tickHigh);
    }
}