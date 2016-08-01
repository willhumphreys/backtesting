package matcha;

import com.opencsv.CSVReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@SpringBootApplication
public class BacktestingApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BacktestingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        CSVReader csvReader = new CSVReader(new FileReader(new File("Data/dataOut2.txt")));
        List<String[]> list = csvReader.readAll();

        list.forEach(line -> System.out.println(line[0]));

        String[][] dataArr = new String[list.size()][];
        dataArr = list.toArray(dataArr);



    }
}
