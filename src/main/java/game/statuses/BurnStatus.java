package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * BurnStatus represents a status effect of being burned.
 * Each BurnStatus is one stack. Something can have multiple BurnStatus applied to them.
 * Getting burned will hurt but it'll probably be your fault in the first place.
 * The mars example was used as a reference.
 *
 * @author echu0057
 */
public class BurnStatus implements Status {

    private int duration;
    private int intensity;
    private Flammable flammable;

    /**
     * Constructor for the BurnStatus class.
     * @param duration The number of ticks the burn lasts.
     * @param intensity The intensity (damage dealt per tick) of the burn.
     * @param flammable The entity that is burned. This could be null.
     */
    public BurnStatus(int duration, int intensity, Flammable flammable) {
        this.duration = duration;
        this.intensity = intensity;
        this.flammable = flammable;
    }

    /**
     * Called once per tick to update the status of the current entity.
     * Whatever the burn does is up to the entity, taking damage for example.
     * @param currEntity the entity this status is attached to
     * @param location   the entity's current tile
     */
    public void tickStatus(GameEntity currEntity, Location location) {
        // Logic here is like in the mars example (see PoisonStatus' tickStatus for more detail)
        if (flammable != null) {
            flammable.burn(intensity);
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
