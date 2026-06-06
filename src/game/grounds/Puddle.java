package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.items.Consumable;
import game.items.ItemAbilities;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;

/**
 * A small, stationary body of mysterious liquid on the ground.
 * Stepping on it should be safe. Drinking from it, however... might be unhealthy.
 *
 * @author echu0057
 */
public class Puddle extends Ground implements Consumable {

    /**
     * Constructor for the Puddle class.
     */
    public Puddle() {
        super('~', "Puddle");
    }

    /**
     * Returns a list of allowable actions with this puddle.
     * The puddle may be consumed by the actor if said actor is directly on it.
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        // Get the default list of actions from Ground (actually empty).
        ActionList actions = super.allowableActions(actor, location, direction);
        // Allow consuming if the actor is directly on it.
        // Things to check: if there's actually someone on the puddle's location,
        // and if that someone is the same as the actor parameter.
        if (location.containsAnActor() && location.getActor() == actor) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Have the actor drink straight from the puddle.
     * This may or may not go well depending on whether the actor can sterilise the water.
     * @param actor the actor consuming from the puddle
     * @param map   the map the actor occupies
     * @return a string description of the result of consuming from this puddle
     */
    public String consumedBy(Actor actor, GameMap map) {
        // Check if the consumer has the STERILISER ability.
        if (actor.hasAbility(ItemAbilities.STERILISER)) {
            // Heal for 1 hp.
            actor.heal(1);
            return actor + " sterilises and drinks the puddle water, healing them by 1 hp.";

        } else {
            // Note that Actor doesn't implement Poisonable. Can't change that.
            // Based on the mars example, we can try to use the engine code to convert it.
            Poisonable poisonable = actor.asCapability(Poisonable.class).orElse(null);
            if (poisonable != null) {
                // Poison the poisonable actor (1 damage, lasts 3 turns).
                actor.addStatus(new PoisonStatus(3, 1, poisonable));
            }
            return actor + " was poisoned (1 dmg, 3 turns) from drinking toxic water directly!";
        }
    }

}
