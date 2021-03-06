package matcha;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class TickDataReaderImpl implements TickDataReader {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private final DateTimeFormatter formatter;

    TickDataReaderImpl() {
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
    }

    @Override
    public List<DataRecord> read(Path fileLocation) throws IOException {

        Reader in = new FileReader(fileLocation.toFile());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
        LOG.info("Loaded data from " + fileLocation);
        List<DataRecord> dataRecords = Lists.newArrayList();
        for (CSVRecord record : records) {

            String dateTimeStr = record.get("date.time");
            BigDecimal open = new BigDecimal(record.get("open"));
            BigDecimal low = new BigDecimal(record.get("low"));
            BigDecimal high = new BigDecimal(record.get("high"));
            BigDecimal close = new BigDecimal(record.get("close"));
            BigDecimal yesterdaysDailyLow = new BigDecimal(record.get("yesterdays.daily.low"));
            BigDecimal yesterdaysDailyHigh = new BigDecimal(record.get("yesterdays.daily.high"));
            BigDecimal todaysLow = new BigDecimal(record.get("todays.low"));
            BigDecimal todaysHigh = new BigDecimal(record.get("todays.high"));
            final BigDecimal topBollingerBand = new BigDecimal(record.get("bollinger.2sd.90.TopBand"));
            final BigDecimal movingAverage = new BigDecimal(record.get("bollinger.2sd.90.MovingAverage"));
            final BigDecimal bottomBollingerBand = new BigDecimal(record.get("bollinger.2sd.90.BottomBand"));

            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                LOG.info("Failed to parse '" + dateTimeStr + "'");
                throw e;
            }

            DataRecord dataRecord = new DataRecord.Builder()
                    .setDateTime(dateTime)
                    .setOpen(open)
                    .setLow(low)
                    .setHigh(high)
                    .setClose(close)
                    .setYesterdaysDailyLow(yesterdaysDailyLow)
                    .setYesterdaysDailyHigh(yesterdaysDailyHigh)
                    .setTodaysLow(todaysLow)
                    .setTodaysHigh(todaysHigh)
                    .setTopBollingerBand(topBollingerBand)
                    .setMovingAverage(movingAverage)
                    .setBottomBollingerBand(bottomBollingerBand)
                    .createDataRecord();
            dataRecords.add(dataRecord);
        }

        LOG.info("Finished parsing " + fileLocation);
        return dataRecords;
    }
}
