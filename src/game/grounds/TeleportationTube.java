package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.items.Fire;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Company-installed teleportation tube offering multiple preset arrival sites
 * with hazardous energy discharge at the destination.
 *
 * @author eche0116
 * @version 1.0
 */
public class TeleportationTube extends Ground implements Teleporter {
    private static final int FIRE_DURATION = 2;
    private static final int NEARBY_RADIUS = 1;
    private static final Random random = new Random();
    private final List<Location> destinations;

    /**
     * Creates a tube bound to the supplied arrival locations (maps may differ).
     *
     * @param destinations preset arrival tiles
     */
    public TeleportationTube(List<Location> destinations) {
        super('Φ', "Teleportation Tube");
        this.destinations = destinations;
    }

    @Override
    public String teleport(Actor actor, GameMap map, Location destination) {
        Location finalDestination = destination;
        boolean malfunctionsMessage = false;

        if (random.nextBoolean()) {
            malfunctionsMessage = true;
            List<Location> candidates = new ArrayList<>();
            for (int y : finalDestination.map().getYRange()) {
                for (int x : finalDestination.map().getXRange()) {
                    Location loc = finalDestination.map().at(x, y);
                    if (!loc.containsAnActor() && loc.getGround().canActorEnter(actor)) {
                        candidates.add(loc);
                    }
                }
            }
            finalDestination = candidates.get(random.nextInt(candidates.size()));
        }

        finalDestination.map().moveActor(actor, destination);
        for (Location location : finalDestination.getNearbyLocations(NEARBY_RADIUS)) {
            location.addItem(new Fire(FIRE_DURATION));
        }

        return String.format("%s teleported to %s by %s.%s",
                actor,
                destination,
                this,
                malfunctionsMessage ?
                        String.format(
                                " But device malfunctioned and randomly teleport %s to %s",
                                actor,
                                finalDestination) :
                        "");
    }

    /**
     * Exposes one menu entry per destination when a worker stands beside the tube.
     *
     * @param actor     the acting actor
     * @param location  the tube tile
     * @param direction direction label from the engine
     * @return teleport actions for each configured destination
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        for (Location destination : destinations) {
            actions.add(new TeleportAction(this, destination));
        }
        return actions;
    }
}