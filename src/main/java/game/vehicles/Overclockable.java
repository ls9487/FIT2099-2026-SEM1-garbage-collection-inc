package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Capability for rideables that support the overclock special action.
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Overclockable {
    /**
     * Sacrifices up to five hit points to destroy nearby tiles with dirt and fire.
     *
     * @param actor the rider overclocking the vehicle
     * @param map the map containing the rider
     * @return a narrative description of the overclock outcome
     */
    String overclock(Actor actor, GameMap map);
}
