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
 * A class representing a first aid kit.
 * Using it increases max hp by 1 and heals to full.
 * Good news: this means a mortally wounded worker can pop a use and even be better off than before!
 * Bad news: it has a 20-turn cooldown between uses, only ticking down if a worker carries it.
 * Badder news: it's not even usable at the start, so pick it up and wait around.
 *
 * @author echu0057
 */
public class FirstAidKit extends EclipseItem implements Consumable, Buyable {

    /** Credit cost charged by the SuperComputer. */
    private static final int BUY_PRICE = 1000;

    /** Inventory weight in units. */
    private static final int WEIGHT = 25;

    /** Map display symbol for this kit. */
    private static final char SYMBOL = '+';

    /** Turns between consecutive uses; the kit starts unusable until ticked down. */
    private static final int COOLDOWN_TURNS = 20;

    /** Maximum HP gained when the kit is consumed. */
    private static final int MAX_HP_INCREASE = 1;

    /**
     * Constructor for the FirstAidKit class. Rather heavy, with a weight of 25 units.
     * 20 turn cooldown per use, and starts off unusable.
     */
    public FirstAidKit() {
        super("First Aid Kit", SYMBOL, WEIGHT);
        this.addNewStatistic(ItemStatistics.COOLDOWN, new BaseStatistic(COOLDOWN_TURNS));
    }

    /**
     * Returns a list of allowable actions that the first aid kit.
     * "Consuming" it is only allowed when it's ready (cooldown of 0).
     * @param owner The actor that owns the item.
     * @param map The map where the actor is performing the action on.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(owner, map);
        // Allow consuming if cooldown is 0 (ready to use).
        if (isUsable()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Returns a list of allowable actions that someone on the specified location
     * can do with the first aid kit (on the ground, on the same location).
     * @param location The location of the ground on which the item lies.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Location location) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(location);
        // Only allow consuming if it's usable, and if the actor can directly consume it.
        if (isUsable() && location.containsAnActor() &&
                location.getActor().hasAbility(ActorAbilities.DIRECT_CONSUMER)) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Have the actor "consume" the first aid kit, resetting its cooldown.
     * Increases max hp by 1 and heals to full.
     * @param actor The actor consuming this first aid kit.
     * @return A string description of the result of consuming this first aid kit.
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        // Ensure the kit is ready to be used (COOLDOWN of 0).
        if (isUsable()) {
            // Reset the cooldown to max (which is 20).
            this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.UPDATE,
                    this.getMaximumStatistic(ItemStatistics.COOLDOWN));
            // Increase max hp by 1, heal to full.
            // Note that changing the maximum actually sets the current statistic to its maximum.
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, 1);

            return actor + " uses the first aid kit and feels much healthier than before!";
        } else {
            return actor + " can't use this first aid kit yet! Wait till it's ready!";
        }
    }

    /**
     * Indicates whether the first aid kit can be used (COOLDOWN of 0).
     * To be used only internally within this class.
     * @return A boolean indicating whether the first aid kit can be used.
     */
    private boolean isUsable() {
        return this.getStatistic(ItemStatistics.COOLDOWN) == 0;
    }

    /**
     * Inform the FirstAidKit of the passage of time ONLY when being carried.
     * So once per turn, tick down the cooldown.
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        // Won't ever go negative. BaseStatistic handles that logic.
        this.modifyStatistic(ItemStatistics.COOLDOWN, StatisticOperations.DECREASE, 1);
    }

    /**
     * This is the corporate price so buying it without 1000 credits is a terminal mistake.
     * @author esoo0013
     */
    @Override
    public int getBuyPrice() {
        return BUY_PRICE;
    }

    /**
     * Nothing dramatic on a successful buy. The kit lands in the inventory
     * with its existing 20-turn cooldown rules intact.
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        buyer.getInventory().add(this);
        return buyer + " buys the first aid kit for " + getBuyPrice() + " credits.";
    }

    /**
     * Trying to buy this WITHOUT the funds UPSETS the SuperComputer enough
     * to kill the worker on the spot.
     * @author esoo0013
     */
    @Override
    public String cannotAfford(Actor buyer, GameMap map) {
        // Unconscious method to kill the buyer immediately.
        buyer.unconscious(map);
        return "The Supercomputer is enraged by the audacity. " + buyer + " is killed on the spot.";
    }

}
