package matcha;

import java.util.Optional;

public interface PositionPlacer {

    Optional<Position> placePositions(UsefulTickData usefulTickData,
                                      BackTestingParameters backTestingParameters,
                                      int decimalPointPlace,
                                      boolean timeToOpenPosition);
}
