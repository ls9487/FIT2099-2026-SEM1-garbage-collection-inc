package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.vehicles.Hoverer;

/**
 * Action to activate hover blast on a HoverBike when the rider has extra energy.
 *
 * @author lyan0121
 * @version 1.0
 */
public class HoverBlastAction extends Action {
    private Hoverer hoverer;

    /**
     * Creates a hover blast action for the given hover-capable rideable.
     *
     * @param hoverer the rideable providing hover blast
     */
    public HoverBlastAction(Hoverer hoverer) {
        this.hoverer = hoverer;
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
        return hoverer.hoverBlast(actor, map);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s activate hover blast.", actor);
    }
}
