package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.InfectAction;
import game.actors.EnvironmentalTriggerer;
import game.statuses.Infectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Spawner is an interface providing logic for spawning an actor.
 *
 * @author echu0057
 */
public interface Spawner {

    /**
     * Default implementation is to attempt to spawn an actor on a random valid
     * adjacent location of a given location.
     * @param actor The actor to be spawned.
     * @param sourceLocation The source of the spawner (will attempt to spawn around it).
     * @return A boolean denoting whether the spawn was successful.
     */
    default boolean spawnActor(Actor actor, Location sourceLocation) {
        final Random random = new Random();
        List<Location> validLocations = new ArrayList<>();

        // Get all the surrounding exits.
        for (Exit exit : sourceLocation.getExits()) {
            Location destination = exit.getDestination();
            // A location will only be valid for spawning if there isn't an actor there already.
            if (!destination.containsAnActor()) {
                validLocations.add(destination);
            }
        }

        // If the list is non-empty, choose a random location.
        if (!validLocations.isEmpty()) {
            Location spawningLocation = validLocations.get(random.nextInt(validLocations.size()));
            // Then, place the actor onto the map at the chosen location.
            // Because addActor could throw an exception, IntelliJ requires me to do this...
            try {
                spawningLocation.addActor(actor);
                // Check if the actor implements EnvironmentalTriggerer. If it does, that means
                // it has an effect upon spawning, which needs to be called.
                EnvironmentalTriggerer triggerer = actor.asCapability(EnvironmentalTriggerer.class).orElse(null);
                if (triggerer != null) {
                    triggerer.onSpawnEffect(spawningLocation);
                }
                // Actor was spawned successfully.
                return true;

            } catch (GameEngineException e) {
                // Something went wrong on the engine's end, so it failed.
                return false;
            }
        } else {
            // No valid locations to spawn, so it failed.
            return false;
        }
    }

    /**
     * Default implementation is to pick a random actor from the given list and try to spawn it.
     * @param spawnableActors A list of suppliers of actors that can be spawned.
     * @param sourceLocation The source of the spawner (will attempt to spawn around it).
     * @return A boolean denoting whether the spawn was successful.
     */
    default boolean spawnRandomActor(List<Supplier<Actor>> spawnableActors, Location sourceLocation) {
        final Random random = new Random();
        // Create a new actor from the list of suppliers. This will be the one spawned.
        Actor chosenActor = spawnableActors.get(random.nextInt(spawnableActors.size())).get();
        // Pass the spawning logic onto the spawnActor method. Return whatever it returns.
        return spawnActor(chosenActor, sourceLocation);
    }

}
