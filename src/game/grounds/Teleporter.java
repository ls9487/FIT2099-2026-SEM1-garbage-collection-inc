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
    String teleport(Actor actor, GameMap map, Location destination);
}