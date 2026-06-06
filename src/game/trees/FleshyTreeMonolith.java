package game.trees;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.TreeTeleportBehaviour;
import game.grounds.Teleporter;

/**
 * Mature fleshy tree that teleports actors to linked destinations.
 *
 * @author lden0031
 * @version 1.0
 */
public class FleshyTreeMonolith extends Tree implements Teleporter {
    private static final char DISPLAY_CHAR = 'H';
    private static final String NAME = "Fleshy Tree Monolith";
    private static final int TELEPORT_BEHAVIOUR_PRIORITY = 1;

    /**
     * Creates a monolith with tree teleport behaviour enabled.
     */
    public FleshyTreeMonolith() {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(TELEPORT_BEHAVIOUR_PRIORITY, new TreeTeleportBehaviour(this));
    }

    /**
     * Moves the actor to the destination location on the linked map.
     *
     * @param actor       the actor being teleported
     * @param map         the map the actor occupies before teleporting
     * @param destination the tile to move the actor to
     * @return a message describing the teleport
     */
    @Override
    public String teleport(Actor actor, GameMap map, Location destination) {
        destination.map().moveActor(actor, destination);
        return String.format("%s teleported to %s by %s.",
                actor,
                destination,
                this
        );
    }
}
