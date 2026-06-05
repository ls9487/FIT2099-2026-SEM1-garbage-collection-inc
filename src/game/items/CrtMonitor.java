package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * CrtMonitor is a class representing a CRT monitor.
 * Also ancient technology. Doesn't really have a purpose for now beyond testing weight limits.
 *
 * @author echu0057
 */
public class CrtMonitor extends EclipseItem implements Sellable {

    /** Credit value paid by the SuperComputer on sale. */
    private static final int SELL_PRICE = 25;

    /** Inventory weight in units (this thing is HEAVY). */
    private static final int WEIGHT = 30;

    /** Map display symbol for this monitor. */
    private static final char SYMBOL = '◙';

    /** HP the seller gains from the relief of offloading the monitor. */
    private static final int SELL_HEAL = 5;

    /** Chance the ancient hardware shorts out during sale. */
    private static final double SHORT_CHANCE = 0.20;

    /** Damage dealt to the seller when the monitor shorts out. */
    private static final int SHORT_DAMAGE = 2;

    /** Duration (in turns) of any Fire this monitor spawns. */
    private static final int FIRE_DURATION = 5;
    private final Random random = new Random();

    /**
     * Constructor for the CrtMonitor class.
     * Has a weight of 30 units, which is heavy!
     */
    public CrtMonitor() {
        super("CRT Monitor", SYMBOL, WEIGHT);
    }

    /**
     * Twenty-five credits, fixed. Heaviest single-payout item in the game by weight ratio.
     * @author esoo0013
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Selling unconditionally heals 5 HP from the relief of finally putting
     * down 30 units of dead weight. Then there's a 20% chance the ancient
     * hardware shorts out: 2 damage to the seller, fire on every neighbour.
     * The monitor leaves the inventory at the end of the transaction.
     * @param seller The actor doing the selling.
     * @param map The map the seller is on.
     * @return A full sentence describing the sale and its effects.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map, Location superComputerLocation) {
        seller.heal(SELL_HEAL);
        StringBuilder msg = new StringBuilder(seller + " sells the CRT monitor for "
                + getSellPrice() + " credits. Offloading it heals " + SELL_HEAL + " hp.");

        // 20% chance for the terminal to short.
        if (random.nextDouble() < SHORT_CHANCE) {
            // If it does, deal 2 damage and ignite the surrounding locations.
            seller.hurt(SHORT_DAMAGE);
            for (Location adjacent : map.locationOf(seller).getNearbyLocations(1)) {
                adjacent.addItem(new Fire(FIRE_DURATION));
            }
            msg.append(" The terminal shorts out, dealing ").append(SHORT_DAMAGE)
               .append(" damage and igniting the area!");
        }

        seller.getInventory().remove(this);
        return msg.toString();
    }

}
