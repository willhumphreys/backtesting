package matcha;

import java.io.IOException;
import java.nio.file.Path;

interface TickDataReader {
    String[][] read(Path s) throws IOException;
}
