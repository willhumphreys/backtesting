package matcha;

class Position {
    private final String entryDate;
    private final double entry;
    private final double target;
    private final double stop;

    Position(String entryDate, double entry, double target, double stop) {
        this.entryDate = entryDate;
        this.entry = entry;
        this.target = target;
        this.stop = stop;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public double getEntry() {
        return entry;
    }

    public double getTarget() {
        return target;
    }

    public double getStop() {
        return stop;
    }
}
