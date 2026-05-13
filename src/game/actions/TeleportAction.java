package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Teleporter;

/**
 * Moves an actor to a destination location, optionally simulating teleport
 * device malfunction and applying arrival environmental effects.
 *
 * @author eche0116
 * @version 1.0
 */
public class TeleportAction extends Action {

    private final Teleporter teleporter;
    private final Location destination;

    /**
     * Builds a teleport action with optional malfunction, fire ring, and hooks.
     *
     * @param teleporter the teleporter teleporting actor
     * @param destination the location targeted to arrived
     */
    public TeleportAction(Teleporter teleporter, Location destination) {
        this.teleporter = teleporter;
        this.destination = destination;
    }

    /**
     * teleporter teleport actor to selected destination
     *
     * @param actor the actor being teleported
     * @param map   the map the actor is currently on (used only for messaging consistency; movement uses destination maps)
     * @return a description of the teleport outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return teleporter.teleport(actor, map, destination);
    }

    /**
     * Describes what this action will do in the menu (teleporter teleport actor).
     *
     * @param actor the acting actor
     * @return menu line for this teleport choice
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s uses %s to teleport to %s", actor, teleporter, destination);
    }
}