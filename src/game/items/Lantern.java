package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statuses.BurnStatus;
import game.statuses.Flammable;
import java.util.Random;


/**
 * Lantern is a class representing a lantern.
 * Except this lantern isn't so well-made. Has a low chance of leaking its burning fuel.
 *
 * @author echu0057
 */
public class Lantern extends EclipseItem implements Sellable {

    /** Inventory weight in units. */
    private static final int WEIGHT = 7;

    /** Map display symbol for this lantern. */
    private static final char SYMBOL = '&';

    /** Oil units a fresh lantern holds (stored as DURABILITY). */
    private static final int INITIAL_OIL = 10;

    /** Credits paid per remaining oil unit on sale. */
    private static final int CREDITS_PER_OIL = 5;

    /** Per-turn chance, while carried, of leaking fuel onto the carrier's tile. */
    private static final double LEAK_CHANCE = 0.05;

    /** Oil consumed by a single leak event. */
    private static final int LEAK_OIL_COST = 1;

    /** Chance the seller catches a burn when the lantern is sold. */
    private static final double SELLER_BURN_CHANCE = 0.50;

    /** Chance fire spawns on every adjacent tile when the lantern is sold. */
    private static final double ADJACENT_FIRE_CHANCE = 0.25;

    /** Burn-status duration applied to a torched seller. */
    private static final int SELLER_BURN_DURATION = 3;

    /** Burn-status damage per turn applied to a torched seller. */
    private static final int SELLER_BURN_DAMAGE = 2;

    /** Duration (in turns) of any Fire this lantern spawns. */
    private static final int FIRE_DURATION = 5;

    /**
     * Constructor for the Lantern class.
     * Has a weight of 7 units, and an oil fuel (as durability) of 10 units.
     */
    public Lantern() {
        super("Lantern", SYMBOL, WEIGHT);
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(INITIAL_OIL));
    }

    /**
     * Inform the Lantern of the passage of time ONLY when being carried.
     * So once per turn, if it has oil, it has a 5% chance of leaking fire.
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        // Need to have non-zero durability, and the leak chance check.
        if (this.getStatistic(ItemStatistics.DURABILITY) > 0 && Math.random() <= LEAK_CHANCE) {
            this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, LEAK_OIL_COST);
            // Create fire on currentLocation.
            currentLocation.addItem(new Fire(FIRE_DURATION));
        }
    }

    private final Random random = new Random();

    /**
     * Five credits per oil unit remaining. It caps at 50 for a full lantern,
     * so if it leaks it will be less.
     * @author esoo0013
     */
    @Override
    public int sellPrice(Actor seller) {
        return CREDITS_PER_OIL * this.getStatistic(ItemStatistics.DURABILITY);
    }

    /**
     * Two independent rolls when the lantern leaves the seller's hands.
     * 50% chance the seller gets a 3-turn 2-dmg burn from the remaining fuel.
     * 25% chance fire flashes onto every adjacent tile.
     * Both can fire on the SAME transaction. The lantern always leaves the
     * inventory at the end of the transaction.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        int price = sellPrice(seller);
        StringBuilder msg = new StringBuilder(seller + " sells the lantern for " + price + " credits.");

        if (random.nextDouble() < SELLER_BURN_CHANCE) {
            Flammable flammable = seller.asCapability(Flammable.class).orElse(null);
            if (flammable != null) {
                seller.addStatus(new BurnStatus(SELLER_BURN_DURATION, SELLER_BURN_DAMAGE, flammable));
            }
            msg.append(" The fuel sloshes and burns ").append(seller).append(" (")
               .append(SELLER_BURN_DAMAGE).append(" dmg, ")
               .append(SELLER_BURN_DURATION).append(" turns).");
        }

        if (random.nextDouble() < ADJACENT_FIRE_CHANCE) {
            for (var adjacent : map.locationOf(seller).getNearbyLocations(1)) {
                adjacent.addItem(new Fire(FIRE_DURATION));
            }
            msg.append(" Sparks ignite the surrounding tiles.");
        }

        seller.getInventory().remove(this);
        return msg.toString();
    }
}
