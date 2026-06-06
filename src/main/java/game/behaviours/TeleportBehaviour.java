package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.actors.ActorAbilities;
import game.grounds.Teleporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Teleports the acting creature to a random walkable tile within range (wisp self-teleport).
 *
 * @author lyan0121
 * @version 1.0
 */
public class TeleportBehaviour implements Behaviour<Actor, Action> {
    private static final Random random = new Random();
    private final int teleportRange;
    private final Teleporter teleporter;

    /**
     * constructor
     * @param teleportRange maximum Chebyshev distance for destination selection
     * @param teleporter    handler that performs the relocation
     */
    public TeleportBehaviour(int teleportRange, Teleporter teleporter) {
        this.teleportRange = teleportRange;
        this.teleporter = teleporter;
    }
    /**
     * Teleports the acting creature to a random walkable tile within range (wisp self-teleport).
     *
     * @param actor    the entity performing the behaviour
     * @param location the entity's current tile
     * @return a TeleportAction, or null when no worker or destination exists
     */
    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();

        List<Location> enterableTiles = new ArrayList<>();
        List<Actor> targetedWorkers = new ArrayList<>();
        for (Location target : location.getNearbyLocations(teleportRange)) {
            if(target.containsAnActor() && target.getActor().hasAbility(ActorAbilities.PLAYER)) {
                targetedWorkers.add(target.getActor());
            }

            if (target.canActorEnter(actor)) enterableTiles.add(target);
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

        Location destination = enterableTiles.get(random.nextInt(enterableTiles.size()));

        return new TeleportAction(teleporter, destination);
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
