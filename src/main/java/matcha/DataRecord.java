package matcha;

class DataRecord {
    private final String dateTime;
    private final String open;
    private final String low;
    private final String high;
    private final String close;
    private final String yesterdaysDailyLow;
    private final String yesterdaysDailyHigh;
    private final String todaysLow;
    private final String todaysHigh;

    DataRecord(String dateTime, String open, String low, String high, String close, String yesterdaysDailyLow, String yesterdaysDailyHigh, String todaysLow, String todaysHigh) {

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

    public String getDateTime() {
        return dateTime;
    }

    public String getOpen() {
        return open;
    }

    public String getLow() {
        return low;
    }

    public String getHigh() {
        return high;
    }

    public String getClose() {
        return close;
    }

    public String getYesterdaysDailyLow() {
        return yesterdaysDailyLow;
    }

    public String getYesterdaysDailyHigh() {
        return yesterdaysDailyHigh;
    }

    public String getTodaysLow() {
        return todaysLow;
    }

    public String getTodaysHigh() {
        return todaysHigh;
    }

    public static class Builder {
        private String dateTime;
        private String open;
        private String low;
        private String high;
        private String close;
        private String yesterdaysDailyLow;
        private String yesterdaysDailyHigh;
        private String todaysLow;
        private String todaysHigh;

        public Builder setDateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder setOpen(String open) {
            this.open = open;
            return this;
        }

        public Builder setLow(String low) {
            this.low = low;
            return this;
        }

        public Builder setHigh(String high) {
            this.high = high;
            return this;
        }

        public Builder setClose(String close) {
            this.close = close;
            return this;
        }

        public Builder setYesterdaysDailyLow(String yesterdaysDailyLow) {
            this.yesterdaysDailyLow = yesterdaysDailyLow;
            return this;
        }

        public Builder setYesterdaysDailyHigh(String yesterdaysDailyHigh) {
            this.yesterdaysDailyHigh = yesterdaysDailyHigh;
            return this;
        }

        public Builder setTodaysLow(String todaysLow) {
            this.todaysLow = todaysLow;
            return this;
        }

        public Builder setTodaysHigh(String todaysHigh) {
            this.todaysHigh = todaysHigh;
            return this;
        }

        public DataRecord createDataRecord() {
            return new DataRecord(dateTime, open, low, high, close, yesterdaysDailyLow, yesterdaysDailyHigh, todaysLow, todaysHigh);
        }
    }
}
