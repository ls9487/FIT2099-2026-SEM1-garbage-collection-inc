package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * StunStatus represents a status effect of being stunned.
 * when being stunned actor's turn are skipped
 * @author lyan0121
 * @version 1.0
 */
public class StunStatus implements Status {
    private int duration;
    private int intensity;
    private Stunnable stunnable;

    /**
     * Constructor for the StunStatus class.
     * @param duration The number of ticks the stun lasts.
     */
    public StunStatus(int duration, int intensity, Stunnable stunnable) {
        this.duration = duration;
        this.intensity = intensity;
        this.stunnable = stunnable;
    }
    /**
     * Called once per tick to update the status of the current entity.
     * Whatever the stun does is up to the entity, taking damage for example.
     * @param currEntity The entity this status is attached to.
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (stunnable != null) {
            duration--;
            stunnable.stun(intensity);
        }
    }
    /**
     * Indicates whether this status is still active.
     *
     * @return true if active, false otherwise
     */
    @Override
    public boolean isStatusActive() {
        return duration > 0;
    }
}
