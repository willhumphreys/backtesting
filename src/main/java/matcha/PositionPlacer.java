package matcha;

public interface PositionPlacer {

    Position createShort(UsefulTickData usefulTickData, int decimalPointPlacer);

    Position createLong(UsefulTickData usefulTickData, int decimalPointPlace);

}
