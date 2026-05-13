package game.actors;

/**
 * Capability for actors whose inventory may contain access cards that grant
 * door clearance.
 *
 * @author eche0116
 * @version 1.0
 */
public interface Unlocker {

    /**
     * Returns the highest door clearance available from carried items, if any.
     *
     * @return the strongest clearance held, or empty when no qualifying item is carried
     */
    ClearanceLevel currentClearanceLevel();
}