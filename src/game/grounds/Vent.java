package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Vent represents a vent on some location. Actors cannot enter it.
 * However, some creatures could emerge from this...
 * Spawns one creature every 20 turns.
 * What creatures it spawns should depend on the moon (GameMap).
 *
 * @author echu0057
 */
public class Vent extends Ground implements Spawner {

    // Keep the default constructors for spawnable creatures in this list.
    private List<Supplier<Actor>> spawnableActors;

    /**
     * Constructor for the Vent class.
     * @param spawnableActors A list of suppliers for actors (i.e. their default constructors).
     */
    public Vent(List<Supplier<Actor>> spawnableActors) {
        super('v', "Vent");
        this.spawnableActors = spawnableActors;
    }

    /**
     * Vent is motion-activated, and only to whatever actors it's sensitive to.
     * That includes the worker (and actually only the worker for now).
     * Every tick, if there's a surrounding worker, it'll spawn a creature.
     * @param location The location of the Ground.
     */
    @Override
    public void tick(Location location) {
        boolean ventActivated = false;
        // Check all the surrounding exits.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            // If there's an adjacent actor that can activate the vent,
            // the vent is activated for this turn.
            if (destination.containsAnActor() &&
                    destination.getActor().hasAbility(ActorAbilities.VENT_ACTIVATOR)) {
                ventActivated = true;
                break;
            }
        }
        // If activated, try to spawn.
        if (ventActivated) {
            boolean spawnSuccess = this.spawnRandomActor(this.spawnableActors, location);
            // Successful spawning will try to cause poisoning around the vent.
            if (spawnSuccess) {
                this.poisonAdjacent(location);
            }
        }
    }

    /**
     * Upon successful spawning, the vent poisons all actors adjacent to it.
     * Poison does 1 damage per turn, for 5 turns.
     * To be used internally within this class only.
     * @param location The location of the original hole.
     */
    private void poisonAdjacent(Location location) {
        final int POISON_DAMAGE = 1;
        final int POISON_DURATION = 5;
        final int POISON_RANGE = 1;

        // Get the adjacent locations.
        List<Location> adjacentLocations = location.getNearbyLocations(POISON_RANGE);
        for (Location adjacentLocation : adjacentLocations) {
            if (adjacentLocation.containsAnActor()) {
                // Actor is present at this adjacent location. Try to poison it.
                Actor adjacentActor = adjacentLocation.getActor();
                Poisonable poisonable = adjacentActor.asCapability(Poisonable.class).orElse(null);
                if (poisonable != null) {
                    // Poison the poisonable actor.
                    adjacentActor.addStatus(new PoisonStatus(POISON_DURATION, POISON_DAMAGE, poisonable));
                }
            }
        }
    }

    /**
     * Actors can't walk over a vent. They just can't.
     * @param actor The actor to check.
     * @return false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

}