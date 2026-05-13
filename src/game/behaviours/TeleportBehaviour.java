package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.actors.ActorAbilities;
import game.grounds.Teleporter;
import game.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeleportBehaviour implements Behaviour<Tree, Boolean> {
    private static final Random random = new Random();
    private final Teleporter teleporter;

    public TeleportBehaviour(Teleporter teleporter) {
        this.teleporter = teleporter;
    }

    @Override
    public Boolean operate(Tree tree, Location location) {
        List<Actor> nearbyWorkers = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() && destination.getActor().hasAbility(ActorAbilities.PLAYER)) {
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
