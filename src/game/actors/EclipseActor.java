package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.behaviours.FollowBehaviour;
import game.statuses.Alarmable;
import game.statuses.Flammable;
import game.statuses.Poisonable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * An abstract class representing an actor for this game (Garbage Collection Inc.)
 * According to this assignment, all actors related to the game can be burned and poisoned.
 * Additionally, all actors can be alarmed.
 *
 * @author echu0057
 */
public abstract class EclipseActor extends Actor implements Poisonable, Flammable, Alarmable
{

    private final Map<Integer, Behaviour<Actor, Action>> behaviours;

    /** Maximum amount of credits a worker can hold. */
    public static final int MAX_CREDITS = 1000;

    /**
     * Constructor for the EclipseActor class.
     * @param name The name of the actor.
     * @param displayChar The display character of the actor on the map.
     * @param hitPoints The health value of the actor.
     * @param inventory The inventory of the actor.
     */
    public EclipseActor(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);

        this.behaviours = new TreeMap<>();

        // Workers start with 0 credits, while the statistic itself + it keeps track of the 1000-credit cap
        this.addNewStatistic(EclipseStatistics.CREDITS, new BaseStatistic(MAX_CREDITS));
        this.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.UPDATE, 0);
    }

    /**
     * Add a new behaviour to the actor, along with a priority.
     * @param priority The priority of the behaviour. Lower values are iterated over first.
     * @param behaviour The behaviour to be added.
     */
    public void addNewBehaviour(Integer priority, Behaviour behaviour) {
        this.behaviours.put(priority, behaviour);
    }

    /**
     * Removes a behaviour from the actor based on its priority.
     * @param priority The priority of the behaviour to be removed.
     */
    public void removeBehaviour(Integer priority) {
        // Based on how Map works there's no error if the key doesn't exist.
        this.behaviours.remove(priority);
    }

    /**
     * Return an unmodifiable collection of the behaviours' values.
     * The ordering will start from the lowest to highest priority (due to TreeMap).
     * Subclasses may need to use this to access behaviour values, since behaviours is private.
     */
    protected Collection<Behaviour<Actor, Action>> getBehaviourValues() {
        // Note that Map.values() returns a Collection type, hence unmodifiableCollection.
        return Collections.unmodifiableCollection(behaviours.values());
    }

    /**
     * Makes the burning actor take burn damage.
     * @param damage The damage dealt via burn.
     */
    public void burn(int damage) {
        this.hurt(damage);
    }

    /**
     * Makes the poisoned actor take poison damage.
     * @param damage The damage dealt via poison.
     */
    public void poison(int damage) {
        this.hurt(damage);
    }

    /**
     * When alarmed, hostile actors will start pursuing the actor (worker) who tripped it.
     * This is done by adding a lower-priority behaviour before its WanderBehaviour.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void enableAlarmed(Actor alarmTripper) {
        if (this.hasAbility(ActorAbilities.WORKER_HOSTILE)) {
            this.addNewBehaviour(998, new FollowBehaviour(alarmTripper));
        }
    }

    /**
     * Transition back to the non-alarmed state.
     * Removes the FollowBehaviour that the hostile entity once had.
     * @param alarmTripper The actor that tripped the alarm.
     */
    public void disableAlarmed(Actor alarmTripper) {
        if (this.hasAbility(ActorAbilities.WORKER_HOSTILE)) {
            this.removeBehaviour(998);
        }
    }

    /**
     * Returns the actor's current credit balance.
     * @return current amount of credits owned by the actor.
     * @author esoo0013
     */
    public int getCredits() {
        return this.getStatistic(EclipseStatistics.CREDITS);
    }

    /**
     * Adds credits to the actor.
     * Values above the maximum cap are automatically clamped by the statistic system.
     * @param amount Number of credits to add.
     * @author esoo0013
     */
    public void addCredits(int amount) {
        if (amount <= 0) {
            return;
        }

        this.modifyStatistic(
                EclipseStatistics.CREDITS,
                StatisticOperations.INCREASE,
                amount
        );
    }

    /**
     * Removes credits from the actor.
     * The balance will NEVER go below 0.
     * @param amount Number of credits to deduct.
     * @author esoo0013
     */
    public void deductCredits(int amount) {
        if (amount <= 0) {
            return;
        }

        this.modifyStatistic(
                EclipseStatistics.CREDITS,
                StatisticOperations.DECREASE,
                amount
        );
    }

    /**
     * Checks whether the actor has enough credits
     * for a transaction.
     *
     * @param amount Required amount.
     * @return true if the actor can afford it.
     * @author esoo0013
     */
    public boolean canAfford(int amount) {
        return getCredits() >= amount;
    }

}
