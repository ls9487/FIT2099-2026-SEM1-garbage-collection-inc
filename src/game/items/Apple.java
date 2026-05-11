package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.actors.ActorAbilities;
import game.statuses.PoisonStatus;
import game.statuses.Poisonable;

/**
 * Apple represents an apple that really has seen better days.
 * It's spoiled and poisons the consumer when eaten, unless it's sterilised.
 * If sterilised, it's pretty nutritious!
 *
 * @author echu0057
 */
public class Apple extends EclipseItem implements Consumable, Sellable {

    /**
     * Constructor for the Apple class.
     * Has a weight of 1 unit.
     */
    public Apple() {
        super("Apple", 'ó', 1);
    }

    /**
     * Returns a list of allowable actions that the owner can do with the apple.
     * Can be eaten as long as it's in the inventory.
     * @param owner The actor that owns the item.
     * @param map The map where the actor is performing the action on.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(owner, map);
        // Allow consuming.
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Returns a list of allowable actions that someone on the specified location
     * can do with the apple (on the ground, on the same location).
     * @param location The location of the ground on which the item lies.
     * @return A potentially non-empty ActionList.
     */
    @Override
    public ActionList allowableActions(Location location) {
        // Get the default list of actions from Item (actually empty).
        ActionList actions = super.allowableActions(location);
        // NOTE: Actors will only call this method on items they're standing on top of.
        // Anyway, only allow consuming if the actor can directly consume it.
        if (location.containsAnActor() && location.getActor().hasAbility(ActorAbilities.DIRECT_CONSUMER)) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Have the actor consume this apple.
     * Poisons if not sterilised, heals otherwise.
     * @param actor The actor consuming this apple.
     * @return A string description of the result of consuming this apple.
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        // Apple is eaten. Try to remove it from the actor's inventory.
        // If that fails, then the apple was on the ground. Still, remove it.
        if (!actor.getInventory().remove(this)) {
            map.locationOf(actor).removeItem(this);
        }
        // Check if the consumer has the STERILISER ability.
        if (actor.hasAbility(ItemAbilities.STERILISER)) {
            // Heal for 3 hp.
            actor.heal(3);
            return actor + " eats a sterilised apple, healing them by 3 hp.";

        } else {
            // Note that Actor doesn't implement Poisonable. Can't change that.
            // Based on the mars example, we can try to use the engine code to convert it.
            Poisonable poisonable = actor.asCapability(Poisonable.class).orElse(null);
            if (poisonable != null) {
                // Poison the poisonable actor (1 damage, lasts 5 turns).
                actor.addStatus(new PoisonStatus(5, 1, poisonable));
            }
            return actor + " was poisoned (1 dmg, 5 turns) from eating the spoiled apple!";
        }
    }

    /**
     * Apples sell for 1 credit. The Supercomputer doesn't care that they're spoiled.
     * @author esoo0013
     */
    @Override
    public int sellPrice(Actor seller) {
        return 1;
    }

    /**
     * If the seller is carrying a Sterilisation Box, the apple gets neutralised
     * on the way out and nothing nasty happens. Otherwise the seller catches
     * a 2-turn poison (2 dmg/turn) from handling the spoiled fruit. The apple
     * leaves the inventory either way.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        StringBuilder msg = new StringBuilder(seller + " sells an apple for 1 credit.");

        if (seller.hasAbility(ItemAbilities.STERILISER)) {
            msg.append(" The apple is neutralised by the Sterilisation Box on its way out.");
        } else {
            Poisonable poisonable = seller.asCapability(Poisonable.class).orElse(null);
            if (poisonable != null) {
                seller.addStatus(new PoisonStatus(2, 2, poisonable));
            }
            msg.append(" ").append(seller).append(" is poisoned (2 dmg, 2 turns) from handling the spoiled apple.");
        }

        seller.getInventory().remove(this);
        return msg.toString();
    }

}
