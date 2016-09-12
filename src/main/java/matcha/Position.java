package matcha;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class Position {
    private final LocalDateTime entryDate;
    private final BigDecimal entry;
    private final BigDecimal target;
    private final BigDecimal stop;
    private boolean closed;

    Position(LocalDateTime entryDate, BigDecimal entry, BigDecimal target, BigDecimal stop) {
        this.entryDate = entryDate;
        this.entry = entry;
        this.target = target;
        this.stop = stop;

    }

    LocalDateTime getEntryDate() {
        return entryDate;
    }

    BigDecimal getEntry() {
        return entry;
    }

    BigDecimal getTarget() {
        return target;
    }

    BigDecimal getStop() {
        return stop;
    }

    boolean isClosed() {
        return closed;
    }

    void close() {
        this.closed = true;
    }
}
