package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Moves one step away from the nearest worker within range.
 *
 * @author lyan0121
 * @version 1.0
 */
public class FleeBehaviour implements Behaviour<Actor, Action> {
    private final int fleeRange;

    /**
     * @param fleeRange maximum Chebyshev distance to detect workers
     */
    public FleeBehaviour(int fleeRange) {
        this.fleeRange = fleeRange;
    }

    /**
     * Moves one step away from the nearest worker within range.
     * @param actor    the entity performing the behaviour
     * @param location the entity's current tile
     * @return a move that increases distance from the nearest worker, or null
     */
    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();

        List<Actor> targetedWorkers = new ArrayList<>();
        for (Location target : location.getNearbyLocations(fleeRange)) {
            if(target.containsAnActor() && target.getActor().hasAbility(ActorAbilities.PLAYER)) {
                targetedWorkers.add(target.getActor());
            }
        }

        int nearestWorkerDistance = Integer.MAX_VALUE;
        Actor nearestWorker = null;

        for (Actor targetedWorker : targetedWorkers) {
            int newDistance = distance(map.locationOf(targetedWorker), location);
            if (nearestWorkerDistance > newDistance) {
                nearestWorkerDistance = newDistance;
                nearestWorker = targetedWorker;
                if (nearestWorkerDistance == 1) break;
            }
        }

        if(!map.contains(nearestWorker) || !map.contains(actor))
            return null;

        Location here = map.locationOf(actor);
        Location there = map.locationOf(nearestWorker);

        int currentDistance = distance(here, there);
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                int newDistance = distance(destination, there);
                if (newDistance > currentDistance) {
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
