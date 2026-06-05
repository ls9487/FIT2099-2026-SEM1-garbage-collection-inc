package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Stuff we can flog to the SuperComputer for credits.
 * sellPrice() just reports what it'd go for, soldBy() runs after the
 * credits land and is where the special transaction side-effects live.
 *
 * @author esoo0013
 */
public interface Sellable {

    /**
     * What this item sells for. May depend on item state (e.g. a half-empty
     * cookie pack is worth less).
     * @return Credit value.
     * @author esoo0013
     */
    public int getSellPrice();

    /**
     * Apply the item-specific sale logic AND remove the item from the
     * seller's inventory. Called by SellAction after credits have been
     * paid.
     *
     * Implementations are expected to:
     * 1 - run any side-effects (poison, burns, fires, glitches, etc.),
     * 2 - call seller.getInventory().remove(...) for this item,
     * 3 - return a full sentence describing the sale, including the price and any side-effect outcomes.
     *
     * SellAction itself returns this string unchanged.
     *
     * @param seller The actor doing the selling.
     * @param map The map the seller is on.
     * @return A full sentence describing the sale and its effects.
     * @author esoo0013
     */
    public String soldBy(Actor seller, GameMap map, Location superComputerLocation);
}