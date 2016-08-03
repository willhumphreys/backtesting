package matcha;

import org.springframework.stereotype.Service;

@Service
public class HighAndLowChecks {

    boolean getLowCheck(String[] lowOfTheDay, double Low, double PreviousLow, int lowCheckPref, int index) {
        boolean lowCheck;
        switch (lowCheckPref) {
            case 0:
                //Current Candle is below last candle.
                lowCheck = Low < PreviousLow;
                break;
            case 1:
                //New low for the day is put in.
                lowCheck = Double.parseDouble(lowOfTheDay[index]) < Double.parseDouble(lowOfTheDay[index - 1]);
                break;
            case 2:
                //We don't put in a new low.
                lowCheck = Double.parseDouble(lowOfTheDay[index]) >= Double.parseDouble(lowOfTheDay[index - 1]);
                break;
            default:
                throw new IllegalArgumentException("Invalid lowCheck value");
        }
        return lowCheck;
    }

    boolean getHighCheck(String[] highOfTheDay, double High, double PreviousHigh, int highCheckPref, int
            index) {
        boolean highCheck;
        switch (highCheckPref) {
            case 0:
                highCheck = High > PreviousHigh;
                break;
            case 1:
                highCheck = Double.parseDouble(highOfTheDay[index]) > Double.parseDouble(highOfTheDay[index - 1]);
                break;
            case 2:
                //We don't put in a new high.
                highCheck = Double.parseDouble(highOfTheDay[index]) <= Double.parseDouble(highOfTheDay[index - 1]);
                break;
            default:
                throw new IllegalArgumentException("Invalid highCheck value");
        }
        return highCheck;
    }

}
