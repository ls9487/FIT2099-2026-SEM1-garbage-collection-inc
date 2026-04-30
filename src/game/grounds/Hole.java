package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Hole represents a hole on some location. Actors can't enter it, since they'll fall into it.
 * However, some creatures could emerge from this...
 * Spawns one creature every 20 turns.
 * What creatures it spawns should depend on the moon (GameMap).
 *
 * @author echu0057
 */
public class Hole extends Ground {

    private final Random random = new Random();
    // The idea is to keep the default constructors for spawnable creatures in this list.
    private List<Supplier<Actor>> spawnableActors;

    /**
     * Constructor for the Hole class.
     * Has a cooldown of 20 turns between spawning creatures.
     * @param spawnableActors A list of suppliers for actors (i.e. their default constructors).
     */
    public Hole(List<Supplier<Actor>> spawnableActors) {
        super('o', "Hole");
        this.spawnableActors = spawnableActors;
        this.addNewStatistic(GroundStatistics.COOLDOWN, new BaseStatistic(20));
    }

    /**
     * Spawns an actor based on what this hole can spawn. Chooses randomly.
     * To be used internally within this class only.
     */
    private void spawnActor(Location location) {
        // Make sure the list is non-empty and location is unoccupied. Does nothing if empty.
        if (!spawnableActors.isEmpty() && !location.containsAnActor()) {
            // Choose a random index of the list, create the actor based on it.
            Actor spawnedActor = spawnableActors.get(random.nextInt(spawnableActors.size())).get();
            // Then, place the actor onto the map.
            // Because addActor could throw an exception, IntelliJ requires me to do this...
            try {
                location.addActor(spawnedActor);
            } catch (GameEngineException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Hole will experience time and may spawn a creature when it's ready to do so.
     * @param location The location of the Ground.
     */
    @Override
    public void tick(Location location) {
        this.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.DECREASE, 1);
        // Check if it's ready to spawn a creature.
        if (this.getStatistic(GroundStatistics.COOLDOWN) == 0) {
            this.spawnActor(location);
            // Reset cooldown.
            this.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE,
                    this.getMaximumStatistic(GroundStatistics.COOLDOWN));
        }
    }

    /**
     * Creatures can crawl out of a hole, but they can't walk over it, can they?
     * @param actor The actor to check.
     * @return false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

}
