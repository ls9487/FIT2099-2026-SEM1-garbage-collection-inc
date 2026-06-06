package game.spawners;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Spawner is an interface for implementing classes to handle the logic of spawning an actor.
 * It might not be as trivial as just placing the spawned actor onto a given location.
 *
 * @author echu0057
 */
public interface Spawner {

    /**
     * This method will have the spawner spawn an actor at the given location.
     * Which actor to spawn, and what side effects happen from spawning is left up to
     * the classes implementing this interface.
     * @param location The location for the actor to be spawned at.
     */
    public void spawnAt(Location location);

}
