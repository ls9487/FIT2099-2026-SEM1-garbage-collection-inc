package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import game.vehicles.Purifiable;

/**
 * Action for a rider with {@link game.vehicles.VehicleAbilities#PURIFY} to purify toxic waste into dirt.
 *
 * @author lyan0121
 * @version 1.0
 */
public class PurifyAction extends Action {
    private Purifiable purifiable;
    private Ground purifiedGround;

    /**
     * Creates a purify action that replaces the purifiable tile with the given ground.
     *
     * @param purifiable the toxic waste or other purifiable ground
     * @param purifiedGround the ground type to place after purification
     */
    public PurifyAction(Purifiable purifiable, Ground purifiedGround) {
        this.purifiable = purifiable;
        this.purifiedGround = purifiedGround;
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
        return purifiable.purify(actor, map, purifiedGround);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s purifies %s", actor, purifiable);
    }
}
