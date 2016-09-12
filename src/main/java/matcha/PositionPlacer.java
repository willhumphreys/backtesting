package matcha;

import java.util.Optional;

interface PositionPlacer {

    Optional<Position> placePositions(UsefulTickData usefulTickData,
                                      int decimalPointPlace);
}
