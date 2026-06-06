package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability for targets that can be pushed backward by a rider with VehicleAbilities.BULLDOZE
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Bulldozeable {
    /**
     * Pushes this target one tile away from the bulldozing actor, or applies collision damage.
     *
     * @param actor    the actor performing the bulldozer
     * @param map      the map containing both actors
     * @param location
     * @return a narrative description of the bulldoze outcome
     */
    String bulldoze(Actor actor, GameMap map, Location location);
}
