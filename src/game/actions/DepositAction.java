package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.grounds.QuotaManager;
import game.items.Depositable;

/**
 * DepositAction handles depositing a Depositable item into the SuperComputer
 * to contribute toward the company quota (handled by a QuotaManager).
 *
 * The quota credit addition is handled here.
 * Item specific side effects and inventory removal are delegated to the Depositable.
 *
 * This mirrors the pattern established by SellAction.
 *
 * @author eche0116
 */
public class DepositAction extends Action {

    private final Depositable depositable;
    private final QuotaManager quotaManager;

    /**
     * Constructs a DepositAction for the given item and quota manager.
     *
     * @param depositable  The item being deposited.
     * @param quotaManager The QuotaManager to handle the company credits when deposited.
     */
    public DepositAction(Depositable depositable, QuotaManager quotaManager) {
        this.depositable = depositable;
        this.quotaManager = quotaManager;
    }

    /**
     * Adds company credits to the QuotaManager, then delegates side effects
     * and inventory removal to the Depositable.
     *
     * @param actor The actor performing the deposit.
     * @param map   The map the actor is on.
     * @return A description of the deposit result.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Depositing will always add company credits to the QuotaManager.
        quotaManager.addCompanyCredits(depositable.getDepositValue());
        // Let the depositable handle the side effects of depositing.
        return depositable.depositedBy(actor, map);
    }

    /**
     * Describes this action in the menu.
     *
     * @param actor The actor performing this action.
     * @return The menu description.
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s deposits %s for %d company credits",
                actor, depositable, depositable.getDepositValue());
    }
}