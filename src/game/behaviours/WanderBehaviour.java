package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Behaviour for an actor wandering around (to any random adjacent spot that can it can move to).
 * Taken from the forest example.
 *
 * @author Riordan Alfredo
 * @author echu0057
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();

    /**
     * Returns an action (MoveActorAction) for an actor wandering around.
     * Where it moves to is chosen randomly from valid spots it can move to.
     * @param actor The actor performing the behaviour.
     * @param location The location of the current actor.
     * @return An action taken by this behaviour.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        ActionList actions = new ActionList();

        // Get the surroundings and a MoveActorAction for each valid destination.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                actions.add(exit.getDestination().getMoveAction(actor, "around", exit.getHotKey()));
            }
        }

        // Randomly choose where to move to, or return null if there's nowhere to go.
        if (actions.size() > 0) {
            return actions.get(random.nextInt(actions.size()));
        }
        else
        {
            return null;
        }
    }


}
