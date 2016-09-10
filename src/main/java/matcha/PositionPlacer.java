package matcha;

public interface PositionPlacer {

    Position createShort(UsefulTickData usefulTickData, int decimalPointPlacer);

    Position createLong(UsefulTickData usefulTickData, int decimalPointPlace);

    boolean isAShortSignal(UsefulTickData usefulTickData, int highLowCheckPref);

    boolean isALongSignal(UsefulTickData usefulTickData, int highLowCheckPref);
}
