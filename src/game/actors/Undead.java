package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.AttackBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;
import game.statuses.Infectable;
import game.weapons.BareFist;

/**
 * Undead represents a reanimated corpse of a worker who perished.
 * Seems to harbour extreme hostility towards the company and its workers.
 * Workers should steer clear and avoid getting beaten up by its fists, even if they're inaccurate.
 * The Bug class in the forest example was used as a reference here.
 *
 * @author echu0057
 */
public class Undead extends EclipseActor implements Infectable {

    /**
     * Constructor for the Undead class. Has 15 hp.
     * Can attack with bare fists, dealing 1 damage with a 10% hit rate.
     * Hostile to workers and will attack them if nearby.
     * Can wander around if there's nothing else to do.
     */
    public Undead() {
        super("Undead", 'Ѫ', 15, new BasicInventory());
        this.setIntrinsicWeapon(new BareFist(1, 10));
        this.enableAbility(ActorAbilities.WORKER_HOSTILE);
        this.addNewBehaviour(1, new AttackBehaviour());
        this.addNewBehaviour(999, new WanderBehaviour());
    }

    /**
     * First checks whether the current actor is unconscious. Does nothing if so.
     * Can also handle multi-turn actions by getting the subsequent action returned by the previous action.
     * It will then check its behaviours to determine what action to do.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action that is chosen in the current turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Forced if the actor isn't conscious.
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Handle multi-turn Actions.
        if (lastAction != null && lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        // Consult each behaviour for what it can do, and perform the first valid action.
        for (Behaviour<Actor, Action> behaviour : this.getBehaviourValues()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }
        // No valid action was taken, so just do nothing.
        return new DoNothingAction();

    }

    /**
     * The infection causes the undead to kaboom and instantly die.
     * Note that the "blowing up" doesn't actually affect its surroundings.
     * @param location The location where the infection tick is happening.
     */
    @Override
    public void infection(Location location) {
        // Damage equal to max hp guarantees killing it.
        this.hurt(this.getMaximumStatistic(ActorStatistics.HEALTH));
    }

}
