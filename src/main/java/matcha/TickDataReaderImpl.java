package matcha;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class TickDataReaderImpl implements TickDataReader {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private DateTimeFormatter formatter;

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

            String dateTime = record.get("date.time");
            String open = record.get("open");
            String low = record.get("low");
            String high = record.get("high");
            String close = record.get("close");
            String yesterdaysDailyLow = record.get("yesterdays.daily.low");
            String yesterdaysDailyHigh = record.get("yesterdays.daily.high");
            String todaysLow = record.get("todays.low");
            String todaysHigh = record.get("todays.high");

//            final String[] lineArray = iter.next();
//            if(lineArray[0].trim().length() == 0 || lineArray[0].equals("date.time")) {
//                iter.remove();
//                continue;
//            }

            LocalDateTime hourDateTime;
            try {
                hourDateTime = LocalDateTime.parse(dateTime, formatter);
            } catch (DateTimeParseException e) {
                LOG.info("Failed to parse '" + dateTime + "'");
                throw e;
            }
            dateTime = hourDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

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
                    .createDataRecord();
            dataRecords.add(dataRecord);
        }

        LOG.info("Finished parsing " + fileLocation);
        return dataRecords;
    }
}
