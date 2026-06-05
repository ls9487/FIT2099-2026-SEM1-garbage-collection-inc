package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Depositable is an interface representing an item that can be deposited
 * into the SuperComputer to contribute toward the company quota.
 *
 * @author eche0116
 */
public interface Depositable {

    /**
     * Returns how many Company Credits this item contributes when deposited.
     *
     * @return the company credit value of this item.
     */
    int getDepositValue();

    /**
     * Called when an actor deposits this item into the SuperComputer.
     * Implementations handle side effects (healing, teleportation, damage, etc.)
     * and must remove this item from the actor's inventory.
     *
     * @param actor The actor depositing this item.
     * @param map   The map the actor is on.
     * @return A string description of what happened.
     */
    String depositedBy(Actor actor, GameMap map);
}