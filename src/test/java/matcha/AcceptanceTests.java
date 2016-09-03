package matcha;

import org.junit.Test;
import org.springframework.boot.SpringApplication;

public class AcceptanceTests {

    @Test
    public void shouldPlaceATrade() throws Exception {

        SpringApplication.run(BacktestingApplication.class, "FadeTheBreakoutNormalDaily", "input_one_winner.csv", "acceptance_results");

    }
}
