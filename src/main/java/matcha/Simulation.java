package matcha;

import org.springframework.stereotype.Service;

@Service
public class Simulation {
    private static final int DATE = 0;
    private static final int OPEN = 1;
    private static final int LOW = 2;
    private static final int HIGH = 3;
    private static final int CLOSE = 4;
    private static final int DAILY_LOW = 5;
    private static final int DAILY_HIGH = 6;

    Results execute(String[][] data) {

        for (String[] line : data) {
            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] + line[DAILY_HIGH]);
        }

        return new Results();
    }
}
