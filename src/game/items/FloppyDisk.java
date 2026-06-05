package game.items;

import edu.monash.fit2099.engine.positions.Location;
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

    private final Random random = new Random();

    /** Credit value paid by the SuperComputer on sale. */
    private static final int SELL_PRICE = 1;

    /** Inventory weight in units. */
    private static final int WEIGHT = 1;

    /** Map display symbol for this disk. */
    private static final char SYMBOL = '⊟';

    /** Chance the SuperComputer glitches and reclaims credits after paying out. */
    private static final double GLITCH_CHANCE = 0.5;

    /** Credits the SuperComputer reclaims on a glitch. */
    private static final int GLITCH_DEDUCTION = 50;

    /**
     * Constructor for the FloppyDisk class.
     * Has a weight of 1 unit.
     */
    public FloppyDisk() {
        super("Floppy Disk", SYMBOL, WEIGHT);
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
    public String soldBy(Actor seller, GameMap map, Location superComputerLocation) {
        StringBuilder msg = new StringBuilder(seller + " sells the floppy disk for "
                + getSellPrice() + " credit.");

        if (random.nextDouble() < GLITCH_CHANCE && seller.hasStatistic(EclipseStatistics.CREDITS)) {
            seller.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.DECREASE, GLITCH_DEDUCTION);
            msg.append(" The terminal glitches and pockets ").append(GLITCH_DEDUCTION).append(" credits.");
        }

        seller.getInventory().remove(this);
        return msg.toString();
    }
}