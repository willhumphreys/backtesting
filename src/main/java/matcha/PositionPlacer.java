package matcha;

import java.util.Optional;

public interface PositionPlacer {

    Optional<Position> placePositions(UsefulTickData usefulTickData,
                                      int decimalPointPlace, int highLowCheckPref);
}
