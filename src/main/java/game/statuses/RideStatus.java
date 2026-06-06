package game.statuses;

import edu.monash.fit2099.engine.capabilities.Status;

/**
 * Status indicating that an actor is currently mounted on a rideable.
 * This status does not expire on its own; it is removed only when the actor dismounts.
 *
 * @author lyan0121
 * @version 1.0
 */
public class RideStatus implements Status {
    /**
     * Ride status remains active until explicitly removed by dismounting.
     *
     * @return always true while the status is present
     */
    @Override
    public boolean isStatusActive() {
        return true;
    }
}
