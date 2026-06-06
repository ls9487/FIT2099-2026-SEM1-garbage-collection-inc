package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Loose sand ground created when a wall is bulldozed.
 * Any actor standing on sand is randomly moved each turn to an empty enterable
 * tile within two steps of its current position.
 *
 * @author lyan0121
 * @version 1.0
 */
public class Sand extends Ground {
    private static final Random random = new Random();

    /**
     * Creates a sand tile that shifts actors standing on it each turn.
     */
    public Sand() {
        super('∴', "Sand");
    }

    /**
     * Each turn, relocates an actor on this tile to a random nearby empty location.
     *
     * @param location the location of this sand tile
     */
    @Override
    public void tick(Location location) {
        Display display = new Display();
        if (location.containsAnActor()) {
            Actor actorStanding = location.getActor();
            List<Location> nearbyLocation = new ArrayList<>();
            for (Location here : location.getNearbyLocations(2)) {
                if (!here.containsAnActor() && here.canActorEnter(actorStanding)) {
                    nearbyLocation.add(here);
                }
            }

            if (nearbyLocation.isEmpty()) {
                display.println(String.format("%s kept balance in %s because %s is being surrounded.", actorStanding, location, actorStanding));
                return;
            }

            Location slippedTo = nearbyLocation.get(random.nextInt(nearbyLocation.size()));
            location.map().moveActor(actorStanding, slippedTo);
            display.println(String.format("%s slipped to %s because %s is too slippery.", actorStanding, slippedTo, this));
        }
    }
}
