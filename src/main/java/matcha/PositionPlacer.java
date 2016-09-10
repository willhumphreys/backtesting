package matcha;

import java.time.LocalDateTime;

import static java.lang.String.format;

public interface PositionPlacer {

    Position createShort(UsefulTickData usefulTickData, int decimalPointPlacer);

    Position createLong(UsefulTickData usefulTickData, int decimalPointPlace);

}
