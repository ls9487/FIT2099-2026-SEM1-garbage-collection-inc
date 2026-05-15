package game.actions;

import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Sellable;
import game.actors.EclipseStatistics;

/**
 * Handles selling a Sellable to the SuperComputer.
 *
 * The action holds the capability directly.
 * All item-specific sell side effects and inventory removal
 * live in Sellable via soldBy().
 *
 * @author esoo0013
 */
public class SellAction extends Action {

    private final Sellable sellable;

    /**
     * Constructor for a SellAction.
     * @param sellable the sellable being offered for sale.
     * @author esoo0013
     */
    public SellAction(Sellable sellable) {
        this.sellable = sellable;
    }

    /**
     * Runs the generic selling flow.
     * Credit addition is done on this action.
     * Item-specific side effects of being sold and inventory removal are
     * delegated to the sellable.
     * @param seller The actor selling the sellable.
     * @param map The map the actor is on.
     * @return The description of the transaction result.
     */
    @Override
    public String execute(Actor seller, GameMap map) {
        // Not all actors have credits. If they don't, then the action will not proceed further.
        if (!seller.hasStatistic(EclipseStatistics.CREDITS)) {
            return seller + " has nowhere to put credits.";
        }
        // Give the credits to the seller since they're selling it.
        int price = sellable.getSellPrice();
        seller.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.INCREASE, price);
        // Let the sellable handle their side-effects and inventory removal.
        return sellable.soldBy(seller, map);
    }

    /**
     * Describes what this action will do in the menu (selling something, and for how much).
     * @param seller The actor performing the action.
     * @return The description of this action.
     */
    @Override
    public String menuDescription(Actor seller) {
        return String.format("%s sells %s for %d credits",
                seller, sellable, sellable.getSellPrice());
    }

}