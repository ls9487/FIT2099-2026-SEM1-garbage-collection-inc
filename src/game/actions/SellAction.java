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

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasStatistic(EclipseStatistics.CREDITS)) {
            return actor + " has nowhere to put credits.";
        }
        int price = sellable.sellPrice(actor);
        actor.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.INCREASE, price);

        return sellable.soldBy(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s sells %s for %d credits",
                actor, sellable, sellable.sellPrice(actor));
    }
}