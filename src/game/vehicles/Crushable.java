package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability for grounds or objects that can be crushed into dirt by a rider
 * with VehicleAbilities.CRUSH
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Crushable {
    /**
     * Crushes this target, typically replacing impassable ground with dirt.
     *
     * @param actor the actor performing the crush
     * @param map the map containing the crush site
     * @return a narrative description of the crush outcome
     */
    String crush(Actor actor, GameMap map, Location location);
}
