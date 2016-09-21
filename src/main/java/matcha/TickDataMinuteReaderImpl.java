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

class TickDataMinuteReaderImpl implements TickDataReader {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private final DateTimeFormatter formatter;

    TickDataMinuteReaderImpl() {
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
                    .createDataRecord();
            dataRecords.add(dataRecord);
        }

        LOG.info("Finished parsing minute data " + fileLocation);
        return dataRecords;
    }
}
