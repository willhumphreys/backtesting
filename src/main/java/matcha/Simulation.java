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

    private boolean availableToTrade;

    private boolean timeToOpenPosition;

    private DateTimeFormatter formatter;


    public Simulation() {
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
        entry = -1;
        stop = -1;
        target = -1;
    }

    Results execute(String[][] hourData, String[][] tickData) {

        int hourCounter = 0;
        for (int i = 1; i < tickData.length; i++) {

            //If it the last tick skip trading.
            if(i == tickData.length -1) {
                continue;
            }

            //Get tick hour and the hour hour.
            LocalDateTime hourDateTime = LocalDateTime.parse(hourData[hourCounter][DATE], formatter);
            LocalDateTime tickDateTime = LocalDateTime.parse(tickData[i][DATE], formatter);
            LocalDateTime nextTickDateTime = LocalDateTime.parse(tickData[i +1][DATE], formatter);

            int hourCandleHour = hourDateTime.getHour();
            final int tickCandleHour = tickDateTime.getHour();

            //If this is the last tick then it is time to open our position.
            if(tickDateTime.getHour() != nextTickDateTime.getHour()) {
                timeToOpenPosition = true;
            }

            //If the hour has changed we need to update the hour counter.
            if(tickCandleHour != hourCandleHour) {
                hourCounter ++;

            }

            if (hourCounter != 0) {

                double CandleClose = parseDouble(hourData[hourCounter][CLOSE]);
                double CandleOpen = parseDouble(hourData[hourCounter][OPEN]);
                double CandleLow = parseDouble(hourData[hourCounter][LOW]);
                double PreviousCandleLow = parseDouble(hourData[hourCounter - 1][LOW]);
                double CandleHigh = parseDouble(hourData[hourCounter][HIGH]);
                double PreviousCandleHigh = parseDouble(hourData[hourCounter - 1][HIGH]);
                double PreviousCandleClose = parseDouble(hourData[hourCounter - 1][CLOSE]);

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



                    if (entry == -1 && timeToOpenPosition && availableToTrade) {

                        System.out.println("Enter new low");


                        this.stop = CandleClose + (CandleClose - CandleLow);
                        this.target = CandleLow;
                        this.entry = CandleClose;
                        availableToTrade = false;
                    }

                }

                if (takeOutYesterdaysHigh &&
                        closeNegative &&
                        closeBelowYesterdaysHigh &&
                        openBelowYesterdaysLow &&
                        getHighCheck(hourData[TODAYS_HIGH], CandleHigh, PreviousCandleHigh, 0, i)) {




                    if (entry == -1 && timeToOpenPosition && availableToTrade) {

                        System.out.println("Enter new high");
                        this.stop = CandleClose - (CandleHigh - CandleClose);
                        this.target = CandleHigh;
                        this.entry = CandleClose;
                        availableToTrade = false;
                    }
                }

            }



            timeToOpenPosition = false;

        }

//        for (String[] line : hourData) {
//            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] +
//                    line[DAILY_HIGH]);
//        }

        return new Results();
    }

    private boolean isHourCandleOutOfSyncByMoreThanAnHour(int hourCandleHour, int tickCandleHour) {
        return Math.abs(hourCandleHour - tickCandleHour) > 1 && !(hourCandleHour == 23 && tickCandleHour == 0);
    }

    private boolean isHourCandleBehind(int hourCandleHour, int tickCandleHour) {
        return hourCandleHour == tickCandleHour - 1;
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
                throw new IllegalArgumentException("Invalid lowCheck value");
        }
        return lowCheck;
    }

    private boolean getHighCheck(String[] highOfTheDay, double High, double PreviousHigh, int highCheckPref, int
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
