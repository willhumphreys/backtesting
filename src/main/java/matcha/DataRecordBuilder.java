package matcha;

public class DataRecordBuilder {
    private String dateTime;
    private String open;
    private String low;
    private String high;
    private String close;
    private String yesterdaysDailyLow;
    private String yesterdaysDailyHigh;
    private String todaysLow;
    private String todaysHigh;

    public DataRecordBuilder setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public DataRecordBuilder setOpen(String open) {
        this.open = open;
        return this;
    }

    public DataRecordBuilder setLow(String low) {
        this.low = low;
        return this;
    }

    public DataRecordBuilder setHigh(String high) {
        this.high = high;
        return this;
    }

    public DataRecordBuilder setClose(String close) {
        this.close = close;
        return this;
    }

    public DataRecordBuilder setYesterdaysDailyLow(String yesterdaysDailyLow) {
        this.yesterdaysDailyLow = yesterdaysDailyLow;
        return this;
    }

    public DataRecordBuilder setYesterdaysDailyHigh(String yesterdaysDailyHigh) {
        this.yesterdaysDailyHigh = yesterdaysDailyHigh;
        return this;
    }

    public DataRecordBuilder setTodaysLow(String todaysLow) {
        this.todaysLow = todaysLow;
        return this;
    }

    public DataRecordBuilder setTodaysHigh(String todaysHigh) {
        this.todaysHigh = todaysHigh;
        return this;
    }

    public DataRecord createDataRecord() {
        return new DataRecord(dateTime, open, low, high, close, yesterdaysDailyLow, yesterdaysDailyHigh, todaysLow, todaysHigh);
    }
}
