package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UnlockAction;
import game.items.ItemAbilities;
import game.statuses.Alarmable;

/**
 * Its primary purpose in the universe is to halt the progress of underpaid
 * {@code ContractedWorker}s until they can produce the correct rectangular
 * piece of plastic.
 *
 * @author echu0057
 */
public class Door extends Ground implements Unlockable, Alarmable {

    private boolean isUnlocked;
    private boolean isAlarmed;

    /**
     * Constructor for the Door class.
     * Starts off locked and must be unlocked for actors to pass through.
     */
    public Door() {
        super('=', "Door");
        isUnlocked = false;
        isAlarmed = false;
    }

    /**
     * Returns an ActionList that could contain an UnlockAction under certain conditions.
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        // Get the default list of actions from Ground (actually empty).
        ActionList actions = super.allowableActions(actor, location, direction);
        // Allow this door to be unlocked if the actor can do so, and this door is locked.
        // Additionally, the door must not be in an alarmed state.
        if (actor.hasAbility(ItemAbilities.UNLOCKER) && !isUnlocked && !isAlarmed) {
            actions.add(new UnlockAction(this));
        }
        return actions;
    }

    /**
     * if the door is unlocked, any actor can step into the door
     * @param actor the Actor to check
     * @return true if the door is unlocked, false otherwise.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked;
    }

    /**
     * Have the actor unlock the door using their card (or other means).
     * Unlocked door stays open so others can traverse them.
     * @param actor The actor unlocking the door.
     * @return A string description of the result of unlocking the door.
     */
    @Override
    public String unlockedBy(Actor actor) {
        if (actor.hasAbility(ItemAbilities.UNLOCKER) && !isAlarmed) {
            // Successfully unlocked!
            isUnlocked = true;
            return actor + " unlocked the door.";
        } else {
            return actor + " couldn't unlock the door.";
        }

    }

    /**
     * When alarmed, doors will lock itself.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void enableAlarmed(Actor alarmTripper) {
        isUnlocked = false;
        isAlarmed = true;
    }

    /**
     * Transition back to the non-alarmed state.
     * Does not unlock itself, requiring the door to be unlocked again.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void disableAlarmed(Actor alarmTripper) {
        isAlarmed = false;
    }

}
