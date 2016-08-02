package matcha;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Double.parseDouble;
import static java.lang.String.format;

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

    private double entry;
    private double stop;
    private double target;

    private boolean pending;


    private DateTimeFormatter formatter;


    public Simulation() {
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
    }

    Results execute(String[][] hourData, String[][] tickData) {


        int hourCounter = 0;
        for (int i = 1; i < tickData.length; i++) {

            LocalDateTime hourDateTime = LocalDateTime.parse(hourData[hourCounter][DATE], formatter);
            LocalDateTime tickDateTime = LocalDateTime.parse(tickData[i][DATE], formatter);

            final int hourCandleHour = hourDateTime.getHour();
            final int tickCandleHour = tickDateTime.getHour();
            if(hourCandleHour != tickCandleHour || hourCandleHour != tickCandleHour -1) {
                throw new IllegalStateException(format("The hour candles are out of sync. " +
                        "Hour Candle: %d Tick Candle: %d", hourCandleHour, tickCandleHour));
            }

            if(hourCandleHour == tickCandleHour -1) {
                hourCounter++;
            }

            double CandleClose = parseDouble(hourData[hourCounter][CLOSE]);
            double CandleOpen = parseDouble(hourData[hourCounter][OPEN]);
            double CandleLow = parseDouble(hourData[hourCounter][LOW]);
            double PreviousCandleLow = parseDouble(hourData[i -1][LOW]);
            double CandleHigh = parseDouble(hourData[hourCounter][HIGH]);
            double PreviousCandleHigh = parseDouble(hourData[i -1][HIGH]);
            double PreviousCandleClose = parseDouble(hourData[i-1][CLOSE]);

            double yesterdaysLow = parseDouble(hourData[hourCounter][DAILY_LOW]);
            double yesterdaysHigh = parseDouble(hourData[hourCounter][DAILY_HIGH]);

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
                    getLowCheck(hourData[TODAYS_LOW], CandleLow, PreviousCandleLow, 0, i)) {

                System.out.println("Enter new low");


                if(entry == -1 && !pending) {

                    this.stop = CandleClose + (CandleClose - CandleLow);
                    this.target = CandleLow;
                    this.entry = CandleClose;
                    pending = true;

                }

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
                    getHighCheck(hourData[TODAYS_HIGH], CandleHigh, PreviousCandleHigh, 0, i)) {

                System.out.println("Enter new high");


                if(entry == -1 && !pending) {
                    this.stop = CandleClose - (CandleHigh - CandleClose);
                    this.target = CandleHigh;
                    this.entry = CandleClose;
                    pending = true;
                }
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

        for (String[] line : hourData) {
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
