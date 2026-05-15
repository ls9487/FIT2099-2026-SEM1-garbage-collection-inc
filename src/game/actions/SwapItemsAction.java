package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

public class SwapItemsAction extends Action {
    private Item itemFrom;
    private Location from;
    private Item itemTo;
    private Location to;

    public SwapItemsAction(Item itemFrom, Location from, Item itemTo, Location to) {
        this.itemFrom = itemFrom;
        this.from = from;
        this.itemTo = itemTo;
        this.to = to;
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
        from.removeItem(itemFrom);
        to.removeItem(itemTo);

        from.addItem(itemTo);
        to.addItem(itemFrom);
        return String.format("%s swapped %s at %s and %s at %s", actor, itemFrom, from, itemTo, to);
    }

    /**
     * Describe what action will be performed if this Action is chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s swaps %s and %s", actor, itemFrom, itemTo);
    }
}
