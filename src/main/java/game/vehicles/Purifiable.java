package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability for grounds that can be purified by a rider with
 * VehicleAbilities.PURIFY, such as toxic waste turned into dirt.
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Purifiable {
    /**
     * Purifies this ground tile, replacing it with the given purified ground.
     * Purification may poison nearby actors when toxic waste leaks.
     *
     * @param actor the actor performing the purification
     * @param map the map containing the tile
     * @param purifiedGround the ground type to place after purification
     * @return a narrative description of the purification outcome
     */
    public String purify(Actor actor, GameMap map, Ground purifiedGround);
}
