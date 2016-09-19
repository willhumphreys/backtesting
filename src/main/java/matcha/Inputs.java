package matcha;

import java.nio.file.Path;

class Inputs {
    private final String symbol;
    private final Path file1;
    private final Path file2;

    Inputs(String symbol, Path file1, Path file2) {
        this.symbol = symbol;
        this.file1 = file1;
        this.file2 = file2;
    }

    Path getFile1() {
        return file1;
    }

    Path getFile2() {
        return file2;
    }

    public String getSymbol() {
        return symbol;
    }
}
