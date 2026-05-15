package game.behaviours;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.InfectAction;
import game.actors.Infector;
import game.statuses.Infectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Behaviour for an actor parasitically infecting whatever it can infect around it.
 *
 * @author echu0057
 */
public class InfectBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();
    private final Infector infector;

    /**
     * Constructor for the InfectBehaviour class.
     * Which infector is trying to cause infection.
     *
     * @param infector The infector involved in this behaviour.
     */
    public InfectBehaviour(Infector infector) {
        this.infector = infector;
    }

    /**
     * Checks surrounding locations for infectables, generating an InfectAction for each one.
     * Then, randomly chooses one InfectAction (one specific infectable target) and returns it.
     * @param actor The actor performing the behaviour.
     * @param location The location of the current actor.
     * @return An action taken by this behaviour.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        ActionList actions = new ActionList();
        // Keep track of surrounding entities to be processed later.
        List<GameEntity> surroundingEntities = new ArrayList<>();

        // Get all the surrounding entities from each exit.
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            // Get the surrounding actor.
            if (destination.containsAnActor()) {
                surroundingEntities.add(destination.getActor());
            }
            // Get the surrounding ground (grounds cannot be infected for now,
            // but they could be in the future...)
            surroundingEntities.add(destination.getGround());
            // Get the loose items.
            surroundingEntities.addAll(destination.getItems());
        }

        // For each entity, if it's infectable, generate an InfectAction for it.
        for (GameEntity entity : surroundingEntities) {
            Infectable infectable = entity.asCapability(Infectable.class).orElse(null);
            if (infectable != null) {
                actions.add(new InfectAction(entity, infector));
            }
        }

        // Randomly choose what to infect, or return null if there's nothing to infect.
        if (actions.size() > 0) {
            return actions.get(random.nextInt(actions.size()));
        }
        else
        {
            return null;
        }
    }

}
