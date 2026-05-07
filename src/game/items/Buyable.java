package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Stuff the SuperComputer is willing to part with for credits.
 * buyPrice() is the cost, boughtBy() is what happens after the credits leave
 * the wallet, and cannotAfford() lets specific items (cough, FirstAidKit)
 * react badly when a broke worker tries to buy them.
 *
 * @author esoo0013
 */
public interface Buyable {

    /**
     * What this item costs.
     * @param buyer The actor trying to buy.
     * @return Credit cost.
     * @author esoo0013
     */
    int buyPrice(Actor buyer);

    /**
     * Apply whatever special thing happens when the item is bought. Called after
     * credits are deducted but before the item is added to inventory.
     * @param buyer The actor doing the buying.
     * @param map The map the buyer is on.
     * @return A description of what happened.
     * @author esoo0013
     */
    String boughtBy(Actor buyer, GameMap map);

    /**
     * What happens when the buyer can't afford this item. Default is just a
     * polite refusal. FirstAidKit overrides this to murder the buyer.
     * @param buyer The actor who tried and failed.
     * @param map The map the buyer is on.
     * @return A description of the failure.
     * @author esoo0013
     */
    default String cannotAfford(Actor buyer, GameMap map) {
        return buyer + " cannot afford this transaction.";
    }
}