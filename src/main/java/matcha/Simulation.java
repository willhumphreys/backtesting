package matcha;

import org.springframework.stereotype.Service;

import static java.lang.Float.parseFloat;

@Service
public class Simulation {
    private static final int DATE = 0;
    private static final int OPEN = 1;
    private static final int LOW = 2;
    private static final int HIGH = 3;
    private static final int CLOSE = 4;
    private static final int DAILY_LOW = 5;
    private static final int DAILY_HIGH = 6;

    Results execute(String[][] data) {


        for (int i = 0; i < data.length; i++) {


            float CandleClose = parseFloat(data[i][DATE]);
            float CandleOpen = parseFloat(data[i][OPEN]);
            float CandleLow = parseFloat(data[i][LOW];
            float PreviousCandleLow = parseFloat(data[i -1][LOW]);
            float CandleHigh = parseFloat(data[i][HIGH]);
            float PreviousCandleHigh = parseFloat(data[i -1][HIGH]);
            float PreviousCandleClose = parseFloat(data[i-1][CLOSE]);

            float yesterdaysLow = parseFloat(data[i][DAILY_LOW]);
            float yesterdaysHigh = parseFloat(data[i][DAILY_HIGH]);

            float LastBarSize = PreviousCandleClose - PreviousCandleLow;


            Boolean takeOutYesterdaysLow = CandleLow < yesterdaysLow;
            Boolean closePositive = CandleClose > CandleOpen;
            Boolean closeAboveYesterdaysLow = CandleClose > yesterdaysLow;
            Boolean openAboveYesterdaysLow = CandleOpen > yesterdaysLow;

            Boolean takeOutYesterdaysHigh = CandleHigh > yesterdaysHigh;
            Boolean closeNegative = CandleClose < CandleOpen;
            Boolean closeBelowYesterdaysHigh = CandleClose < yesterdaysHigh;
            Boolean openBelowYesterdaysLow = CandleOpen < yesterdaysHigh;


//            if (takeOutYesterdaysLow &&
//                    closePositive &&
//                    closeAboveYesterdaysLow &&
//                    openAboveYesterdaysLow &&
//                    getLowCheck(sc, lowOfTheDay, CandleLow, PreviousCandleLow, highLowCheckPrefInput.GetIndex()) &&
//                    sc.GetBarHasClosedStatus() == BHCS_BAR_HAS_CLOSED) {

        }

        for (String[] line : data) {
            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] + line[DAILY_HIGH]);
        }

        return new Results();
    }
}
