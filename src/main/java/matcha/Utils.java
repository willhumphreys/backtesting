package matcha;

import org.springframework.stereotype.Service;

@Service
class Utils {

    int convertTicksToInt(double doubleTicks) {
        return Math.toIntExact(Math.round(doubleTicks * 20000));
    }
}
