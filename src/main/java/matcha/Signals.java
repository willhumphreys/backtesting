package matcha;

class Signals {

    boolean isLongSignal(UsefulTickData usefulTickData, int highLowCheckPref) {
        final boolean takeOutYesterdaysHigh = usefulTickData.isTakeOutYesterdaysHigh();
        final boolean closeNegative = usefulTickData.isCloseNegative();
        final boolean closeBelowYesterdaysHigh = usefulTickData.isCloseBelowYesterdaysHigh();
        final boolean openBelowYesterdaysHigh = usefulTickData.isOpenBelowYesterdaysHigh();
        final boolean highCheck = getHighCheck(usefulTickData, highLowCheckPref);
        return takeOutYesterdaysHigh &&
                closeNegative &&
                closeBelowYesterdaysHigh &&
                openBelowYesterdaysHigh &&
                highCheck;
    }

    boolean isShortSignal(UsefulTickData usefulTickData, int highLowCheckPref) {
        final boolean takeOutYesterdaysLow = usefulTickData.isTakeOutYesterdaysLow();
        final boolean closePositive = usefulTickData.isClosePositive();
        final boolean closeAboveYesterdaysLow = usefulTickData.isCloseAboveYesterdaysLow();
        return takeOutYesterdaysLow &&
                closePositive &&
                closeAboveYesterdaysLow &&
                usefulTickData.isOpenAboveYesterdaysLow() &&
                getLowCheck(usefulTickData, highLowCheckPref);
    }

    boolean getLowCheck(UsefulTickData usefulTickData, int highLowCheckPref) {
        boolean lowCheck;
        switch (highLowCheckPref) {
            case 0:
                //Current Candle is below last candle.
                lowCheck = usefulTickData.getCandleLow() < usefulTickData.getPreviousCandleLow();
                break;
            case 1:
                //New low for the day is put in.
                lowCheck = usefulTickData.getTodaysLow() < usefulTickData.getLowOfYesterday();
                break;
            case 2:
                //We don't put in a new low.
                lowCheck = usefulTickData.getTodaysLow() >= usefulTickData.getLowOfYesterday();
                break;
            default:
                throw new IllegalArgumentException("Invalid lowCheck value");
        }
        return lowCheck;
    }

    boolean getHighCheck(UsefulTickData usefulTickData, int highCheckPref) {
        boolean highCheck;
        switch (highCheckPref) {
            case 0:
                highCheck = usefulTickData.getCandleHigh() > usefulTickData.getPreviousCandleHigh();
                break;
            case 1:
                highCheck = usefulTickData.getTodaysHigh() > usefulTickData.getHighOfYesterday();
                break;
            case 2:
                //We don't put in a new high.
                highCheck = usefulTickData.getTodaysHigh() <= usefulTickData.getHighOfYesterday();
                break;
            default:
                throw new IllegalArgumentException("Invalid highCheck value");
        }
        return highCheck;
    }

}
