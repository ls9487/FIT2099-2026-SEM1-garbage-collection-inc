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

public class SpawnBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();
    private List<Spawner> spawners;

    public SpawnBehaviour(List<Spawner> spawners) {
        this.spawners = spawners;
    }

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
