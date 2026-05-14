package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.spawners.Spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hole represents a hole on some location. Actors can't enter it, since they'll fall into it.
 * However, some creatures could emerge from this...
 * Spawns one creature every 20 turns.
 * What creatures it spawns should depend on the moon (GameMap).
 *
 * @author echu0057
 */
public class Hole extends Ground {

    private static final Random random = new Random();
    private static final double HOLE_EXPANSION_CHANCE = 0.01;
    // Keeps a list of spawners so they can be used to spawn creatures.
    private List<Spawner> spawners;

    /**
     * Constructor for the Hole class.
     * Has a cooldown of 20 turns between spawning creatures.
     * @param spawners A list of spawners for actors.
     */
    public Hole(List<Spawner> spawners) {
        super('o', "Hole");
        this.spawners = spawners;
        this.addNewStatistic(GroundStatistics.COOLDOWN, new BaseStatistic(20));
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
            this.spawn(location);
            // Regardless of the outcome, reset cooldown.
            this.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE,
                    this.getMaximumStatistic(GroundStatistics.COOLDOWN));
        }
    }

    /**
     * Randomly chooses an actor to be spawned (based on what it can spawn).
     * The actor will try to be spawned directly on this hole (like in A1).
     * @param location The location of the hole.
     */
    private void spawn(Location location) {
        if (!location.containsAnActor()) {
            // The hole itself is currently unoccupied. Choose a random spawner and make it spawn.
            Spawner chosenSpawner = spawners.get(random.nextInt(spawners.size()));
            chosenSpawner.spawnAt(location);
            // The spawn was done, and we'll need to see if the hole expands.
            this.expandHole(location);
        }
    }

    /**
     * Upon successful spawning, it has a small chance (1%) to expand.
     * This converts an adjacent ground into a hole, inherting the same spawnable actors.
     * To be used internally within this class only.
     * @param location The location of the original hole.
     */
    private void expandHole(Location location) {
        if (random.nextDouble() <= HOLE_EXPANSION_CHANCE) {
            final int EXPANSION_RANGE = 1;
            // Get the adjacent locations (no need to check for anything else).
            // Then, randomly choose one.
            List<Location> adjacentLocations = location.getNearbyLocations(EXPANSION_RANGE);
            Location expandedLocation = adjacentLocations.get(random.nextInt(adjacentLocations.size()));
            // That location's ground is now a hole with the same spawners. Note the defensive copy.
            List<Spawner> spawnersCopy = new ArrayList<>(spawners);
            expandedLocation.setGround(new Hole(spawnersCopy));
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
