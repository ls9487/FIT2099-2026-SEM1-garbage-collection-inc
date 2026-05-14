package game.items;

import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.actors.EclipseStatistics;

import java.util.Random;

/**
 * FloppyDisk is a class representing a floppy disk.
 * Ancient technology. Doesn't really have a purpose for now.
 *
 * @author echu0057
 */
public class FloppyDisk extends EclipseItem implements Sellable {
    private static final Random random = new Random();
    private static final int SELL_PRICE = 1;
    private static final int GLITCH_DEDUCTION = 50;
    private static final double GLITCH_CHANCE = 0.5;

    /**
     * Constructor for the FloppyDisk class.
     * Has a weight of 1 unit.
     */
    public FloppyDisk() {
        super("Floppy Disk", '⊟', 1);
    }

    /**
     * Sells for 1 credit. Although, the SuperComputer MIGHT glitch.
     * @author esoo0013
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * 50% chance the SuperComputer glitches AFTER paying out and snatches
     * 50 credits straight out of the wallet. Worker has been warned.
     * The disk leaves the inventory regardless of whether the glitch fires.
     * @param seller The actor doing the selling.
     * @param map The map the seller is on.
     * @return A full sentence describing the sale and its effects.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        StringBuilder msg = new StringBuilder(seller + " sells the floppy disk for "
                + getSellPrice() + " credit.");

        // Check if a glitch happens (50% chance) and pocket 50 credits from the seller.
        if (random.nextDouble() < GLITCH_CHANCE && seller.hasStatistic(EclipseStatistics.CREDITS)) {
            seller.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.DECREASE,
                    GLITCH_DEDUCTION);
            msg.append(" But the terminal glitches and pockets some credits?!");
        }

        seller.getInventory().remove(this);
        return msg.toString();
    }

}