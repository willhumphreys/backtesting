package matcha;

import java.time.LocalDateTime;

class Position {
    private final LocalDateTime entryDate;
    private final double entry;
    private final double target;
    private final double stop;
    private boolean haveEdge;
    private boolean filled;
    private boolean closed;
    private int couldOfBeenBetter;

    Position(LocalDateTime entryDate, double entry, double target, double stop, boolean haveEdge, boolean filled) {
        this.entryDate = entryDate;
        this.entry = entry;
        this.target = target;
        this.stop = stop;
        this.haveEdge = haveEdge;
        this.filled = filled;
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

    boolean isHaveEdge() {
        return haveEdge;
    }

    int getCouldOfBeenBetter() {
        return couldOfBeenBetter;
    }

    void setCouldOfBeenBetter(int couldOfBeenBetter) {
        this.couldOfBeenBetter = couldOfBeenBetter;
    }

//    boolean isFilled() {
//        return filled;
//    }

    void fill() {
        this.filled = true;
    }
}
