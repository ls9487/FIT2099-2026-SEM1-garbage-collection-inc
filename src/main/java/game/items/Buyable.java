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
     * @return Credit cost.
     * @author esoo0013
     */
    public int getBuyPrice();

    /**
     * Apply the item-specific purchase logic AND place the item into the
     * buyer's inventory. Called by BuyAction after credits have been
     * deducted.
     *
     * Implementations are expected to:
     * 1 - run any side-effects (damage, hidden fees, radiation, etc.),
     * 2 - call buyer.getInventory().add(...) with the item,
     * 3 - return a full sentence describing the purchase
     * including the price and any side-effect outcomes.
     *
     * BuyAction itself returns this string unchanged.
     *
     * @param buyer The actor doing the buying.
     * @param map The map the buyer is on.
     * @return A full description describing the purchase and its effects.
     * @author esoo0013
     */
    public String boughtBy(Actor buyer, GameMap map);

    /**
     * What happens when the buyer can't afford this item. Maybe nothing (polite refusal),
     * maybe straight up murdering the buyer (FirstAidKit).
     * @param buyer The actor who lacks credits.
     * @param map The map the buyer is on.
     * @return A description of the failure.
     * @author esoo0013
     */
    public String cannotAfford(Actor buyer, GameMap map);

}