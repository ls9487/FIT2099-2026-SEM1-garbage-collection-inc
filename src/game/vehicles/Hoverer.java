package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Capability for rideables that can perform the hover blast special action.
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Hoverer {
    /**
     * Destroys items and blows actors away within the hover blast radius.
     * Requires the rider to have VehicleAbilities.EXTRA_ENERGY
     *
     * @param actor the rider activating hover blast
     * @param map the map containing the rider
     * @return a narrative description of destroyed items and displaced actors
     */
    String hoverBlast(Actor actor, GameMap map);
}
