package game.vehicles;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.DismountAction;
import game.actions.MountAction;
import game.items.EclipseItem;

/**
 * Abstract non-portable vehicle that an actor can mount or dismount.
 * Mounting adds VehicleAbilities.MOUNTED to the rider and places this rideable in the rider's inventory
 * dismounting removes the ability and returns the rideable to the ground.
 *
 * @author lyan0121
 * @version 1.0
 */
public abstract class Rideable extends EclipseItem {
    /**
     * Creates a rideable with the given display name and map character.
     *
     * @param name the name of this item
     * @param displayChar the character used when this item is on the ground
     */
    public Rideable(String name, char displayChar) {
        super(name, displayChar, 0);
        makeNonPortable();
    }

    /**
     * Mounts this rideable: removes it from the ground, adds it to the actor's inventory,
     * and applies VehicleAbilities.MOUNTED to the rider.
     *
     * @param actor the actor mounting this rideable
     * @param itemLocation the ground location where this rideable currently lies
     * @return a short narrative description of the mount
     */
    public String mount(Actor actor, Location itemLocation) {
        // cannot use pickup action then execute because non-portable
        itemLocation.removeItem(this);
        actor.getInventory().add(this);
        actor.enableAbility(VehicleAbilities.MOUNTED);
        return String.format("%s mounts %s", actor, this);
    }

    /**
     * Dismounts this rideable: removes it from the actor's inventory, places it on the
     * actor's current tile, and clears VehicleAbilities.MOUNTED from the rider.
     *
     * @param actor the actor dismounting this rideable
     * @param map the map containing the actor
     * @return a short narrative description of the dismount
     */
    public String dismount(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        map.locationOf(actor).addItem(this);
        actor.disableAbility(VehicleAbilities.MOUNTED);
        return String.format("%s dismounts %s", actor, this);
    }

    /**
     * List of allowable actions that can be performed on the item when it is on the ground
     *
     * @param location the location of the ground on which the item lies
     * @return an unmodifiable list of Actions
     */
    @Override
    public ActionList allowableActions(Location location) {
        ActionList actions = new ActionList();
        if (location.containsAnActor() && !location.getActor().hasAbility(VehicleAbilities.MOUNTED)) {
            actions.add(new MountAction(this, location));
        }
        return actions;
    }

    /**
     * While carried by a mounted rider, offers a dismount action.
     *
     * @param owner the actor carrying this rideable
     * @param map the map the owner occupies
     * @return an action list containing DismountAction when applicable
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return new ActionList(new DismountAction(this));
    }
}
