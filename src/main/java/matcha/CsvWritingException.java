package matcha;

import java.io.IOException;

class CsvWritingException extends RuntimeException {
    CsvWritingException(String message, IOException e) {
        super(message, e);
    }
}
