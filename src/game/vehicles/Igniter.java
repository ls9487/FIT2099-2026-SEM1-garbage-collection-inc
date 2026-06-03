package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Capability for rideable upgrades that can ignite an adjacent actor.
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Igniter {
    /**
     * Removes a random item from the rider's inventory and places fire under the target.
     *
     * @param actor the rider using the igniter upgrade
     * @param map the map containing the actors
     * @param target the adjacent actor to ignite
     * @return a narrative description of the ignite outcome
     */
    String ignite(Actor actor, GameMap map, Actor target);
}
