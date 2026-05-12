package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statuses.BurnStatus;
import game.statuses.Flammable;
import game.statuses.Infectable;

import java.util.Random;


/**
 * Lantern is a class representing a lantern.
 * Except this lantern isn't so well-made. Has a low chance of leaking its burning fuel.
 *
 * @author echu0057
 */
public class Lantern extends EclipseItem implements Sellable, Infectable {

    /**
     * Constructor for the Lantern class.
     * Has a weight of 7 units, and an oil fuel (as durability) of 10 units.
     */
    public Lantern() {
        super("Lantern", '&', 7);
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(10));
    }

    /**
     * Inform the Lantern of the passage of time ONLY when being carried.
     * So once per turn, if it has oil, it has a 5% chance of leaking fire.
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        // Need to have non-zero durability, and a 5% chance check to leak.
        if (this.getStatistic(ItemStatistics.DURABILITY) > 0 && Math.random() <= 0.05) {
            this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
            // Create fire on currentLocation.
            currentLocation.addItem(new Fire());
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
        return 5 * this.getStatistic(ItemStatistics.DURABILITY);
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

        if (random.nextDouble() < 0.50) {
            Flammable flammable = seller.asCapability(Flammable.class).orElse(null);
            if (flammable != null) {
                seller.addStatus(new BurnStatus(3, 2, flammable));
            }
            msg.append(" The fuel sloshes and burns ").append(seller).append(" (2 dmg, 3 turns).");
        }

        if (random.nextDouble() < 0.25) {
            for (var adjacent : map.locationOf(seller).getNearbyLocations(1)) {
                adjacent.addItem(new Fire());
            }
            msg.append(" Sparks ignite the surrounding tiles.");
        }

        seller.getInventory().remove(this);
        return msg.toString();
    }

    /**
     * The infection finds the fuel yummy, draining it by 1 unit per turn.
     * Note that the "blowing up" doesn't actually affect its surroundings.
     */
    public void infection() {
        // Decrease its oil (durability) by 1. No need to check if it's greater than 0 or anything.
        // Due to how the engine's statistics work, it'll already prevent it going negative.
        this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
    }

}
