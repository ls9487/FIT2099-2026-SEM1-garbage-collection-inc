package game.behaviours;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.items.Spawner;
import game.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class SpawnBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();
    private Spawner spawner;
    private Actor spawnedActor;

    public SpawnBehaviour(Spawner spawner, Actor spawnedActor) {
        this.spawner = spawner;
        this.spawnedActor = spawnedActor;
    }

    @Override
    public Boolean operate(Tree entity, Location location) {
        int numberOfNearbyWorkers = 0;
        for (Location destination : location.getNearbyLocations(1)) {
            if (destination.containsAnActor() && destination.getActor().hasAbility(ActorAbilities.PLAYER)) {
                numberOfNearbyWorkers++;
            }
        }

        if (numberOfNearbyWorkers == 0) {
            return Boolean.FALSE;
        }

        List<Location> spawnableLocations = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor() && destination.canActorEnter(spawnedActor)) {
                spawnableLocations.add(destination);
            }
        }

        for (int i = 0; numberOfNearbyWorkers > i; i++) {
            int chosenDestinationIndex = random.nextInt(spawnableLocations.size());
            Location destination = spawnableLocations.get(chosenDestinationIndex);

            spawner.spawn(destination);
            spawnableLocations.remove(chosenDestinationIndex);
        }
        return Boolean.TRUE;
    }
}
