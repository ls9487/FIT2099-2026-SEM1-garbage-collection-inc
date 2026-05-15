package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.ConsumeAction;
import game.actors.ActorAbilities;

/**
 * CookiePack represents a pack of 5 cookies. Only one can be eaten at a time (by law).
 * Quite unhealthy, so eating it decreases max hp.
 * If sterilised, it provides some healing.
 *
 * @author echu0057
 */
public class CookiePack extends EclipseItem implements Consumable, Sellable {

    /** Inventory weight in units. */
    private static final int WEIGHT = 2;

    /** Map display symbol for this pack. */
    private static final char SYMBOL = '◍';

    /** Cookies in a fresh pack (also the price for a fresh pack and the max HP cost). */
    private static final int INITIAL_COOKIES = 5;

    /** HP healed when one sterilised cookie is consumed. */
    private static final int STERILISED_HEAL = 1;

    /** Max-HP reduction when one unsterilised cookie is consumed. */
    private static final int HEALTH_HIT = 1;

    /** How many cookies are eaten per consume action. */
    private static final int COOKIES_PER_BITE = 1;

    /**
     * Constructor for the CookiePack class.
     * Has a weight of 2 units, and can be eaten 5 times.
     */
    public CookiePack() {
        super("Cookies", SYMBOL, WEIGHT);
        this.addNewStatistic(ItemStatistics.DURABILITY, new BaseStatistic(INITIAL_COOKIES));
    }

    /**
     * Returns a list of allowable actions that the cookie pack can perform to its owner.
     * Can be eaten as long as it's in the inventory.
     * @param owner The actor that owns the item.
     * @param map The map where the actor is performing the action on.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(owner, map);
        // Allow consuming.
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Returns a list of allowable actions that someone on the specified location
     * can do with the cookies (on the ground, on the same location).
     * @param location The location of the ground on which the item lies.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Location location) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(location);
        // Only allow consuming if the actor can directly consume it.
        if (location.containsAnActor() && location.getActor().hasAbility(ActorAbilities.DIRECT_CONSUMER)) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Have the actor consume one cookie.
     * Actor will lose 1 max hp, and heal 1 if they can sterilise it.
     * @param actor The actor consuming a cookie.
     * @return A string description of the result of consuming one of the cookies.
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        // Consume one cookie. Remove this if it runs out of cookies.
        this.modifyStatistic(ItemStatistics.DURABILITY, StatisticOperations.DECREASE, COOKIES_PER_BITE);
        if (this.getStatistic(ItemStatistics.DURABILITY) == 0) {
            // Try to remove from actor's inventory. If it returns false (failed),
            // then the item is on the ground, so remove it there.
            if (!actor.getInventory().remove(this)) {
                map.locationOf(actor).removeItem(this);
            }
        }

        // Check if the consumer has the STERILISER ability.
        if (actor.hasAbility(ItemAbilities.STERILISER)) {
            // Heal for STERILISED_HEAL hp.
            actor.heal(STERILISED_HEAL);
            return actor + " eats a sterilised cookie, healing them by " + STERILISED_HEAL + " hp.";
        } else {
            // Decrease max hp by HEALTH_HIT.
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, HEALTH_HIT);
            return actor + " feels unhealthier from eating the unsterilised cookie!";
        }
    }

    /**
     * One credit per remaining cookie. A fresh pack of 5 sells for 5; if the
     * worker's already eaten three, it's only worth 2.
     * @author esoo0013
     */
    @Override
    public int sellPrice(Actor seller) {
        return this.getStatistic(ItemStatistics.DURABILITY);
    }

    /**
     * The "organic processing fee": one HP lost per cookie sold. The whole
     * pack goes at once, so a full pack costs the seller 5 HP immediately,
     * after which the pack leaves the inventory.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        int cookies = this.getStatistic(ItemStatistics.DURABILITY);
        seller.hurt(cookies);
        seller.getInventory().remove(this);
        return seller + " sells the cookie pack for " + cookies + " credits and loses "
                + cookies + " HP to the organic processing fee.";
    }

}
