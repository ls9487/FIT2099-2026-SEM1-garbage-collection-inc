package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;

/**
 * Tier-2 door that overheats neighbouring floor tiles when opened.
 *
 * @author eche0116
 * @version 1.0
 */
public class IronDoor extends Door {

    private static final int FIRE_DURATION = 5;
    private static final int NEARBY_RADIUS = 1;
    private static final int CLEARANCE_LEVEL = 2;

    /**
     * Creates an iron door marked with {@code N} on facility plans.
     *
     * @author eche0116
     * @version 1.0
     */
    public IronDoor() {
        super('N', "Iron Door");
    }

    /**
     * Declares the minimum clearance tier required to unlock this door.
     *
     * @return clearance level two
     * @author eche0116
     * @version 1.0
     */
    @Override
    protected int requiredClearance() {
        return CLEARANCE_LEVEL;
    }

    /**
     * Unlock IronDoor will ignite adjacent floor tiles
     *
     * @param actor the unlocking worker
     * @param map   the map used to locate adjacent floors
     * @return unlock narrative including fire spread
     * @author eche0116
     * @version 1.0
     */
    @Override
    protected String unlockSideEffects(Actor actor, GameMap map) {
        Location doorLocation = null;
        for (Location location : map.locationOf(actor).getNearbyLocations(NEARBY_RADIUS)) {
            if (location.getGround() == this) {
                doorLocation = location;
                break;
            }
        }
        if (doorLocation != null) {
            for (Location neighbour : doorLocation.getNearbyLocations(NEARBY_RADIUS)) {
                if (neighbour.getGround().canActorEnter(actor)) {
                    neighbour.addItem(new Fire(FIRE_DURATION));
                }
            }
        }
        return actor + " unlocked the iron door. The mechanism overheats, igniting adjacent floor tiles.";
    }
}