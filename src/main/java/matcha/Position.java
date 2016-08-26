package matcha;

import java.time.LocalDateTime;

class Position {
    private final LocalDateTime entryDate;
    private final double entry;
    private final double target;
    private final double stop;
    private boolean haveEdge;
    private boolean closed;
    private int couldOfBeenBetter;

    Position(LocalDateTime entryDate, double entry, double target, double stop, boolean haveEdge) {
        this.entryDate = entryDate;
        this.entry = entry;
        this.target = target;
        this.stop = stop;
        this.haveEdge = haveEdge;
    }

    public LocalDateTime getEntryDate() {
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

    public boolean isClosed() {
        return closed;
    }

    void close() {
        this.closed = true;
    }

    public boolean isHaveEdge() {
        return haveEdge;
    }

    public int getCouldOfBeenBetter() {
        return couldOfBeenBetter;
    }

    public void setCouldOfBeenBetter(int couldOfBeenBetter) {
        this.couldOfBeenBetter = couldOfBeenBetter;
    }
}
