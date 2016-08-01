package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BacktestingApplication implements CommandLineRunner {

    private final TickDataReader tickDataReader;

    @Autowired
    public BacktestingApplication(TickDataReader tickDataReader) {
        this.tickDataReader = tickDataReader;
    }

    public static void main(String[] args) {
        SpringApplication.run(BacktestingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        String[][] data = tickDataReader.read("Data/dataOut2.txt");

        System.out.println(data[0][0]);
    }
}
