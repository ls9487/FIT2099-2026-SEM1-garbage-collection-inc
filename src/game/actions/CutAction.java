package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Cuttable;

/**
 * CutAction represents the action of cutting a Cuttable object with a Plasma Cutter.
 * All cutting logic and side effects are delegated to the Cuttable itself.
 *
 * @author eche0116
 */
public class CutAction extends Action {

    private final Cuttable cuttable;
    private final Location location;

    /**
     * Constructs a CutAction targeting the given Cuttable.
     *
     * @param cuttable The object to be cut.
     * @param location The location of where the object was cut.
     */
    public CutAction(Cuttable cuttable, Location location) {
        this.cuttable = cuttable;
        this.location = location;
    }

    /**
     * Executes the cut by delegating to the Cuttable's cutBy() method.
     *
     * @param actor The actor performing the cut.
     * @param map   The map the actor is on.
     * @return A description of what happened.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return cuttable.cutBy(actor, map, location);
    }

    /**
     * Describes this action in the menu.
     *
     * @param actor The actor performing this action.
     * @return The menu description.
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s cuts %s with Plasma Cutter", actor, cuttable);
    }
}