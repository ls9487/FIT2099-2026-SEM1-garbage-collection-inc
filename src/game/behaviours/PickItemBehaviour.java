package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.PickUpAction;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

public class PickItemBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();

    /**
     * A Behaviour represents a kind of objective that an entity can have.  For example
     * it might want to seek out a particular kind of object, or follow another entity,
     * or run away and hide.  Each implementation of Behaviour helps the
     * entity to achieve its objective (returning a result or null if no useful result are available).
     * method that determines which Behaviour to perform.  This allows the Behaviour's logic
     * to be reused in other Actors via delegation instead of inheritance.
     * For example, an Actor(entity T)'s {@code playTurn()} method can use Behaviours to help decide which Action(result R) to
     * perform next.  It can also simply create Actions itself, and for simpler Actors this is
     * likely to be sufficient.
     * Using Behaviours allows us to modularise the code that decides what to do, and that means that it can be
     * reused if (e.g.) more than one kind of Actor needs to be able to seek, follow, or hide.
     *
     * @param entity   The entity performing the behaviour
     * @param location The location of the current entity
     * @return The result of the behaviour, or null if no valid operation could be performed
     * @author Riordan D. Alfredo
     */
    @Override
    public Action operate(Actor entity, Location location) {
        ActionList actions = new ActionList();
        for (Item item : location.getItems()) {
            actions.add(new PickUpAction(item));
        }
        return actions.get(random.nextInt(actions.size()));
    }
}
