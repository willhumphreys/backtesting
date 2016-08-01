package matcha;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static java.lang.Double.parseDouble;

@Service
public class Simulation {
    private static final int DATE = 0;
    private static final int OPEN = 1;
    private static final int LOW = 2;
    private static final int HIGH = 3;
    private static final int CLOSE = 4;
    private static final int DAILY_LOW = 5;
    private static final int DAILY_HIGH = 6;
    private static final int TODAYS_LOW = 7;
    private static final int TODAYS_HIGH = 8;


    private DateTimeFormatter formatter;


    public Simulation() {
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
    }

    Results execute(String[][] data) {


        for (int i = 0; i < data.length; i++) {



            LocalDateTime dateTime = LocalDateTime.parse(data[i][DATE], formatter);

            double CandleClose = parseDouble(data[i][CLOSE]);
            double CandleOpen = parseDouble(data[i][OPEN]);
            double CandleLow = parseDouble(data[i][LOW]);
            double PreviousCandleLow = parseDouble(data[i -1][LOW]);
            double CandleHigh = parseDouble(data[i][HIGH]);
            double PreviousCandleHigh = parseDouble(data[i -1][HIGH]);
            double PreviousCandleClose = parseDouble(data[i-1][CLOSE]);

            double yesterdaysLow = parseDouble(data[i][DAILY_LOW]);
            double yesterdaysHigh = parseDouble(data[i][DAILY_HIGH]);

            double LastBarSize = PreviousCandleClose - PreviousCandleLow;


            Boolean takeOutYesterdaysLow = CandleLow < yesterdaysLow;
            Boolean closePositive = CandleClose > CandleOpen;
            Boolean closeAboveYesterdaysLow = CandleClose > yesterdaysLow;
            Boolean openAboveYesterdaysLow = CandleOpen > yesterdaysLow;

            Boolean takeOutYesterdaysHigh = CandleHigh > yesterdaysHigh;
            Boolean closeNegative = CandleClose < CandleOpen;
            Boolean closeBelowYesterdaysHigh = CandleClose < yesterdaysHigh;
            Boolean openBelowYesterdaysLow = CandleOpen < yesterdaysHigh;

            if (takeOutYesterdaysLow &&
                    closePositive &&
                    closeAboveYesterdaysLow &&
                    openAboveYesterdaysLow &&
                    getLowCheck(data[TODAYS_LOW], CandleLow, PreviousCandleLow, 0, i)) {

                System.out.println("Enter new low");

//                s_SCNewOrder order;
//                order.OrderQuantity = 1;
//                order.OrderType = SCT_ORDERTYPE_MARKET;
//
//                order.Target1Price = CandleLow - extraTicks;
//                order.Stop1Price = CandleClose + (CandleClose - CandleLow) + extraTicks;
//                order.OCOGroup1Quantity = 1; // If this is left at the default of 0, then it will be automatically set.
//
//                //logEntryMessage(sc, PositionData, order);
//                sc.SellEntry(order);

            }

            if (takeOutYesterdaysHigh &&
                    closeNegative &&
                    closeBelowYesterdaysHigh &&
                    openBelowYesterdaysLow &&
                    getHighCheck(data[TODAYS_HIGH], CandleHigh, PreviousCandleHigh, 0, i)) {

                System.out.println("Enter new high");

//                s_SCNewOrder order;
//                order.OrderQuantity = 1;
//                order.OrderType = SCT_ORDERTYPE_MARKET;
//
//                order.Target1Price = CandleHigh + extraTicks;
//                order.Stop1Price = CandleClose - (CandleHigh - CandleClose) - extraTicks;
//
//                order.OCOGroup1Quantity = 1; // If this is left at the default of 0, then it will be automatically set.
//
//                //logEntryMessage(sc, PositionData, order);
//                sc.BuyEntry(order);
            }
        }

        for (String[] line : data) {
            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] + line[DAILY_HIGH]);
        }

        return new Results();
    }

    private boolean getLowCheck(String[] lowOfTheDay, double Low, double PreviousLow, int lowCheckPref, int index) {
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
                throw new IllegalArgumentException("Invalid lowCheck value" );
        }
        return lowCheck;
    }

    private boolean getHighCheck(String[] highOfTheDay, double High, double PreviousHigh, int highCheckPref, int index) {
        boolean highCheck;
        switch(highCheckPref) {
            case 0:
                highCheck = High > PreviousHigh;
                break;
            case 1:
                highCheck =  Double.parseDouble(highOfTheDay[index]) > Double.parseDouble(highOfTheDay[index - 1]);
                break;
            case 2:
                //We don't put in a new high.
                highCheck = Double.parseDouble(highOfTheDay[index]) <= Double.parseDouble(highOfTheDay[index - 1]);
                break;
            default:
                throw new IllegalArgumentException("Invalid highCheck value" );
        }
        return highCheck;
    }

}
