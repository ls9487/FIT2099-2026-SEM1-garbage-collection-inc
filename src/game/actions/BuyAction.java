package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.EclipseActor;
import game.items.Buyable;

/**
 * Handles purchasing items from the SuperComputer.
 *
 * This action performs affordability check, deducts credits,
 * applies item-specific purchase effects, and finally attempts
 * to place the item into the actor's inventory.
 *
 * Special purchase behaviour is given to the Buyable interface
 * so the action itself stays generic and reusable.
 * @author esoo0013
 */
public class BuyAction extends Action {

    private final Item item;

    /**
     * Constructor for a BuyAction.
     * @param item The item being offered for purchase.
     * @author esoo0013
     */
    public BuyAction(Item item) {
        this.item = item;
    }

    /**
     * Handles the full purchase flow for an item.
     *
     * The action checks whether the item is buyable, verifies the actor
     * has enough credits, deducts the transaction cost, applies any
     * purchase effects, and finally adds the item to the inventory.
     *
     * Purchase effects happen before the item is added. This allows
     * effects such as the SterilisationBox radiation to interact with
     * the actor's existing inventory without deleting the newly bought item.
     *
     * @author esoo0013
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        // Making sure the item actually supports buying behaviour
        Buyable buyable = item.asCapability(Buyable.class).orElse(null);
        if (buyable == null) {
            return item + " is not for sale here.";
        }

        // Credits are only supported by EclipseActor-based actors
        EclipseActor eclipseActor = actor.asCapability(EclipseActor.class).orElse(null);

        if (eclipseActor == null) {
            return actor + " has nowhere to draw credits from.";
        }

        // Ask the item itself how much it costs
        int price = buyable.buyPrice(actor);

        // Let the Buyable decide what happens if the actor cannot afford it.
        // Example: FirstAidKit instantly kills the buyer when broke.
        if (!eclipseActor.canAfford(price)) {
            return buyable.cannotAfford(actor, map);
        }

        // Deduct the credits before applying purchase effects
        eclipseActor.deductCredits(price);

        // Run any item-specific purchase logic
        // e.g Level 2 AccessCard damages the buyer OR SterilisationBox deletes a random inventory item
        String effect = buyable.boughtBy(actor, map);

        // Try to place the bought item into the inventory
        boolean stowed = actor.getInventory().add(item);

        // If inventory insertion fails (normally due to weight),
        // the item is discarded after purchase.
        String tail = stowed ? "" : " (Item too heavy and was discarded.)";

        return actor + " buys " + item + " for "
                + price + " credits. " + effect + tail;
    }

    @Override
    public String menuDescription(Actor actor) {
        Buyable buyable = item.asCapability(Buyable.class).orElse(null);
        int price = (buyable != null) ? buyable.buyPrice(actor) : 0;
        return String.format("%s buys %s for %d credits", actor, item, price);
    }
}