package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.InfectBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;

/**
 * Parasite represents an invasive alien lifeform.
 * Multiple entities are susceptible to being infected by a parasite.
 * With high health and being able to reproduce, these creatures pose a severe threat!
 *
 * @author echu0057
 */
public class Parasite extends EclipseActor implements Infector {
    private static final int INFECT_BEHAVIOUR_PRIORITY = 1;
    private static final int WANDER_BEHAVIOUR_PRIORITY = 999;

    /**
     * Constructor for the Parasite class. Has 30 hp.
     * Tries to infect whatever it comes across that can be infected.
     * Can wander around if there's nothing else to do.
     */
    public Parasite() {
        super("Parasite", 'x', 30, new BasicInventory());
        this.addNewBehaviour(INFECT_BEHAVIOUR_PRIORITY, new InfectBehaviour());
        this.addNewBehaviour(WANDER_BEHAVIOUR_PRIORITY, new WanderBehaviour());
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
     * Kills the parasite immediately when it infects something.
     * Because the parasite lives inside the infected thing now, rather than existing on its own.
     * @return A String description of what happens to itself, which is dying.
     */
    @Override
    public String infectingSelfEffect() {
        // Do damage equal to the parasite's max hp (instantly killing it).
        this.hurt(this.getMaximumStatistic(ActorStatistics.HEALTH));
        return String.format("%s dies as a result.", this);
    }

}
