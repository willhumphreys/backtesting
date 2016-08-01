package matcha;

import java.io.IOException;

public interface TickDataReader {
    String[][] read(String s) throws IOException;
}
