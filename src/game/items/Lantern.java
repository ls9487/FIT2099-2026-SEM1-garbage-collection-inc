package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Lantern is a class representing a lantern.
 * Except this lantern isn't so well-made. Has a low chance of leaking its burning fuel.
 *
 * @author echu0057
 */
public class Lantern extends EclipseItem {

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

}
