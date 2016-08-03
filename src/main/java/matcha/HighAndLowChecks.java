package matcha;

import org.springframework.stereotype.Service;

@Service
public class HighAndLowChecks {

    boolean getLowCheck(double lowOfTheDay, double lowOfYesterday, double Low, double PreviousLow, int lowCheckPref) {
        boolean lowCheck;
        switch (lowCheckPref) {
            case 0:
                //Current Candle is below last candle.
                lowCheck = Low < PreviousLow;
                break;
            case 1:
                //New low for the day is put in.
                lowCheck = lowOfTheDay < lowOfYesterday;
                break;
            case 2:
                //We don't put in a new low.
                lowCheck = lowOfTheDay >= lowOfYesterday;
                break;
            default:
                throw new IllegalArgumentException("Invalid lowCheck value");
        }
        return lowCheck;
    }

    boolean getHighCheck(double highOfTheDay, double highOfYesterday, double High, double PreviousHigh, int
            highCheckPref) {
        boolean highCheck;
        switch (highCheckPref) {
            case 0:
                highCheck = High > PreviousHigh;
                break;
            case 1:
                highCheck = highOfTheDay > highOfYesterday;
                break;
            case 2:
                //We don't put in a new high.
                highCheck = highOfTheDay <= highOfYesterday;
                break;
            default:
                throw new IllegalArgumentException("Invalid highCheck value");
        }
        return highCheck;
    }

}
