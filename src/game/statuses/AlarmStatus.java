package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * AlarmStatus represents a status effect of being affected by the alarm.
 * Alarmed entities instantly know who tripped the alarm.
 */
public class AlarmStatus implements Status {

    private int duration;
    private Actor alarmTripper;
    private Alarmable alarmable;

    /**
     * Constructor for the AlarmStatus class.
     * @param duration The number of ticks the alarmed state lasts.
     * @param alarmTripper The guilty actor who tripped the alarm
     * @param alarmable The entity that is alarmed. This could be null.
     */
    public AlarmStatus(int duration, Actor alarmTripper, Alarmable alarmable) {
        this.duration = duration;
        this.alarmTripper = alarmTripper;
        this.alarmable = alarmable;
    }

    /**
     * Called once per tick to update the status of the current entity.
     * Whatever the alarmed state does, whether it's with the guilty actor, is up to the entity.
     * @param currEntity The entity this status is attached to.
     */
    public void tickStatus(GameEntity currEntity, Location location) {
        if (alarmable != null) {
            if (duration <= 1) {
                // Inform the alarmable that it's about to expire so it can disable its alarmed state.
                alarmable.disableAlarmed(alarmTripper);
            } else {
                // Inform the alarmable to enter its alarmed state.
                // Note that this is called every turn in case there's multiple alarms active,
                // so that when old alarms expire, ongoing ones are still treated as active.
                alarmable.enableAlarmed(alarmTripper);
            }
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
