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

    DataRecord(LocalDateTime dateTime, double open, double low, double high, double close, double yesterdaysDailyLow,
               double yesterdaysDailyHigh, double todaysLow, double todaysHigh) {

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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getOpen() {
        return open;
    }

    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }

    public double getClose() {
        return close;
    }

    public double getYesterdaysDailyLow() {
        return yesterdaysDailyLow;
    }

    public double getYesterdaysDailyHigh() {
        return yesterdaysDailyHigh;
    }

    public double getTodaysLow() {
        return todaysLow;
    }

    public double getTodaysHigh() {
        return todaysHigh;
    }

    public static class Builder {
        private LocalDateTime dateTime;
        private double open;
        private double low;
        private double high;
        private double close;
        private double yesterdaysDailyLow;
        private double yesterdaysDailyHigh;
        private double todaysLow;
        private double todaysHigh;

        public Builder setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder setDateTime(String dateTime) {
            return setDateTime(LocalDateTime.parse(dateTime, formatter));
        }

        public Builder setOpen(double open) {
            this.open = open;
            return this;
        }

        public Builder setLow(double low) {
            this.low = low;
            return this;
        }

        public Builder setHigh(double high) {
            this.high = high;
            return this;
        }

        public Builder setClose(double close) {
            this.close = close;
            return this;
        }

        public Builder setYesterdaysDailyLow(double yesterdaysDailyLow) {
            this.yesterdaysDailyLow = yesterdaysDailyLow;
            return this;
        }

        public Builder setYesterdaysDailyHigh(double yesterdaysDailyHigh) {
            this.yesterdaysDailyHigh = yesterdaysDailyHigh;
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

        public DataRecord createDataRecord() {
            return new DataRecord(dateTime, open, low, high, close, yesterdaysDailyLow, yesterdaysDailyHigh, todaysLow,
                    todaysHigh);
        }
    }
}
