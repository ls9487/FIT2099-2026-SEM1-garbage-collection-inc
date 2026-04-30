package game.statuses;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Alarmable is an interface for entities that can be affected by the alarm.
 */
public interface Alarmable {

    /**
     * This method will handle the logic for entering the alarmed state.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void enableAlarmed(Actor alarmTripper);

    /**
     * This method will handle the logic for exiting the alarmed state.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void disableAlarmed(Actor alarmTripper);

}
