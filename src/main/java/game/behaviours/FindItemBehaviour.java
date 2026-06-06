package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Moves one step toward the nearest portable ground item within a search radius.
 *
 * @author lyan0121
 * @version 1.0
 */
public class FindItemBehaviour implements Behaviour<Actor, Action> {
    private final int findingRange;

    /**
     * @param findingRange maximum distance to search for items
     */
    public FindItemBehaviour(int findingRange) {
        this.findingRange = findingRange;
    }

    /**
     * Moves one step toward the nearest portable ground item within a search radius.
     * @param actor    the entity performing the behaviour
     * @param location the entity's current tile
     * @return a move toward the nearest item, or null if none or no valid step exists
     */
    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();

        int nearestItemDistance = Integer.MAX_VALUE;
        Location nearestItemLocation = null;
        for (Location target : location.getNearbyLocations(findingRange)) {
            if (target.getItems().isEmpty() || !target.getItems().get(0).hasAbility(ItemAbility.PORTABLE)) continue;
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
     * @param b the second location
     * @return the number of steps between a and b if you only move in the four cardinal directions.
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
