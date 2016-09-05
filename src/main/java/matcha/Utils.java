package matcha;

class Utils {

    int convertTicksToInt(double doubleTicks, int decimalPointPlace) {
        return Math.toIntExact(Math.round(doubleTicks * decimalPointPlace));
    }
}
