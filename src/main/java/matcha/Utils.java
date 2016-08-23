package matcha;

import org.springframework.stereotype.Service;

@Service
class Utils {

    int convertTicksToInt(double doubleTicks, DecimalPointPlace decimalPointPlace) {
        return Math.toIntExact(Math.round(doubleTicks * getDecimalPointMultiplier(decimalPointPlace)));
    }

    private int getDecimalPointMultiplier(DecimalPointPlace decimalPointPlace) {
        if (decimalPointPlace == DecimalPointPlace.NORMAL) {
            return 20000;
        }
        return 200;
    }
}
