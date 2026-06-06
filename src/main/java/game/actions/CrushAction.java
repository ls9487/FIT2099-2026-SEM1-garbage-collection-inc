package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.vehicles.Crushable;

/**
 * Action for a rider with VehicleAbilities.CRUSH to crush impassable ground into dirt.
 *
 * @author lyan0121
 * @version 1.0
 */
public class CrushAction extends Action {
    private Crushable crushable;
    private Location location;

    /**
     * Creates a crush action against the given crushable target.
     *
     * @param crushable the door, wall, or other crushable ground
     */
    public CrushAction(Crushable crushable, Location location) {
        this.crushable = crushable;
        this.location = location;
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
        return crushable.crush(actor, map, location);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s crushes %s", actor, crushable);
    }
}
