package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Unlockable is an interface representing something that can be unlocked.
 * Feels like I've seen this before... (bootcamp).
 *
 * @author echu0057
 */
public interface Unlockable {

    /**
     * This method lets the actor unlock this unlockable.
     *
     * @param actor The actor unlocking this unlockable.
     * @param map   The map where unlocking occurs (for spatial side effects).
     * @return A string description of the result of unlocking this unlockable.
     */
    String unlockedBy(Actor actor, GameMap map);

}