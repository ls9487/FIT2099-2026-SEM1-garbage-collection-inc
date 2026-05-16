package game.behaviours;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.trees.TreeStatistics;
import game.actors.ActorAbilities;
import game.spawners.Spawner;
import game.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Spawns creatures on adjacent walkable tiles when a worker stands beside the tree.
 *
 * @author lden0031
 * @version 1.0
 */
public class SpawnBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();
    private List<Spawner> spawners;

    /**
     * @param spawners pool used to create each spawned actor
     */
    public SpawnBehaviour(List<Spawner> spawners) {
        this.spawners = spawners;
    }

    /**
     * Spawns creatures on adjacent walkable tiles when a worker stands beside the tree.
     * @param entity   the tree performing the behaviour
     * @param location the tree's tile
     * @return TRUE when spawns occurred, FALSE when no worker is adjacent, or null if not applicable
     */
    @Override
    public Boolean operate(Tree entity, Location location) {
        
        // Single-thread rule: if grow is possible this turn, don't also spawn
        int numberOfNearbyWorkers = 0;
        Actor worker = null;

        for (Location destination : location.getNearbyLocations(1)) {

            if (destination.containsAnActor() && destination.getActor().hasAbility(ActorAbilities.TREE_ACTIVATOR)) {
                numberOfNearbyWorkers++;
                worker = destination.getActor();
            }
        }

        if (numberOfNearbyWorkers == 0) {
            return Boolean.FALSE;
        }

        List<Location> spawnableLocations = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor() && destination.canActorEnter(worker)) {
                spawnableLocations.add(destination);
            }
        }

        for (int i = 0; i < numberOfNearbyWorkers && !spawnableLocations.isEmpty(); i++) {
            int chosenDestinationIndex = random.nextInt(spawnableLocations.size());
            Location destination = spawnableLocations.get(chosenDestinationIndex);

            spawners.get(random.nextInt(spawners.size())).spawnAt(destination);
            spawnableLocations.remove(chosenDestinationIndex);
        }
        return Boolean.TRUE;
    }
}
