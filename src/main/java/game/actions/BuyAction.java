package game.actions;

import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Buyable;
import game.actors.EclipseStatistics;


/**
 * Handles purchasing a Buyable from the SuperComputer.
 *
 * This action confirms the actor can hold credits, checks affordability,
 * deducts the price, and then delegates all item-specific purchase behaviour
 * (side effects AND inventory placement) to the Buyable itself via boughtBy().
 *
 * It also holds the capability directly, never the concrete Item,
 * so it has no knowledge of which Buyable subtype it is operating on.
 *
 * @author esoo0013
 */
public class BuyAction extends Action {

    private final Buyable buyable;

    /**
     * Constructor for a BuyAction.
     * @param buyable the buyable being offered for purchase.
     * @author esoo0013
     */
    public BuyAction(Buyable buyable) {
        this.buyable = buyable;
    }

    /**
     * Runs the generic purchase flow.
     * Affordability and the credit deduction are the action's responsibility.
     * Item-specific side effects of being bought and inventory placement are
     * delegated to the Buyable.
     * @param buyer The actor buying the buyable.
     * @param map The map the actor is on.
     * @return The description of the transaction result.
     * @author esoo0013
     */
    @Override
    public String execute(Actor buyer, GameMap map) {
        // Not all actors have credits. If they don't, then the action will not proceed further.
        if (!buyer.hasStatistic(EclipseStatistics.CREDITS)) {
            return buyer + " has nowhere to draw credits from.";
        }

        int price = buyable.getBuyPrice();
        // Let the Buyable decide what happens if the actor cannot afford it
        // e.g FirstAidKit kills the buyer when broke
        if (buyer.getStatistic(EclipseStatistics.CREDITS) < price) {
            return buyable.cannotAfford(buyer, map);
        }

        // Deduct the buyer's credits as they can afford it.
        buyer.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.DECREASE, price);
        // Buyable owns its own purchase side-effects AND its own
        // inventory insertion. The action has no opinion on either
        return buyable.boughtBy(buyer, map);
    }

    /**
     * Describes what this action will do in the menu (buying something, and for how much).
     * @param buyer The actor performing the action.
     * @return The description of this action.
     */
    @Override
    public String menuDescription(Actor buyer) {
        return String.format("%s buys %s for %d credits",
                buyer, buyable, buyable.getBuyPrice());
    }

}