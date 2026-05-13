package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Items that can materialise one or more actors near a reference location.
 *
 * @author eche0116
 * @version 1.0
 */
public interface Spawner {

    /**
     * Spawns the associated actor(s) near the given origin tile.
     *
     * @param spawnLocation the reference location, usually the owner's tile
     */
    String spawn(Location spawnLocation);

    /**
     * get random spawnable actor from all spawnable actors
     * @param spawnableActors the spawnable actors to randomly pick 1
     * @param random used to random pick spawnable actors
     * @return chosen spawnable actor
     */
    default Actor getRamdomSpawnableActor(List<Supplier<Actor>> spawnableActors, Random random) {
        return spawnableActors.get(random.nextInt(spawnableActors.size())).get();
    }
}