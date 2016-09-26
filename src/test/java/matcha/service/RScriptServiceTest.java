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
        assertThat(rScriptService.executeScript(Paths.get("RScripts/cumulativeProfit.r")), is(equalTo(0)));
    }
}
