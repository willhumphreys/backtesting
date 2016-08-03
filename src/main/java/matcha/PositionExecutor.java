package matcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionExecutor {

    private final Signals signals;

    private String entryDate;
    private String exitDate;
    private double entry;
    private double stop;
    private double target;


    private boolean availableToTrade;
    private boolean timeToOpenPosition;

    @Autowired
    public PositionExecutor(Signals signals) {
        this.signals = signals;
        entry = -1.0;
        stop = -1.0;
        target = -1.0;
        availableToTrade = true;
    }


    public void placePositions(UsefulTickData usefulTickData)  {
        if (signals.isShortSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

            stop = usefulTickData.getCandleClose() + (usefulTickData.getCandleClose() - usefulTickData.getCandleLow());
            target = usefulTickData.getCandleLow();
            entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();
            availableToTrade = false;

        }

        if (signals.isLongSignal(usefulTickData) && timeToOpenPosition && availableToTrade) {

            stop = usefulTickData.getCandleClose() - (usefulTickData.getCandleHigh() - usefulTickData.getCandleClose());
            target = usefulTickData.getCandleHigh();
            entry = usefulTickData.getCandleClose();
            entryDate = usefulTickData.getCandleDate();
            availableToTrade = false;
        }
    }

    public void setTimeToOpenPosition(boolean timeToOpenPosition) {
        this.timeToOpenPosition = timeToOpenPosition;
    }

    public void managePosition(UsefulTickData usefulTickData, Simulation simulation) {
        if (!availableToTrade) {
            if (target > stop) {

                if (usefulTickData.getCandleClose() <= stop) {
                    int profitLoss = simulation.utils.convertTicksToInt(stop - entry);
                    simulation.tickCounter+=profitLoss;
                    simulation.losers++;
                    availableToTrade = true;
                    exitDate = usefulTickData.getCandleDate();
                    System.out.printf("Close long %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, stop, profitLoss, simulation.tickCounter);
                } else if (usefulTickData.getCandleClose() > target) {
                    final int profitLoss = simulation.utils.convertTicksToInt(target - entry);
                    simulation.tickCounter += profitLoss;
                    simulation.winners++;
                    availableToTrade = true;
                    System.out.printf("Close long %s %.5f target: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, target, profitLoss, simulation.tickCounter);
                }
            } else {

                if (usefulTickData.getCandleClose() >= stop) {
                    final int profitLoss = simulation.utils.convertTicksToInt(entry - stop);
                    simulation.tickCounter += profitLoss;
                    simulation.losers++;
                    availableToTrade = true;
                    System.out.printf("Close short %s %.5f stopped: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, stop, profitLoss, simulation.tickCounter);
                } else if (usefulTickData.getCandleClose() < target) {
                    final int profitLoss = simulation.utils.convertTicksToInt(entry - target);
                    simulation.tickCounter += profitLoss;
                    simulation.winners++;
                    availableToTrade = true;
                    System.out.printf("Close short %s %.5f target: %s %.5f ticks %d cumulative profit %d%n",
                            entryDate, entry, exitDate, target, profitLoss, simulation.tickCounter);
                }
            }
        }
    }
}
