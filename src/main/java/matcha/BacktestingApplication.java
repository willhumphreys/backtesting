package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BacktestingApplication implements CommandLineRunner {

    private final TickDataReader tickDataReader;
    private final Simulation simulation;

    @Autowired
    public BacktestingApplication(TickDataReader tickDataReader, Simulation simulation) {
        this.tickDataReader = tickDataReader;
        this.simulation = simulation;
    }

    public static void main(String[] args) {
        SpringApplication.run(BacktestingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        String[][] data = tickDataReader.read("Data/dataOut2.txt");

        Results results = simulation.execute(data);
    }
}
