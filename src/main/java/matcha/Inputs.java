package matcha;

import java.nio.file.Path;

class Inputs {
    private final Path file1;
    private final Path file2;

    Inputs(Path file1, Path file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    Path getFile1() {
        return file1;
    }

    Path getFile2() {
        return file2;
    }
}
