package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.vehicles.Rideable;

/**
 * Action for a mounted actor to dismount Rideable
 *
 * @author lyan0121
 * @version 1.0
 */
public class DismountAction extends Action {
    private Rideable rideable;

    /**
     * Creates a dismount action for the given rideable in the actor's inventory.
     *
     * @param rideable the rideable to dismount
     */
    public DismountAction(Rideable rideable) {
        this.rideable = rideable;
    }

    /**
     * Perform the Action.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of what happened (the result of the action being performed) that can be displayed to the user.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return rideable.dismount(actor, map);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s dismounts %s", actor, rideable);
    }
}
