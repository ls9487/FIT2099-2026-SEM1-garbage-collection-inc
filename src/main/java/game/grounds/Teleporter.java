package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Marker for teleportation devices that expose multiple destination choices.
 *
 * @author eche0116
 * @version 1.0
 */
public interface Teleporter {
    /**
     * teleports actor from current location to destination
     * @param actor the actor being teleported
     * @param map the map the actor at before teleport
     * @param destination the teleport destination
     * @return teleport message
     */
    String teleport(Actor actor, GameMap map, Location destination);
}