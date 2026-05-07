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
     */
    public BuyAction(Item item) {
        this.item = item;
    }

    /**
     * Run the transaction. Order: check buyable, find wallet, check affordability
     * (delegate to cannotAfford if broke), deduct, run side effect, add to inventory.
     * Side effect runs BEFORE the item is added so things like SterilisationBox's
     * radiation operate on the pre-purchase inventory and can't erase themselves.
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