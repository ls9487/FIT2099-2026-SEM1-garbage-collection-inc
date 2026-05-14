package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.ConsumeBehaviour;
import game.behaviours.WanderBehaviour;
import game.inventories.BasicInventory;

/**
 * Slime represents a mutated slime creature.
 * Wanders around and eats stuff on the ground. Whatever workers can consume, it can too.
 *
 * @author echu0057
 */
public class Slime extends EclipseActor {

    /**
     * Constructor for the Slime class. Has 25 hp.
     * Can wander around.
     */
    public Slime() {
        super("Slime", '⍾', 25, new BasicInventory());
        this.enableAbility(ActorAbilities.DIRECT_CONSUMER);
        this.addNewBehaviour(1, new ConsumeBehaviour());
        this.addNewBehaviour(999, new WanderBehaviour());
    }

    /**
     * First checks whether the current actor is unconscious. Does nothing if so.
     * Can also handle multi-turn actions by getting the subsequent action returned by the previous action.
     * Finally, it takes all possible actions (supplied by engine code) and shows it on the
     * console menu for the player to choose.
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

}
