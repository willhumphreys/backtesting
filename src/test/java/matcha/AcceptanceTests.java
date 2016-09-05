package matcha;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AcceptanceTests {

    private BacktestingApplication backtestingApplication;

    @Before
    public void beforeEachTest() throws Exception {
        Injector injector = Guice.createInjector((Module) binder -> {

        });
        backtestingApplication = injector.getInstance(BacktestingApplication.class);
    }

    @Test
    public void shouldAShortWinningTradeAtHighs() throws Exception {
        final List<Results> allResults = backtestingApplication.run("FadeTheBreakoutNormalDaily",
                "input_one_winner.csv", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(1)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(0)));
    }

    @Test
    public void shouldGoLongAWinningTradeAtLows() throws Exception {
        final List<Results> allResults = backtestingApplication.run("FadeTheBreakoutNormalDaily",
                "eurjpy_long_winner.csv", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(1)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(0)));
    }

    @Test
    public void shouldAShortLosingTradeAtHighs() throws Exception {
        final List<Results> allResults = backtestingApplication.run("FadeTheBreakoutNormalDaily",
                "eurjpy_short_loser.csv", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(1)));
    }

}
