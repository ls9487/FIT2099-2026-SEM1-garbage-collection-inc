package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CutAction;
import game.items.AluminiumScrap;
import game.items.Cuttable;
import game.items.ItemAbilities;

import java.util.Random;

/**
 * Tier-1 door that shocks the opener from exposed wiring.
 *
 * @author eche0116
 * @version 1.0
 */
public class AluminiumDoor extends Door implements Cuttable {

    private static final Random random = new Random();
    private static final int SHORT_CIRCUIT_DAMAGE = 2;
    private static final int SECURITY_LEVEL = 1;
    private static final int EXPLOSION_DAMAGE = 100;
    private static final double EXPLOSION_CHANCE = 0.25;

    /**
     * Creates an aluminium door using the standard facility symbol.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door", SECURITY_LEVEL);
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

    /**
     * As well as the allowed actions Door provides, AluminiumDoor may also be cut.
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return A list of allowed actions.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        // Add CutAction if actor holds a PlasmaCutter (i.e. CUTTER ability).
        if (actor.hasAbility(ItemAbilities.CUTTER)) {
            actions.add(new CutAction(this, location));
        }

        return actions;
    }

    /**
     * Cutting the AluminiumDoor will make the current ground turn into a Floor.
     * 25% chance of exploding, dealing 100 damage to adjacent actors.
     * Drops an AluminiumScrap (regardless of explosion or not).
     * @param actor The actor performing the cut.
     * @param map   The map the actor is on.
     * @param location The location where the cuttable object was cut.
     * @return A string description of what happened.
     */
    @Override
    public String cutBy(Actor actor, GameMap map, Location location) {
        // Handle dropping AluminiumScrap and replacing with Floor.
        location.addItem(new AluminiumScrap());
        location.setGround(new Floor());

        // Prepare a description to return.
        StringBuilder result = new StringBuilder(
                String.format("%s cuts the Aluminium Door, it crumbles into scrap!", actor));

        if (random.nextDouble() < EXPLOSION_CHANCE) {
            // Explosion occurred, so everyone around takes 100 damage.
            result.append(" But the door EXPLODES, damaging everyone for ").append(EXPLOSION_DAMAGE)
                    .append(" damage!");
            for (Exit exit : location.getExits()) {
                Location adjacent = exit.getDestination();
                if (adjacent.containsAnActor()) {
                    adjacent.getActor().hurt(EXPLOSION_DAMAGE);
                }
            }
        }

        return result.toString();
    }
}