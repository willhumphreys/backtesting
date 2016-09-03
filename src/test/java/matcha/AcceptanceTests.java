package matcha;

import org.junit.Test;


public class AcceptanceTests {


    private BacktestingApplication backtestingApplication;

    @Test
    public void shouldPlaceATrade() throws Exception {

        backtestingApplication.run("FadeTheBreakoutNormalDaily", "input_one_winner.csv", "acceptance_results");

    }
}
