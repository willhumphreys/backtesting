package matcha;

import java.math.BigDecimal;

class Utils {

    int convertTicksToInt(BigDecimal doubleTicks, int decimalPointPlace) {
        return doubleTicks.multiply(BigDecimal.valueOf(decimalPointPlace)).intValueExact();
    }
}
