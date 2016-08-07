package matcha;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class BacktestingApplication implements CommandLineRunner {

    private final Simulation simulation;

    @Autowired
    public BacktestingApplication(Simulation simulation) {
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


        Path dataDirectory = Paths.get("copied-data");

        int extraTicks = 10;

        final List<String> inputLines = Files.readAllLines(Paths.get("inputFileList.csv"));

        for (String inputLine : inputLines) {
            String[] lineParts = inputLine.split(",");
            final Path oneMinutePath = dataDirectory.resolve(lineParts[0]);
            final Path sixtyMinutePath = dataDirectory.resolve(lineParts[1]);
            Inputs input = new Inputs(oneMinutePath, sixtyMinutePath);
            final Results results = simulation.execute(input, extraTicks, "results");

            System.out.println(results);
        }
    }
}
