package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.ConsumeAction;
import game.actors.ActorAbilities;

/**
 * Flask represents a flask that can be drunk from.
 * Somehow possesses healing properties.
 *
 * Due to severe budget cuts, the flask is only permitted to hold five (5)
 * mouthfuls of liquid per deployment. Employees are reminded not to consume
 * all five charges in a panic during a single encounter.
 *
 * @author echu0057
 */
public class Flask extends EclipseItem implements Consumable {

    /**
     * Constructor for the Flask class.
     * Has a weight of 3 units, and can be used (consumed) 5 times.
     */
    public Flask() {
        super("Flask", 'u', 3);
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(5));
    }

    /**
     * Returns a list of allowable actions that the flask can perform to its owner.
     * Which is drinking (consuming) it. If it has any uses left, that is.
     * @param owner The actor that owns the item.
     * @param map The map where the actor is performing the action on.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(owner, map);
        // Allow consuming if durability is more than zero.
        if (isUsable()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Returns a list of allowable actions that someone on the specified location
     * can do with the flask (on the ground, on the same location).
     * @param location The location of the ground on which the item lies.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Location location) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(location);
        // Anyway, only allow consuming if it's usable, and if the actor can directly consume it.
        if (isUsable() && location.containsAnActor() &&
                location.getActor().hasAbility(ActorAbilities.DIRECT_CONSUMER)) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Have the actor consume this flask, using up one of its uses.
     * Also heals the actor by one point (thanks to mag- uh, science).
     * @param actor The actor consuming this flask.
     * @return A string description of the result of consuming this flask.
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        // Check if there's any more uses in the flask.
        if (isUsable()) {
            this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, 1);
            actor.heal(1);
            return actor + " drinks from flask, healing them by 1 hp.";
        } else {
            return actor + " can't drink from flask. It's empty.";
        }
    }

    /**
     * Indicates whether the flask can be used (DURABILITY of more than 0).
     * To be used only internally within this class.
     * @return A boolean indicating whether the first aid kit can be used.
     */
    private boolean isUsable() {
        return this.getStatistic(ItemStatistics.DURABILITY) > 0;
    }

}
