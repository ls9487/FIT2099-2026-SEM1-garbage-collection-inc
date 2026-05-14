package game.spawners;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Undead;

/**
 * UndeadSpawner is a spawner for the Undead.
 * Give it a location, and it'll try to spawn an Undead there.
 * If successful, it triggers an environmental reaction (spawned Undead gains 1 max hp
 * per adjacent actor around where it spawned).
 */
public class UndeadSpawner implements Spawner {

    /**
     * Spawns an undead at the given location.
     * Successful spawning triggers an environmental reaction.
     * @param location The location for the undead to be spawned at.
     */
    @Override
    public void spawnAt(Location location) {
        Undead spawnedUndead = new Undead();
        // try/catch required by IntelliJ.
        try {
            location.addActor(spawnedUndead);
            // At this point, spawning was successful. Trigger the environmental reaction.
            this.boostMaxHp(spawnedUndead, location);

        } catch (GameEngineException ignored) {
            // Spawn failed (likely because location was occupied).
            // It's fine to proceed, this attempt is just ignored.
        }
    }

    /**
     * Upon successful spawning, check adjacent locations for actors.
     * Each actor increases the recently spawned undead's max hp by 1.
     * To be used internally within this class only.
     * @param spawnedUndead The undead that was spawned.
     * @param location The location where the undead was spawned.
     */
    private void boostMaxHp(Undead spawnedUndead, Location location) {
        // +1 max hp per adjacent actor as specified.
        final int BOOST_PER_ACTOR = 1;
        // Check each adjacent location.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            // Each adjacent actor will boost the undead's max hp (by 1).
            if (destination.containsAnActor()) {
                spawnedUndead.modifyStatisticMaximum(ActorStatistics.HEALTH,
                        StatisticOperations.INCREASE, BOOST_PER_ACTOR);
            }
        }
    }

}
