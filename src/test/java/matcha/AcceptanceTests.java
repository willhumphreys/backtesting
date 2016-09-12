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
        final List<Results> allResults = backtestingApplication.run(
                "-input", "acceptance_data/input_one_winner.csv", "-output", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(1)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(0)));
        assertThat(results.getPositionStats().getShortTradeCount(), is(equalTo(1)));
        assertThat(results.getPositionStats().getTickCounter(), is(equalTo(27)));
    }

    @Test
    public void shouldGoLongAWinningTradeAtLows() throws Exception {
        final List<Results> allResults = backtestingApplication.run(
                "-input", "acceptance_data/eurjpy_long_winner.csv", "-output", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(1)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(0)));
        assertThat(results.getPositionStats().getLongTradeCount(), is(equalTo(1)));
        assertThat(results.getPositionStats().getTickCounter(), is(equalTo(22)));
    }

    @Test
    public void shouldAShortLosingTradeAtHighs() throws Exception {
        final List<Results> allResults = backtestingApplication.run(
                "-input", "acceptance_data/eurjpy_short_loser.csv", "-output", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(1)));
        assertThat(results.getPositionStats().getShortTradeCount(), is(equalTo(1)));
        assertThat(results.getPositionStats().getTickCounter(), is(equalTo(-51)));
    }

    @Test
    public void shouldGoLongALosingTradeAtLows() throws Exception {
        final List<Results> allResults = backtestingApplication.run(
                "-input", "acceptance_data/eurjpy_long_loser.csv", "-output", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(1)));
        assertThat(results.getPositionStats().getLongTradeCount(), is(equalTo(1)));
        assertThat(results.getPositionStats().getTickCounter(), is(equalTo(-15)));
    }

    @Test
    public void shouldGoLongALosingTradeAtLowsAndThenLongAWinningTradeAtLows() throws Exception {
        final List<Results> allResults = backtestingApplication.run(
                "-input", "acceptance_data/eurjpy_long_loser_then_winner.csv", "-output", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(1)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(1)));
        assertThat(results.getPositionStats().getLongTradeCount(), is(equalTo(2)));
        assertThat(results.getPositionStats().getTickCounter(), is(equalTo(7)));
    }

    @Test
    public void shouldGoLongALosingTradeAtLowsAndThenShortALosingTradeAtLows() throws Exception {
        final List<Results> allResults = backtestingApplication.run(
                "-input", "acceptance_data/eurjpy_long_loser_then_short_loser.csv", "-output", "acceptance_results");

        assertThat(allResults.size(), is(equalTo(1)));
        final Results results = allResults.get(0);

        assertThat(results.getPositionStats().getWinners(), is(equalTo(0)));
        assertThat(results.getPositionStats().getLosers(), is(equalTo(2)));
        assertThat(results.getPositionStats().getLongTradeCount(), is(equalTo(1)));
        assertThat(results.getPositionStats().getShortTradeCount(), is(equalTo(1)));
        assertThat(results.getPositionStats().getTickCounter(), is(equalTo(-147)));
    }
}
