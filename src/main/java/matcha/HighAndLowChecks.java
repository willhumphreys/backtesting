package matcha;

import org.springframework.stereotype.Service;

@Service
public class HighAndLowChecks {

    boolean getLowCheck(UsefulTickData usefulTickData, int lowCheckPref) {
        boolean lowCheck;
        switch (lowCheckPref) {
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
