package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

public class FindItemBehaviour implements Behaviour<Actor, Action> {
    private final int findingRange;

    public FindItemBehaviour(int findingRange) {
        this.findingRange = findingRange;
    }

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
     * @param actor   The entity performing the behaviour
     * @param location The location of the current entity
     * @return The result of the behaviour, or null if no valid operation could be performed
     * @author Riordan D. Alfredo
     */
    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();

        int nearestItemDistance = Integer.MAX_VALUE;
        Location nearestItemLocation = null;
        for (Location target : location.getNearbyLocations(findingRange)) {
            if (target.getItems().isEmpty() || !target.getItems().getFirst().hasAbility(ItemAbility.PORTABLE)) continue;
            int newDistance = distance(location, target);
            if (newDistance < nearestItemDistance) {
                nearestItemDistance = newDistance;
                nearestItemLocation = target;
            }
        }

        if (nearestItemLocation == null) return null;

        Location here = map.locationOf(actor);

        int currentDistance = distance(here, nearestItemLocation);
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                int newDistance = distance(destination, nearestItemLocation);
                if (newDistance < currentDistance) {
                    return new MoveActorAction(destination, exit.getName());
                }
            }
        }

        return null;
    }


    /**
     * Compute the Manhattan distance between two locations.
     *
     * @param a the first location
     * @param b the first location
     * @return the number of steps between a and b if you only move in the four cardinal directions.
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
