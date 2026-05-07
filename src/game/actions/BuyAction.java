package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.EclipseActor;
import game.inventories.Wallet;
import game.items.Buyable;

/**
 * The "give me that thing" action. Handles the affordability check,
 * the wallet deduction, the side effect, and the inventory add.
 * Specific item weirdness (hidden fees, dying for being broke, radiation
 * eating your stuff) is left to the Buyable's own methods.
 *
 * @author esoo0013
 */
public class BuyAction extends Action {

    private final Item item;

    /**
     * Constructor. The item must implement Buyable; if it doesn't this action
     * will just refuse politely at execute time.
     * @param item The item being offered.
     * @author esoo0013
     */
    public BuyAction(Item item) {
        this.item = item;
    }

    /**
     * Handles the full purchase flow for an item.
     *
     * The action checks whether the item is buyable, verifies the actor has
     * a valid Wallet and enough credits, deducts the cost, applies any
     * purchase effects, and finally adds the item to the inventory.
     *
     * Purchase side effects happen before the item is added to the inventory.
     * This allows effects such as the SterilisationBox radiation to operate
     * on the actor's existing inventory without deleting the newly bought item.
     *
     * @author esoo0013
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Buyable buyable = item.asCapability(Buyable.class).orElse(null);
        if (buyable == null) {
            return item + " is not for sale here.";
        }

        Wallet wallet = walletOf(actor);
        if (wallet == null) {
            return actor + " has nowhere to draw credits from.";
        }

        int price = buyable.buyPrice(actor);
        if (!wallet.canAfford(price)) {
            return buyable.cannotAfford(actor, map);
        }

        wallet.subtract(price);
        String effect = buyable.boughtBy(actor, map);
        boolean stowed = actor.getInventory().add(item);
        String tail = stowed ? "" : " (Item too heavy and was discarded.)";
        return actor + " buys " + item + " for " + price + " credits. " + effect + tail;
    }

    /**
     * Pull the wallet off an EclipseActor. Returns null for any other actor type.
     * @author esoo0013
     */
    private Wallet walletOf(Actor actor) {
        return actor.asCapability(EclipseActor.class).map(EclipseActor::getWallet).orElse(null);
    }

    @Override
    public String menuDescription(Actor actor) {
        Buyable buyable = item.asCapability(Buyable.class).orElse(null);
        int price = (buyable != null) ? buyable.buyPrice(actor) : 0;
        return String.format("%s buys %s for %d credits", actor, item, price);
    }
}