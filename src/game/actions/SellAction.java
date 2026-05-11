package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.EclipseActor;
import game.items.Sellable;

/**
 * Handles selling items to the SuperComputer.
 *
 * The action awards credits to the seller, and applies any
 * item-specific sell effects, and then removes the item
 * from the inventory.
 *
 * @author esoo0013
 */
public class SellAction extends Action {

    private final Item item;

    /**
     * Constructor for a SellAction.
     *
     * @param item The item being sold.
     * @author esoo0013
     */
    public SellAction(Item item) {
        this.item = item;
    }

    /**
     * Handles the full selling flow for an item
     *
     * The action checks whether the item is sellable,
     * calculates the item's value, credits the seller,
     * applies any sell effects, and finally removes
     * the item from the inventory.
     *
     * The sell price is calculated before side effects
     * are applied so item state changes do not affect
     * the transaction midway through the sale.
     *
     * @author esoo0013
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Sellable sellable = item.asCapability(Sellable.class).orElse(null);
        if (sellable == null) {
            return item + " can't be sold.";
        }

        // Credits are only supported by EclipseActor-based actors
        EclipseActor eclipseActor = actor.asCapability(EclipseActor.class).orElse(null);

        if (eclipseActor == null) {
            return actor + " has nowhere to put credits.";
        }

        // Calculate the price before side effects are applied
        // This prevents item state changes from affecting the sale value
        int price = sellable.sellPrice(actor);

        // Award the credits immediately after the price is finalised
        eclipseActor.addCredits(price);

        // Run any item-specific sell effects
        // e.g Apple poisons the seller
        // e.g Lantern may ignite nearby tiles
        // e.g CRT Monitor may explode
        String effect = sellable.soldBy(actor, map);

        // Remove the sold item from the inventory once the transaction and side effects are complete
        actor.getInventory().remove(item);

        return actor + " sells " + item + " for " + price + " credits. " + effect;
    }

    @Override
    public String menuDescription(Actor actor) {
        Sellable sellable = item.asCapability(Sellable.class).orElse(null);
        int price = (sellable != null) ? sellable.sellPrice(actor) : 0;
        return String.format("%s sells %s for %d credits", actor, item, price);
    }
}