package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * PoisonStatus represents a status effect of being poisoned.
 * Each PoisonStatus is one stack. Something can have multiple PoisonStatus applied to them.
 * Lasts some number of turns, gradually killing the poisoned entity.
 * The mars example was used as a reference.
 *
 * @author echu0057
 */
public class PoisonStatus implements Status {

    private int duration;
    private int intensity;
    private Poisonable poisonable;

    /**
     * Constructor for the PoisonStatus class.
     * @param duration The number of ticks the poison lasts.
     * @param intensity The intensity (damage dealt per tick) of the poison.
     * @param poisonable The entity that is poisoned. This could be null.
     */
    public PoisonStatus(int duration, int intensity, Poisonable poisonable) {
        this.duration = duration;
        this.intensity = intensity;
        this.poisonable = poisonable;
    }

    /**
     * Called once per tick to update the status of the current entity.
     * Whatever the poison does is up to the entity, taking damage for example.
     * @param currEntity the entity this status is attached to
     * @param location   the entity's current tile
     */
    public void tickStatus(GameEntity currEntity, Location location) {
        // Note that currEntity can't actually be used. Instead, poisonable is used.
        // GameEntity doesn't have poison, and we can't modify the game engine.
        // Also add a not null check in case we failed to convert something to poisonable.
        if (poisonable != null) {
            poisonable.poison(intensity);
            duration--;
        }
    }

    /**
     * Indicates whether this status is still active (duration not expired).
     * @return true if active, false otherwise.
     */
    public boolean isStatusActive() {
        return duration > 0;
    }

}
