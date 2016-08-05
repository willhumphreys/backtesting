package matcha;

import java.nio.file.Path;

public class Inputs {
    private final Path file1;
    private final Path file2;

    public Inputs(Path file1, Path file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    public Path getFile1() {
        return file1;
    }

    public Path getFile2() {
        return file2;
    }
}
