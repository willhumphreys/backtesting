package matcha;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private double entry;
    private double stop;
    private double target;

    private boolean availableToTrade;
    private boolean timeToOpenPosition;

    private double tickCounter;
    private int winners;
    private int losers;


    private DateTimeFormatter formatter;


    public Simulation() {
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m:s");
        entry = -1.0;
        stop = -1.0;
        target = -1.0;
        availableToTrade = true;
    }

    Results execute(String[][] hourData, String[][] tickData) {

        int hourCounter = 0;
        for (int i = 1; i < tickData.length; i++) {

            //If it the last tick skip trading.
            if (i == tickData.length - 1) {
                continue;
            }

            //Get tick hour and the hour hour.
            LocalDateTime hourDateTime = LocalDateTime.parse(hourData[hourCounter][DATE], formatter);
            LocalDateTime tickDateTime = LocalDateTime.parse(tickData[i][DATE], formatter);
            LocalDateTime nextTickDateTime = LocalDateTime.parse(tickData[i + 1][DATE], formatter);

            int hourCandleHour = hourDateTime.getHour();
            final int tickCandleHour = tickDateTime.getHour();

            //If this is the last tick then it is time to open our position if we have one.
            if (tickDateTime.getHour() != nextTickDateTime.getHour()) {
                timeToOpenPosition = true;
            }

            //If the hour has changed we need to update the hour counter.
            if (tickCandleHour != hourCandleHour) {
                hourCounter++;
            }

            if (hourCounter != 0) {

                double candleClose = parseDouble(hourData[hourCounter][CLOSE]);
                double candleOpen = parseDouble(hourData[hourCounter][OPEN]);
                double candleLow = parseDouble(hourData[hourCounter][LOW]);
                double previousCandleLow = parseDouble(hourData[hourCounter - 1][LOW]);
                double candleHigh = parseDouble(hourData[hourCounter][HIGH]);
                double previousCandleHigh = parseDouble(hourData[hourCounter - 1][HIGH]);
                double previousCandleClose = parseDouble(hourData[hourCounter - 1][CLOSE]);

                double yesterdaysLow = parseDouble(hourData[hourCounter][DAILY_LOW]);
                double yesterdaysHigh = parseDouble(hourData[hourCounter][DAILY_HIGH]);

                double LastBarSize = previousCandleClose - previousCandleLow;

                boolean takeOutYesterdaysLow = candleLow < yesterdaysLow;
                boolean closePositive = candleClose > candleOpen;
                boolean closeAboveYesterdaysLow = candleClose > yesterdaysLow;
                boolean openAboveYesterdaysLow = candleOpen > yesterdaysLow;

                boolean takeOutYesterdaysHigh = candleHigh > yesterdaysHigh;
                boolean closeNegative = candleClose < candleOpen;
                boolean closeBelowYesterdaysHigh = candleClose < yesterdaysHigh;
                boolean openBelowYesterdaysLow = candleOpen < yesterdaysHigh;


                if (!availableToTrade) {

                    if (target > stop) {

                        if (candleClose <= stop) {
                            int profitLoss = convertTicksToInt(stop - entry);
                            tickCounter+=profitLoss;
                            losers++;
                            availableToTrade = true;
                            System.out.printf("Close long entry: %s target: %.5f stop: %.5f for loss %s cumulative profit %s%n", entry, target, stop, profitLoss, tickCounter);
                        } else if (candleClose > target) {
                            final int profitLoss = convertTicksToInt(target - entry);
                            tickCounter += profitLoss;
                            winners++;
                            availableToTrade = true;
                            System.out.printf("Close long entry: %s target: %.5f stop: %.5f for win %s cumulative profit %s%n", entry, target, stop, profitLoss, tickCounter);
                        }
                    } else {

                        if (candleClose >= stop) {
                            final int profitLoss = convertTicksToInt(entry - stop);
                            tickCounter += profitLoss;
                            losers++;
                            availableToTrade = true;
                            System.out.printf("Close short entry: %s target: %.5f stop: %.5f for loss %s cumulative profit %s%n", entry, target, stop, profitLoss, tickCounter);
                        } else if (candleClose < target) {
                            final int profitLoss = convertTicksToInt(entry - target);
                            tickCounter += profitLoss;
                            winners++;
                            availableToTrade = true;
                            System.out.printf("Close short entry: %s target: %.5f stop: %.5f for win %s cumulative profit %s%n", entry, target, stop, profitLoss, tickCounter);
                        }
                    }
                }

                if (takeOutYesterdaysLow &&
                        closePositive &&
                        closeAboveYesterdaysLow &&
                        openAboveYesterdaysLow &&
                        getLowCheck(hourData[TODAYS_LOW], candleLow, previousCandleLow, 0, i) &&
                        timeToOpenPosition &&
                        availableToTrade) {

                    this.stop = candleClose + (candleClose - candleLow);
                    this.target = candleLow;
                    this.entry = candleClose;
                    availableToTrade = false;

                }

                if (takeOutYesterdaysHigh &&
                        closeNegative &&
                        closeBelowYesterdaysHigh &&
                        openBelowYesterdaysLow &&
                        getHighCheck(hourData[TODAYS_HIGH], candleHigh, previousCandleHigh, 0, i) &&
                        timeToOpenPosition &&
                        availableToTrade) {


                    this.stop = candleClose - (candleHigh - candleClose);
                    this.target = candleHigh;
                    this.entry = candleClose;
                    availableToTrade = false;
                }
            }

            timeToOpenPosition = false;

        }

//        for (String[] line : hourData) {
//            System.out.println(line[DATE] + line[OPEN] + line[LOW] + line[HIGH] + line[CLOSE] + line[DAILY_LOW] +
//                    line[DAILY_HIGH]);
//        }

        return new Results(tickCounter, winners, losers);
    }

    private int convertTicksToInt(double doubleTicks) {
        return Math.toIntExact(Math.round(doubleTicks * 10000));
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
