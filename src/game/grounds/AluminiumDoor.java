package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CutAction;
import game.actions.UnlockAction;
import game.items.AluminiumScrap;
import game.items.Cuttable;
import game.items.ItemAbilities;
import game.items.PlasmaCutter;

import java.util.Random;

/**
 * Tier-1 door that shocks the opener from exposed wiring.
 *
 * @author eche0116
 * @version 1.0
 */
public class AluminiumDoor extends Door implements Cuttable {

    private static final int SHORT_CIRCUIT_DAMAGE = 2;
    private static final int SECURITY_LEVEL = 1;

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

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (canActorUnlock(actor)) {
            actions.add(new UnlockAction(this));
        }
        // Add CutAction if actor holds a PlasmaCutter
        boolean hasPlasmaCutter = actor.getInventory().getItems().stream().anyMatch(item -> item.hasAbility(ItemAbilities.CUTTER));
        if (hasPlasmaCutter) {
            actions.add(new CutAction(this, location));
        }
        return actions;
    }

    @Override
    public String cutBy(Actor actor, GameMap map, Location location) {
        location.addItem(new AluminiumScrap());
        location.setGround(new Floor());

        // 25% chance to explode
        StringBuilder result = new StringBuilder(
                String.format("%s cuts the Aluminium Door — it crumbles into scrap!", actor));
        if (new Random().nextDouble() < 0.25) {
            result.append(" The door EXPLODES!");
            for (Exit exit : map.locationOf(actor).getExits()) {
                Location adjacent = exit.getDestination();
                if (adjacent.containsAnActor()) {
                    adjacent.getActor().hurt(100);
                    result.append(String.format(" %s takes 100 damage!", adjacent.getActor()));
                }
            }
        }
        return result.toString();
    }
}