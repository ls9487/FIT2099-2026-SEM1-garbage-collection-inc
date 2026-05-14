package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Tier-1 door that shocks the opener from exposed wiring.
 *
 * @author eche0116
 * @version 1.0
 */
public class AluminiumDoor extends Door {

    private static final int SHORT_CIRCUIT_DAMAGE = 2;
    private static final int CLEARANCE_LEVEL = 1;

    /**
     * Creates an aluminium door using the standard facility symbol.
     *
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door");
    }

    /**
     * Declares the minimum clearance tier required to unlock this door.
     *
     * @return clearance level one
     */
    @Override
    protected int requiredClearance() {
        return CLEARANCE_LEVEL;
    }

    /**
     * Unlock AluminiumDoor will shock actor for 2 damage
     *
     * @param actor the unlocking worker
     * @param map   the map (unused for shock damage)
     * @return successful unlock door message
     */
    @Override
    protected String unlockSideEffects(Actor actor, GameMap map) {
        actor.hurt(SHORT_CIRCUIT_DAMAGE);
        return actor + " unlocked the aluminium door. Faulty wiring shocks them for " + SHORT_CIRCUIT_DAMAGE + " damage.";
    }
}