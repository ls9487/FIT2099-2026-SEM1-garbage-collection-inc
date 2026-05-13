package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UnlockAction;
import game.actors.Unlocker;
import game.items.ClearanceLevel;
import game.statuses.Alarmable;

/**
 * Abstract secured door that opens only for sufficient clearance and applies
 * tier-specific hazards or benefits when opened.
 *
 * @author echu0057
 * @version 2.0
 */
public abstract class Door extends Ground implements Unlockable, Alarmable {

    private boolean isUnlocked;
    private boolean isAlarmed;

    /**
     * Constructor for the Door class.
     * Starts off locked and must be unlocked for actors to pass through.
     *
     * @param displayChar the display of door in the map
     * @param name the name of the door
     */
    protected Door(char displayChar, String name) {
        super(displayChar, name);
        this.isUnlocked = false;
        this.isAlarmed = false;
    }

    /**
     * Returns whether the actor may attempt an unlock interaction on this door.
     *
     * @param actor the actor interacting with the door
     * @return true when clearance is met and the door is locked and not alarmed
     */
    public boolean canActorUnlock(Actor actor) {
        Unlocker unlocker = actor.asCapability(Unlocker.class).orElse(null);
        if (unlocker == null) {
            return false;
        } else {
            return unlocker.currentClearanceLevel() >= requiredClearance() &&
                    !isUnlocked && !isAlarmed;
        }
    }

    /**
     * Declares the minimum clearance tier required to unlock this door.
     *
     * @return required clearance level
     */
    protected abstract ClearanceLevel requiredClearance();

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
        if (canActorUnlock(actor)) {
            actions.add(new UnlockAction(this));
        }
        return actions;
    }

    /**
     * if the door is unlocked, any actor can step into the door
     *
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
     *
     * @param actor The actor unlocking the door.
     * @param map   The map containing the door.
     * @return A string description of the result of unlocking the door.
     */
    @Override
    public String unlockedBy(Actor actor, GameMap map) {
        isUnlocked = true;
        return unlockSideEffects(actor, map);
    }

    /**
     * Runs tier-specific logic after a successful unlock.
     *
     * @param actor the actor that unlocked the door
     * @param map   the map containing the door
     * @return narrative description including unlock confirmation
     */
    protected abstract String unlockSideEffects(Actor actor, GameMap map);

    /**
     * When alarmed, doors will lock itself.
     *
     * @param alarmTripper The actor that tripped the alarm.
     */
    @Override
    public void enableAlarmed(Actor alarmTripper) {
        isUnlocked = false;
        isAlarmed = true;
    }

    /**
     * Transition back to the non-alarmed state.
     * Does not unlock itself, requiring the door to be unlocked again.
     *
     * @param alarmTripper The actor that tripped the alarm.
     */
    @Override
    public void disableAlarmed(Actor alarmTripper) {
        isAlarmed = false;
    }
}