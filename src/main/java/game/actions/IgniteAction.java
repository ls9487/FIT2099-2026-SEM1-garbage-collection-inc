package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.vehicles.Igniter;

/**
 * Action to ignite an adjacent actor using a {@link game.vehicles.MagneticField} upgrade.
 *
 * @author lyan0121
 * @version 1.0
 */
public class IgniteAction extends Action {
    private Igniter igniter;
    private Actor target;

    /**
     * Creates an ignite action against the given adjacent target.
     *
     * @param igniter the magnetic field or other igniter upgrade
     * @param target the adjacent actor to ignite
     */
    public IgniteAction(Igniter igniter, Actor target) {
        this.igniter = igniter;
        this.target = target;
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
        return igniter.ignite(actor, map, target);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s ignites %s", actor, target);
    }
}
