package matcha;

import com.opencsv.CSVReader;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ListIterator;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class TickDataReaderImpl implements TickDataReader {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private DateTimeFormatter formatter;

    TickDataReaderImpl() {
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
    }

    @Override
    public String[][] read(Path fileLocation) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(fileLocation.toFile()));
        List<String[]> list = csvReader.readAll();

        LOG.info("Loaded data from " + fileLocation);
        for(ListIterator<String[]> iter = list.listIterator(); iter.hasNext();){

            final String[] lineArray = iter.next();
            if(lineArray[0].trim().length() == 0 || lineArray[0].equals("date.time")) {
                iter.remove();
                continue;
            }

            LocalDateTime hourDateTime;
            try {
                hourDateTime = LocalDateTime.parse(lineArray[0], formatter);
            } catch (DateTimeParseException e) {
                LOG.info("Failed to parse '" + lineArray[0] + "'");
                throw e;
            }
            lineArray[0] =  hourDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        }

        String[][] dataArr = new String[list.size()][];
        return list.toArray(dataArr);
    }
}
