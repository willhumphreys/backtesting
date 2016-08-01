package matcha;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class TickDataReaderImpl implements TickDataReader {
    @Override
    public String[][] read(String fileLocation) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(new File(fileLocation)));
        List<String[]> list = csvReader.readAll();

        list.forEach(line -> System.out.println(line[0]));

        String[][] dataArr = new String[list.size()][];
        return list.toArray(dataArr);
    }
}
