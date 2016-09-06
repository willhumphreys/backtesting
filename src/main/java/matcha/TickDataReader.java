package matcha;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

interface TickDataReader {
    List<DataRecord> read(Path s) throws IOException;
}
