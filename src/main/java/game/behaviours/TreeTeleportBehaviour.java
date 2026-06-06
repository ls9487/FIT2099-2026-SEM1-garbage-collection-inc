package game.behaviours;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.grounds.Teleporter;
import game.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Teleports each adjacent worker to a random walkable tile on the map (mature warper tree).
 *
 * @author lden0031
 * @version 1.0
 */
public class TreeTeleportBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();
    private final Teleporter teleporter;

    /**
     * constructor
     * @param teleporter handler that relocates workers
     */
    public TreeTeleportBehaviour(Teleporter teleporter) {
        this.teleporter = teleporter;
    }

    /**
     * Teleports each adjacent worker to a random walkable tile on the map.
     * @param tree     the tree performing the behaviour
     * @param location the tree's tile
     * @return {@link Boolean#TRUE} when at least one worker was teleported, {@link Boolean#FALSE} otherwise
     */
    @Override
    public Boolean operate(Tree tree, Location location) {
        List<Actor> nearbyWorkers = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() && destination.getActor().hasAbility(ActorAbilities.TREE_ACTIVATOR)) {
                nearbyWorkers.add(destination.getActor());
            }
        }

        if (nearbyWorkers.isEmpty()) {
            return Boolean.FALSE;
        }

        List<Location> validDestinations = new ArrayList<>();
        for (int x : location.map().getXRange()) {
            for (int y : location.map().getYRange()) {
                Location candidate = location.map().at(x, y);
                if (!candidate.containsAnActor() && candidate.canActorEnter(nearbyWorkers.get(0))) {
                    validDestinations.add(candidate);
                }
            }
        }

        if (validDestinations.isEmpty()) {
            return Boolean.FALSE;
        }

        for (Actor actor : nearbyWorkers) {
            int chosenDestinationIndex = random.nextInt(validDestinations.size());
            Location destination = validDestinations.get(chosenDestinationIndex);

            teleporter.teleport(actor, location.map(),  destination);
            validDestinations.remove(chosenDestinationIndex);
        }

        return Boolean.TRUE;
    }
}