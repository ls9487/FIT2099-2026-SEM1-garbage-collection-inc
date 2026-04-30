package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.grounds.Unlockable;

/**
 * An action representing unlocking something that can be unlocked.
 * Like how we did in the bootcamp.
 *
 * @see Action
 *
 * @author echu0057
 */
public class UnlockAction extends Action {

    private Unlockable unlockable;

    /**
     * Constructor for the UnlockAction class.
     * @param unlockable The unloackable this action is with respect to.
     */
    public UnlockAction(Unlockable unlockable) {
        this.unlockable = unlockable;
    }

    /**
     * When executed, it will let the logic of unlocking be handled by that particular unlockable.
     * @param actor The actor unlocking the unlockable.
     * @param map The map the actor is on.
     * @return The description of the result of unlocking the unlockable.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return unlockable.unlockedBy(actor);
    }

    /**
     * Describes what this action will do in the menu (unlocking the unlockable).
     * @param actor The actor performing this action.
     * @return The description of this action.
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s unlocks %s", actor, unlockable);
    }

}
