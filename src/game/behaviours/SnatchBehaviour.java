package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.PickUpAction;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Depositable;

/**
 * Behaviour that picks up the first depositable portable item on the actor's tile.
 *
 * @author lden0031
 * @version 1.0
 */
public class SnatchBehaviour implements Behaviour<Actor, Action> {
    /**
     * Attempts to pick up a depositable item at the actor's location.
     *
     * @param entity   the actor performing the snatch
     * @param location the tile the actor occupies
     * @return a pick-up action for the first depositable item found, or null if none
     */
    @Override
    public Action operate(Actor entity, Location location) {

        for (Item item : location.getItems()) {
            Depositable depositable = item.asCapability(Depositable.class).orElse(null);
            if (depositable != null) {
                return new PickUpAction(item);
            }
        }
        return null;
    }
}
