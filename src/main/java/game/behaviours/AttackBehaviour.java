package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Behaviour for an actor attacking whatever it can attack around it.
 *
 * @author echu0057
 */
public class AttackBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();

    /**
     * Returns an action (AttackAction) for an actor wanting to attack.
     * Its target to attack is chosen randomly from whoever it can attack.
     * @param actor The actor performing the behaviour.
     * @param location The location of the current actor.
     * @return An action taken by this behaviour.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        ActionList actions = new ActionList();

        // Get the surroundings and an AttackAction for each valid target.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                // Ask the destination's actor for allowable actions.
                actions.add(destination.getActor().allowableActions(actor, exit.getName(), location.map()));
            }
        }

        // Randomly choose what to attack, or return null if there's nothing to attack.
        if (actions.size() > 0) {
            return actions.get(random.nextInt(actions.size()));
        }
        else
        {
            return null;
        }
    }

}
