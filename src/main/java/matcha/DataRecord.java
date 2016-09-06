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
}
