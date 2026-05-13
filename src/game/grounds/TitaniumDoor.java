package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.ClearanceLevel;

/**
 * Tier-3 door that triggers a brief decontamination heal when opened.
 *
 * @author eche0116
 * @version 1.0
 */
public class TitaniumDoor extends Door {

    private static final int DECONTAMINATION_HEAL = 5;

    /**
     * Creates a titanium door marked with {@code M} on facility plans.
     *
     */
    public TitaniumDoor() {
        super('M', "Titanium Door");
    }

    /**
     * Declares the minimum clearance tier required to unlock this door.
     *
     * @return clearance level three
     */
    @Override
    protected ClearanceLevel requiredClearance() {
        return ClearanceLevel.LEVEL_3;
    }

    /**
     * Unlock TitaniumDoor localised decontamination restores actor by 5 health points
     *
     * @param actor the unlocking worker
     * @param map   unused
     * @return unlock narrative including healing
     */
    @Override
    protected String unlockSideEffects(Actor actor, GameMap map) {
        actor.heal(DECONTAMINATION_HEAL);
        return actor + " unlocked the titanium door. Localised decontamination restores " + DECONTAMINATION_HEAL + " health.";
    }
}