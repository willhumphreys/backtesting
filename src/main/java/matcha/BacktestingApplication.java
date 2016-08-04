package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BacktestingApplication implements CommandLineRunner {

    private static final int DATA_FILE = 0;
    private static final int TICK_DATA_FILE = 1;
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

    /**
     *
     */
    @Override
    public void run(String... args) throws Exception {

        String[][] data = tickDataReader.read(args[DATA_FILE]);
        String[][] tickData = tickDataReader.read(args[TICK_DATA_FILE]);

        int extraTicks = 10;

        Results results = simulation.execute(data, tickData, extraTicks);

        System.out.println(results);
    }
}
