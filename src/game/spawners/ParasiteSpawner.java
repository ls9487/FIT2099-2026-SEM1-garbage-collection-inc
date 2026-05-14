package game.spawners;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.actors.Parasite;

/**
 * ParasiteSpawner is a spawner for the Parasite.
 * Give it a location, and it'll try to spawn a Parasite there.
 * If successful, it triggers an environmental reaction (adjacent workers take 2 damage).
 */
public class ParasiteSpawner implements Spawner {

    /**
     * Spawns a parasite at the given location.
     * Successful spawning triggers an environmental reaction.
     * @param location The location for the parasite to be spawned at.
     */
    @Override
    public void spawnAt(Location location) {
        Parasite spawnedParasite = new Parasite();
        // try/catch required by IntelliJ.
        try {
            location.addActor(spawnedParasite);
            // At this point, spawning was successful. Trigger the environmental reaction.
            this.hurtAdjacentActors(location);

        } catch (GameEngineException ignored) {
            // Spawn failed (likely because location was occupied).
            // It's fine to proceed, this attempt is just ignored.
        }
    }

    /**
     * Upon successful spawning, check adjacent locations for those susceptible to the effect.
     * Affected actors will take 2 damage.
     * To be used internally within this class only.
     * @param location The location where the parasite was spawned.
     */
    private void hurtAdjacentActors(Location location) {
        final int ADJACENT_DAMAGE = 2;
        // Check each adjacent location.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            // Check if there's a susceptible actor. If so, deal damage to it.
            if (destination.containsAnActor() &&
                    destination.getActor().hasAbility(ActorAbilities.PARASITE_EFFECT_SUSCEPTIBLE)) {
                destination.getActor().hurt(ADJACENT_DAMAGE);
            }
        }
    }

}
