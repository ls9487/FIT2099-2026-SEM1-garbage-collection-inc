package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Unlockable is an interface representing something that can be unlocked.
 * Feels like I've seen this before... (bootcamp).
 *
 * @author echu0057
 */
public interface Unlockable {

    /**
     * This method lets the actor unlock this unlockable.
     * @param actor The actor unlocking this unlockable.
     * @return A string description of the result of unlocking this unlockable.
     */
    public String unlockedBy(Actor actor);

}
