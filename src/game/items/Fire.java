package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.statuses.BurnStatus;
import game.statuses.Flammable;

/**
 * Fire represents something that's combusting.
 * Very dangerous to be around, avoid touching it. At least it doesn't burn forever.
 * It also can't be picked up, why would you anyway?
 *
 * @author echu0057
 */
public class Fire extends EclipseItem {

    /**
     * Constructor for the Fire class. This should stay on the ground.
     * Weightless and non-portable. Burns for x turns.
     */
    public Fire(int duration) {
        super("Fire", '^', 0);
        this.makeNonPortable();
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(duration));
    }

    /**
     * Inform a fire on the ground of the passage of time.
     * This method is called once per turn.
     * @param currentLocation The location of the ground on which the fire is on.
     */
    @Override
    public void tick(Location currentLocation) {
        // Check if currentLocation contains an actor. If yes, apply burn to them.
        if (currentLocation.containsAnActor()) {
            Actor actor = currentLocation.getActor();
            Flammable flammable = currentLocation.getActorAs(Flammable.class);
            if (flammable != null) {
                // Burn the flammable actor (1 damage, lasts 5 turns).
                actor.addStatus(new BurnStatus(5, 1, flammable));
            }
        }
        // Decrease its durability and extinguish (remove) itself if it reaches 0.
        this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
        if (this.getStatistic(ItemStatistics.DURABILITY ) == 0) {
            currentLocation.removeItem(this);
        }
    }

}
