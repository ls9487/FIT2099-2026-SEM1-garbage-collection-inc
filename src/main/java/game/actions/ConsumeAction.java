package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Consumable;

/**
 * An action representing consuming an item that can be consumed.
 * Like how we did in the bootcamp.
 *
 * @see Action
 *
 * @author echu0057
 */
public class ConsumeAction extends Action {

    private Consumable consumable;

    /**
     * Constructor for the ConsumeAction class.
     * @param consumable The consumable this action is with respect to.
     */
    public ConsumeAction(Consumable consumable) {
        this.consumable = consumable;
    }

    /**
     * When executed, it will let the logic of consuming be handled by that particular consumable.
     * @param actor The actor consuming the consumable.
     * @param map The map the actor is on.
     * @return The description of the result of consuming the consumable.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return consumable.consumedBy(actor, map);
    }

    /**
     * Describes what this action will do in the menu (consuming the consumable).
     * @param actor The actor performing this action.
     * @return The description of this action.
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s consumes %s", actor, consumable);
    }

}
