package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Cuttable is an interface representing something that can be cut with a Plasma Cutter.
 * The cutting logic and side effects are delegated to the implementing class.
 *
 * @author eche0116
 */
public interface Cuttable {

    /**
     * Called when an actor cuts this object with a Plasma Cutter.
     * Implementations handle dropping items, transforming tiles, spawning actors, etc.
     *
     * @param actor The actor performing the cut.
     * @param map   The map the actor is on.
     * @return A string description of what happened.
     */
    String cutBy(Actor actor, GameMap map);
}