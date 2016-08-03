package matcha;

class Position {
    private final String entryDate;
    private final double entry;
    private final double target;
    private final double stop;
    private boolean closed;

    Position(String entryDate, double entry, double target, double stop) {
        this.entryDate = entryDate;
        this.entry = entry;
        this.target = target;
        this.stop = stop;
    }

    public String getEntryDate() {
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
}
