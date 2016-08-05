package matcha;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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


        for (String[] lineArray : list) {
            LocalDateTime hourDateTime = LocalDateTime.parse(lineArray[0], formatter);
            lineArray[0] =  hourDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        }

        String[][] dataArr = new String[list.size()][];
        return list.toArray(dataArr);
    }
}
