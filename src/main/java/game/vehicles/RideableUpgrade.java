package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.items.EclipseItem;
import game.statuses.RideStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Portable upgrade item whose granted vehicle abilities are active only while the
 * carrier has RideStatus (is mounted on a rideable).
 *
 * @author lyan0121
 * @version 1.0
 */
public abstract class RideableUpgrade extends EclipseItem {
    private List<Enum<?>> abilities;

    /**
     * Creates a portable rideable upgrade.
     *
     * @param name the name of this item
     * @param displayChar the character used when this item is on the ground
     * @param weight inventory weight of this upgrade
     */
    public RideableUpgrade(String name, char displayChar, int weight) {
        super(name, displayChar, weight);
        abilities = new ArrayList<>();
    }

    /**
     * Records an ability to grant to the carrier while mounted.
     *
     * @param ability the vehicle ability enum value to register
     */
    @Override
    public void enableAbility(Enum<?> ability) {
        if (!abilities.contains(ability))
            abilities.add(ability);
    }

    /**
     * Stops granting the given ability when the carrier is not mounted.
     *
     * @param ability the vehicle ability enum value to unregister
     */
    @Override
    public void disableAbility(Enum<?> ability) {
        if (abilities.contains(ability))
            abilities.remove(ability);
    }

    /**
     * Each turn, enables registered abilities on the carrier while mounted and
     * disables them otherwise.
     *
     * @param currentLocation the location of the actor carrying this item
     * @param actor the actor carrying this upgrade
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        updateAbilities(actor);
    }

    /**
     * enables this update abilities when actor is mounting on a rideable or
     * disables this update abilities when actor is not mounting on a rideable
     * @param actor the actor carrying this upgrade
     */
    private void updateAbilities(Actor actor) {
        if (actor.hasStatus(RideStatus.class)) {
            for (Enum<?> ability : abilities) {
                super.enableAbility(ability);
            }
        } else {
            for (Enum<?> ability : abilities) {
                super.disableAbility(ability);
            }
        }
    }

    /**
     * Make the item portable, allowing the item to be picked up or dropped by actor
     */
    @Override
    public void makePortable() {
        if (!this.hasAbility(ItemAbility.PORTABLE)) {
            super.enableAbility(ItemAbility.PORTABLE);
        }
    }

    /**
     * Make the item non-portable, preventing the item from being picked up or dropped by actor
     */
    @Override
    public void makeNonPortable() {
        if (this.hasAbility(ItemAbility.PORTABLE)) {
            super.disableAbility(ItemAbility.PORTABLE);
        }
    }
}
