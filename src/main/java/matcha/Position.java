package matcha;

import java.time.LocalDateTime;

class Position {
    private final LocalDateTime entryDate;
    private final double entry;
    private final double target;
    private final double stop;
    private boolean closed;

    Position(LocalDateTime entryDate, double entry, double target, double stop) {
        this.entryDate = entryDate;
        this.entry = entry;
        this.target = target;
        this.stop = stop;

    }

    LocalDateTime getEntryDate() {
        return entryDate;
    }

    double getEntry() {
        return entry;
    }

    double getTarget() {
        return target;
    }

    double getStop() {
        return stop;
    }

    boolean isClosed() {
        return closed;
    }

    void close() {
        this.closed = true;
    }
}
