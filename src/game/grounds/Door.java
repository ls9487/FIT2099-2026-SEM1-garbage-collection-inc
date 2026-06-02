package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UnlockAction;
import game.items.ItemStatistics;
import game.statuses.Alarmable;

/**
 * Abstract secured door that opens only for sufficient clearance and applies
 * tier-specific hazards or benefits when opened.
 *
 *
 * @author echu0057
 * @author eche0116
 */
public abstract class Door extends Ground implements Unlockable, Alarmable {

    private boolean isUnlocked;
    private boolean isAlarmed;
    private int securityLevel;

    /**
     * Constructor for the Door class.
     * Starts off locked and must be unlocked for actors to pass through.
     *
     * @param displayChar the display of door in the map
     * @param name the name of the door
     */
    protected Door(char displayChar, String name, int securityLevel) {
        super(displayChar, name);
        this.isUnlocked = false;
        this.isAlarmed = false;
        this.securityLevel = securityLevel;
    }

    /**
     * Returns whether the actor may attempt an unlock interaction on this door.
     *
     * @param actor the actor interacting with the door
     * @return true when clearance is met and the door is locked and not alarmed
     */
    public boolean canActorUnlock(Actor actor) {
        if (isUnlocked || isAlarmed) {
            return false;
        }

        return actor.getInventory().getItems().stream()
                .anyMatch(item ->
                        item.hasStatistic(ItemStatistics.CLEARANCE_LEVEL) &&
                                item.getStatistic(ItemStatistics.CLEARANCE_LEVEL) >= getRequiredClearance()
                );
    }

    /**
     * Declares the minimum clearance tier required to unlock this door.
     *
     * @return required clearance level
     */
    public int getRequiredClearance() {
        return securityLevel;
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
     * Doors will block projectiles if they haven't been unlocked yet.
     * If they have been unlocked, it's assumed they're left wide open.
     * @return false if the door is unlocked, true otherwise.
     */
    @Override
    public boolean blocksThrownObjects() {
        return !isUnlocked;
    }

    /**
     * Have the actor unlock the door using their card (or other means).
     * Unlocked door stays open so others can traverse them.
     * Note that unlocking doors have side effects, varying with each type of tier...
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