package matcha;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class DataRecord {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");

    private final LocalDateTime dateTime;
    private final BigDecimal open;
    private final BigDecimal low;
    private final BigDecimal high;
    private final BigDecimal close;
    private final BigDecimal yesterdaysDailyLow;
    private final BigDecimal yesterdaysDailyHigh;
    private final BigDecimal todaysLow;
    private final BigDecimal todaysHigh;

    private DataRecord(LocalDateTime dateTime, BigDecimal open, BigDecimal low, BigDecimal high, BigDecimal close,
                       BigDecimal yesterdaysDailyLow, BigDecimal yesterdaysDailyHigh, BigDecimal todaysLow,
                       BigDecimal todaysHigh) {

        this.dateTime = dateTime;
        this.open = open;
        this.low = low;
        this.high = high;
        this.close = close;
        this.yesterdaysDailyLow = yesterdaysDailyLow;
        this.yesterdaysDailyHigh = yesterdaysDailyHigh;
        this.todaysLow = todaysLow;
        this.todaysHigh = todaysHigh;
    }

    LocalDateTime getDateTime() {
        return dateTime;
    }

    BigDecimal getOpen() {
        return open;
    }

    BigDecimal getLow() {
        return low;
    }

    BigDecimal getHigh() {
        return high;
    }

    BigDecimal getClose() {
        return close;
    }

    BigDecimal getYesterdaysDailyLow() {
        return yesterdaysDailyLow;
    }

    BigDecimal getYesterdaysDailyHigh() {
        return yesterdaysDailyHigh;
    }

    BigDecimal getTodaysLow() {
        return todaysLow;
    }

    BigDecimal getTodaysHigh() {
        return todaysHigh;
    }

    static class Builder {
        private LocalDateTime dateTime;
        private BigDecimal open;
        private BigDecimal low;
        private BigDecimal high;
        private BigDecimal close;
        private BigDecimal yesterdaysDailyLow;
        private BigDecimal yesterdaysDailyHigh;
        private BigDecimal todaysLow;
        private BigDecimal todaysHigh;

        Builder setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        Builder setDateTime(String dateTime) {
            return setDateTime(LocalDateTime.parse(dateTime, formatter));
        }

        Builder setOpen(BigDecimal open) {
            this.open = open;
            return this;
        }

        Builder setLow(BigDecimal low) {
            this.low = low;
            return this;
        }

        Builder setHigh(BigDecimal high) {
            this.high = high;
            return this;
        }

        Builder setClose(BigDecimal close) {
            this.close = close;
            return this;
        }

        Builder setYesterdaysDailyLow(BigDecimal yesterdaysDailyLow) {
            this.yesterdaysDailyLow = yesterdaysDailyLow;
            return this;
        }

        Builder setYesterdaysDailyHigh(BigDecimal yesterdaysDailyHigh) {
            this.yesterdaysDailyHigh = yesterdaysDailyHigh;
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

        DataRecord createDataRecord() {
            return new DataRecord(dateTime, open, low, high, close, yesterdaysDailyLow, yesterdaysDailyHigh, todaysLow,
                    todaysHigh);
        }
    }
}
