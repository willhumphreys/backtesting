package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class Simulation {

    private static final int DATE = 0;

    private String entryDate;
    private String exitDate;
    private double entry;
    private double stop;
    private double target;

    private boolean availableToTrade;
    private boolean timeToOpenPosition;

    private int tickCounter;
    private int winners;
    private int losers;


    private DateTimeFormatter formatter;

    private final Utils utils;

    private final HighAndLowChecks highAndLowChecks;

    @Autowired
    public Simulation(HighAndLowChecks highAndLowChecks, Utils utils) {
        this.utils = utils;
        this.highAndLowChecks = highAndLowChecks;
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

                UsefulTickData usefulTickData = new UsefulTickData(hourData, hourCounter).invoke();

                if (!availableToTrade) {
                    if (target > stop) {

                        if (usefulTickData.getCandleClose() <= stop) {
                            int profitLoss = utils.convertTicksToInt(stop - entry);
                            tickCounter+=profitLoss;
                            losers++;
                            availableToTrade = true;
                            exitDate = usefulTickData.getCandleDate();
                            System.out.printf("Close long %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n",
                                    entryDate, entry, exitDate, stop, profitLoss, tickCounter);
                        } else if (usefulTickData.getCandleClose() > target) {
                            final int profitLoss = utils.convertTicksToInt(target - entry);
                            tickCounter += profitLoss;
                            winners++;
                            availableToTrade = true;
                            System.out.printf("Close long %s %.5f target: %s %.5f ticks %d cumulative profit %d%n",
                                    entryDate, entry, exitDate, target, profitLoss, tickCounter);
                        }
                    } else {

                        if (usefulTickData.getCandleClose() >= stop) {
                            final int profitLoss = utils.convertTicksToInt(entry - stop);
                            tickCounter += profitLoss;
                            losers++;
                            availableToTrade = true;
                            System.out.printf("Close short %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n",
                                    entryDate, entry, exitDate, stop, profitLoss, tickCounter);
                        } else if (usefulTickData.getCandleClose() < target) {
                            final int profitLoss = utils.convertTicksToInt(entry - target);
                            tickCounter += profitLoss;
                            winners++;
                            availableToTrade = true;
                            System.out.printf("Close short %s %.5f target: %s %.5f ticks %d cumulative profit %d%n",
                                    entryDate, entry, exitDate, target, profitLoss, tickCounter);
                        }
                    }
                }

                if (isShortSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

                    this.stop = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData.getCandleLow());
                    this.target = usefulTickData.getCandleLow();
                    this.entry = usefulTickData.getCandleClose();
                    this.entryDate = usefulTickData.getCandleDate();
                    availableToTrade = false;

                }

                if (isLongSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

                    this.stop = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData.getCandleClose());
                    this.target = usefulTickData.getCandleHigh();
                    this.entry = usefulTickData.getCandleClose();
                    this.entryDate = usefulTickData.getCandleDate();
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

    private boolean isLongSignal(UsefulTickData usefulTickData) {
        return usefulTickData.isTakeOutYesterdaysHigh() &&
                usefulTickData.isCloseNegative() &&
                usefulTickData.isCloseBelowYesterdaysHigh() &&
                usefulTickData.isOpenBelowYesterdaysLow() &&
                highAndLowChecks.getHighCheck(usefulTickData, 0);
    }

    private boolean isShortSignal(UsefulTickData usefulTickData) {
        return usefulTickData.isTakeOutYesterdaysLow() &&
                usefulTickData.isClosePositive() &&
                usefulTickData.isCloseAboveYesterdaysLow() &&
                usefulTickData.isOpenAboveYesterdaysLow() &&

                highAndLowChecks.getLowCheck(usefulTickData, 0);
    }


}
