package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.EclipseActor;
import game.items.Sellable;

/**
 * Handles selling a Sellable to the SuperComputer.
 *
 * The action holds the capability directly.
 * All item-specific sell side effects and inventory removal
 * live in Sellable via soldBy().
 *
 * @author esoo0013
 */
public class SellAction extends Action {

    private final Sellable sellable;

    /**
     * Constructor for a SellAction.
     * @param sellable the sellable being offered for sale.
     * @author esoo0013
     */
    public SellAction(Sellable sellable) {
        this.sellable = sellable;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        EclipseActor eclipseActor = actor.asCapability(EclipseActor.class).orElse(null);
        if (eclipseActor == null) {
            return actor + " has nowhere to put credits.";
        }

        // Lock in the price before any side-effects change item state
        int price = sellable.sellPrice(actor);

        // Pay the seller first; soldBy() handles consequences and removal
        eclipseActor.addCredits(price);

        return sellable.soldBy(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s sells %s for %d credits",
                actor, sellable, sellable.sellPrice(actor));
    }
}