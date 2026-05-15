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
    private static final Random random = new Random();
    private static final int FIRE_DURATION = 2;
    private static final int NEARBY_RADIUS = 1;
    private static final double MALFUNCTION_CHANCE = 0.5;
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
        // The actual destination to be teleported to. Assume it's the original for now.
        Location finalDestination = destination;
        // Keep track whether there was a malfunction (for the string returned later).
        String malfunctionsMessage = "";

        if (random.nextDouble() <= MALFUNCTION_CHANCE) {
            // Malfunction happened. Get all locations of the original destination's map.
            // Then, choose a random location from all locations that the actor can enter.
            List<Location> candidates = new ArrayList<>();
            for (int y : destination.map().getYRange()) {
                for (int x : destination.map().getXRange()) {
                    Location loc = destination.map().at(x, y);
                    if (loc.canActorEnter(actor)) {
                        candidates.add(loc);
                    }
                }
            }
            // Due to the malfunction, the actual destination has changed.
            finalDestination = candidates.get(random.nextInt(candidates.size()));
            malfunctionsMessage = String.format(".. that doesn't seem right. The teleporter malfunctioned! %s was teleported to %s.", actor, finalDestination);
        }

        // Move the actor to the destination using the map's moveActor method.
        finalDestination.map().moveActor(actor, finalDestination);
        // Burn the adjacent tiles.
        for (Location location : finalDestination.getNearbyLocations(NEARBY_RADIUS)) {
            location.addItem(new Fire(FIRE_DURATION));
        }

        return String.format("%s was teleported to %s by %s.%s", actor, destination,
                this, malfunctionsMessage);
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
            if (!destination.containsAnActor())
                actions.add(new TeleportAction(this, destination));
        }
        return actions;
    }
}