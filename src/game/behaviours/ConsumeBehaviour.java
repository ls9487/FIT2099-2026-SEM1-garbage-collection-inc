package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Behaviour for an actor directly consuming whatever it can consume off the ground.
 *
 * @author echu0057
 */
public class ConsumeBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();

    /**
     * Returns an action (ConsumeAction) for a very hungry actor.
     * What it will consume is chosen randomly from whatever it can consume.
     * @param actor The actor performing the behaviour.
     * @param location The location of the current actor.
     * @return An action taken by this behaviour.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        ActionList actions = new ActionList();

        // Get all items on ground, on the current location and get their allowed actions.
        for (Item item : location.getItems()) {
            actions.add(item.allowableActions(location));
        }

        // Randomly choose what to consume, or return null if there's nothing to consume.
        if (actions.size() > 0) {
            return actions.get(random.nextInt(actions.size()));
        }
        else
        {
            return null;
        }
    }

}
