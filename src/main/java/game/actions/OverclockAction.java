package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.vehicles.Overclockable;

/**
 * Action to overclock a MechSuit, sacrificing health to scorch nearby tiles.
 *
 * @author lyan0121
 * @version 1.0
 */
public class OverclockAction extends Action {
    private Overclockable overclockable;

    /**
     * Creates an overclock action for the given rideable.
     *
     * @param overclockable the mech suit or other overclockable vehicle
     */
    public OverclockAction(Overclockable overclockable) {
        this.overclockable = overclockable;
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
        return overclockable.overclock(actor, map);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s overclocks %s", actor, overclockable);
    }
}
