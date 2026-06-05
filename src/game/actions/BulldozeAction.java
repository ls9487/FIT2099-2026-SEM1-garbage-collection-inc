package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.vehicles.Bulldozeable;

/**
 * Action for a rider with VehicleAbilities.BULLDOZE to bulldoze a wall
 * into sand or push an actor backward.
 *
 * @author lyan0121
 * @version 1.0
 */
public class BulldozeAction extends Action {
    private Bulldozeable bulldozeable;
    private Location knockableLocation;
    private String direction;

    /**
     * Creates a bulldoze action that may chain into a move toward the knocked tile.
     *
     * @param bulldozeable the wall or actor being bulldozed
     * @param knockableLocation the bulldozer's current location used for knock-back geometry
     * @param direction the direction of the bulldoze
     */
    public BulldozeAction(Bulldozeable bulldozeable, Location knockableLocation, String direction) {
        this.bulldozeable = bulldozeable;
        this.knockableLocation = knockableLocation;
        this.direction = direction;
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
        return bulldozeable.bulldoze(actor, map, knockableLocation);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s bulldozes %s", actor, bulldozeable);
    }

    /**
     * After bulldozing a wall, moves the bulldozer into the newly created sand tile.
     *
     * @return a move action into the bulldozed location
     */
    @Override
    public Action getNextAction() {
        return new MoveActorAction(knockableLocation, direction);
    }
}
