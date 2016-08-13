package matcha;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ListIterator;

@Service
public class TickDataReaderImpl implements TickDataReader {

    private DateTimeFormatter formatter;


    public TickDataReaderImpl() {
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
    }

    @Override
    public String[][] read(Path fileLocation) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(fileLocation.toFile()));
        List<String[]> list = csvReader.readAll();


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
                System.out.println("Failed to parse '" + lineArray[0] + "'");
                throw e;
            }
            lineArray[0] =  hourDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        }

        String[][] dataArr = new String[list.size()][];
        return list.toArray(dataArr);
    }
}
