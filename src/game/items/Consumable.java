package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Consumable is an interface representing an item that can be consumed.
 * Feels like I've seen this before... (bootcamp).
 *
 * @author echu0057
 */
public interface Consumable {

    /**
     * This method lets the actor consume this consumable.
     * @param actor The actor consuming this consumable.
     * @return A string description of the result of consuming this consumable.
     */
    public String consumedBy(Actor actor, GameMap map);

}
