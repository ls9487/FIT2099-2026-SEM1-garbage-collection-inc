package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.EclipseActor;
import game.inventories.Wallet;
import game.items.Sellable;

/**
 * The "take this junk off my hands" action. Adds credits, runs the item's
 * side effect, then removes the item from the seller's inventory.
 *
 * @author esoo0013
 */
public class SellAction extends Action {

    private final Item item;

    /**
     * Constructor. The item must implement Sellable.
     * @param item The item being sold.
     * @author esoo0013
     */
    public SellAction(Item item) {
        this.item = item;
    }

    /**
     * Run the sale. Order: resolve sellable, find wallet, compute price (frozen
     * before any side effects mutate the item's state), credit the wallet, run
     * side effect, remove the item.
     * @author esoo0013
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Sellable sellable = item.asCapability(Sellable.class).orElse(null);
        if (sellable == null) {
            return item + " can't be sold.";
        }

        Wallet wallet = walletOf(actor);
        if (wallet == null) {
            return actor + " has nowhere to put credits.";
        }

        int price = sellable.sellPrice(actor);
        wallet.add(price);
        String effect = sellable.soldBy(actor, map);
        actor.getInventory().remove(item);

        return actor + " sells " + item + " for " + price + " credits. " + effect;
    }

    private Wallet walletOf(Actor actor) {
        return actor.asCapability(EclipseActor.class).map(EclipseActor::getWallet).orElse(null);
    }

    @Override
    public String menuDescription(Actor actor) {
        Sellable sellable = item.asCapability(Sellable.class).orElse(null);
        int price = (sellable != null) ? sellable.sellPrice(actor) : 0;
        return String.format("%s sells %s for %d credits", actor, item, price);
    }
}