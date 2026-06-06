package game.spawners;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.ScrapSnatcher;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * ScrapSnatcherSpawner is a spawner for the ScrapSnatcher.
 * Give it a location, and it'll try to spawn an ScrapSnatcher there.
 * If successful, it triggers an environmental reaction (spawn depositable at the
 * surrounding of scrap snatcher).
 *
 * @author lden0031
 * @version 1.0
 */
public class ScrapSnatcherSpawner implements Spawner {
    private static final Random random = new Random();
    private List<Supplier<Item>> depositable;

    /**
     * Creates a spawner that drops random depositable items after a successful spawn.
     *
     * @param depositable suppliers for items scattered on spawn
     */
    public ScrapSnatcherSpawner(List<Supplier<Item>> depositable) {
        this.depositable = depositable;
    }
    /**
     * Spawns an ScrapSnatcher at the given location.
     * Successful spawning triggers an environmental reaction.
     * @param location The location for the ScrapSnatcher to be spawned at.
     */
    @Override
    public void spawnAt(Location location) {
        ScrapSnatcher spawnedScrapSnatcher = new ScrapSnatcher();
        // try/catch required by IntelliJ.
        try {
            location.addActor(spawnedScrapSnatcher);
            // At this point, spawning was successful. Trigger the environmental reaction.
            this.lootExplosion(spawnedScrapSnatcher, location);

        } catch (GameEngineException ignored) {
            // Spawn failed (likely because location was occupied).
            // It's fine to proceed, this attempt is just ignored.
        }
    }

    /**
     * Upon successful spawning, it will spawn depositable at the surrounding of scrap snatcher
     *
     * @param spawnedScrapSnatcher The ScrapSnatcher that was spawned.
     * @param location The location where the ScrapSnatcher was spawned.
     */
    private void lootExplosion(ScrapSnatcher spawnedScrapSnatcher, Location location) {
        for (Location here : location.getNearbyLocations(1)) {
            if (here.canActorEnter(spawnedScrapSnatcher)) {
                Item item = depositable.get(random.nextInt(depositable.size())).get();
                here.addItem(item);
            }
        }
    }

}
