package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.vehicles.Rideable;

/**
 * Action for an actor to mount a Rideable on the ground.
 *
 * @author lyan0121
 * @version 1.0
 */
public class MountAction extends Action {
    private Rideable rideable;
    private Location location;

    /**
     * Creates a mount action for the given rideable at its ground location.
     *
     * @param rideable the rideable to mount
     * @param location the tile where the rideable lies
     */
    public MountAction(Rideable rideable, Location location) {
        this.rideable = rideable;
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
        return rideable.mount(actor, location);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s mounts %s", actor, rideable);
    }
}
