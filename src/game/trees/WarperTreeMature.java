package game.trees;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.TreeTeleportBehaviour;
import game.grounds.Teleporter;

public class WarperTreeMature extends Tree implements Teleporter {
    private static final char DISPLAY_CHAR = 'W';
    private static final String NAME = "Warper Tree Mature";
    private static final int TELEPORT_BEHAVIOUR_PRIORITY = 1;

    public WarperTreeMature() {
        super(DISPLAY_CHAR, NAME);
        addNewBehaviour(TELEPORT_BEHAVIOUR_PRIORITY, new TreeTeleportBehaviour(this));
    }

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
