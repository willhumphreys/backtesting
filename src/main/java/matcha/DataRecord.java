package matcha;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class DataRecord {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");

    private final LocalDateTime dateTime;
    private final double open;
    private final double low;
    private final double high;
    private final double close;
    private final double yesterdaysDailyLow;
    private final double yesterdaysDailyHigh;
    private final double todaysLow;
    private final double todaysHigh;

    private DataRecord(LocalDateTime dateTime, double open, double low, double high, double close,
                       double yesterdaysDailyLow, double yesterdaysDailyHigh, double todaysLow, double todaysHigh) {

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

    double getOpen() {
        return open;
    }

    double getLow() {
        return low;
    }

    double getHigh() {
        return high;
    }

    double getClose() {
        return close;
    }

    double getYesterdaysDailyLow() {
        return yesterdaysDailyLow;
    }

    double getYesterdaysDailyHigh() {
        return yesterdaysDailyHigh;
    }

    double getTodaysLow() {
        return todaysLow;
    }

    double getTodaysHigh() {
        return todaysHigh;
    }

    static class Builder {
        private LocalDateTime dateTime;
        private double open;
        private double low;
        private double high;
        private double close;
        private double yesterdaysDailyLow;
        private double yesterdaysDailyHigh;
        private double todaysLow;
        private double todaysHigh;

        Builder setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        Builder setDateTime(String dateTime) {
            return setDateTime(LocalDateTime.parse(dateTime, formatter));
        }

        Builder setOpen(double open) {
            this.open = open;
            return this;
        }

        Builder setLow(double low) {
            this.low = low;
            return this;
        }

        Builder setHigh(double high) {
            this.high = high;
            return this;
        }

        Builder setClose(double close) {
            this.close = close;
            return this;
        }

        Builder setYesterdaysDailyLow(double yesterdaysDailyLow) {
            this.yesterdaysDailyLow = yesterdaysDailyLow;
            return this;
        }

        Builder setYesterdaysDailyHigh(double yesterdaysDailyHigh) {
            this.yesterdaysDailyHigh = yesterdaysDailyHigh;
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

        DataRecord createDataRecord() {
            return new DataRecord(dateTime, open, low, high, close, yesterdaysDailyLow, yesterdaysDailyHigh, todaysLow,
                    todaysHigh);
        }
    }
}
