package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

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
     * cookie pack is worth less). Should not have side effects.
     * @param seller The actor doing the selling.
     * @return Credit value.
     * @author esoo0013
     */
    int sellPrice(Actor seller);

    /**
     * Apply whatever special thing happens when the item is sold. Called after
     * credits are added to the wallet but before the item leaves the inventory.
     * @param seller The actor doing the selling.
     * @param map The map the seller is on (for spawning fire on neighbours, etc).
     * @return A description of what happened.
     * @author esoo0013
     */
    String soldBy(Actor seller, GameMap map);
}