package matcha.service;

import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RScriptServiceTest {

    @Test
    public void shouldExecuteASimpleRScript() throws Exception {
        final RScriptService rScriptService = new RScriptService();
        assertThat(rScriptService.executeScript(
                new ScriptArguments.Builder()
                        .setScriptPath(Paths.get("RScripts/cumulativeProfit.r"))
                        .setOutputPath(Paths.get("/tmp/test2"))
                        .setInputPath(Paths.get("results/acceptance_results/data"))
                        .createScriptArguments()),
                is(equalTo(0)));
    }
}
