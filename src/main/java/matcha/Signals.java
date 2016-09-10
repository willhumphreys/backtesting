package matcha;

import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

class Signals {


    FadeTheExtremesPositionPlacer fadeTheExtremesPositionPlacer;

    private static final Logger LOG = getLogger(lookup().lookupClass());

    Signals(FadeTheExtremesPositionPlacer fadeTheExtremesPositionPlacer) {
        this.fadeTheExtremesPositionPlacer = fadeTheExtremesPositionPlacer;
    }


}
