package game.vehicles;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.IgniteAction;
import game.grounds.ToxicWaste;
import game.items.Fire;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Portable rideable upgrade that grants VehicleAbilities.EXTRA_ENERGY while mounted.
 * Each turn it pulls adjacent items into the rider's inventory and leaves toxic waste on
 * the tile the rider occupied previously; the rider can ignite an adjacent actor by
 * sacrificing a random inventory item to place fire under them.
 *
 * @author lyan0121
 * @version 1.0
 */
public class MagneticField extends RideableUpgrade implements Igniter {
    private static final Random random = new Random();
    private static final int FIRE_DURATION = 4;
    private static final String NAME = "Magnetic Field";
    private static final char DISPLAY_CHAR = '∩';
    private static final int WEIGHT = 20;
    private Location previousLocation;

    /**
     * Creates a magnetic field upgrade with extra energy registered.
     */
    public MagneticField() {
        super(NAME, DISPLAY_CHAR, WEIGHT);
        previousLocation = null;
        enableAbility(VehicleAbilities.EXTRA_ENERGY);
    }

    /**
     * Each turn while mounted, collects adjacent items and corrupts the previous tile to toxic waste.
     *
     * @param currentLocation the rider's current location
     * @param actor the actor carrying this upgrade
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        super.tick(currentLocation, actor);

        if (actor.hasAbility(VehicleAbilities.MOUNTED)) {
            Display display = new Display();
            for (Location here : currentLocation.getNearbyLocations(1)) {

                List<Item> itemsOnGround = new ArrayList<>(here.getItems());
                for (Item item : itemsOnGround) {
                    if (item.hasAbility(ItemAbility.PORTABLE)) {
                        actor.getInventory().add(item);
                        here.removeItem(item);

                        display.println(String.format("%s ran into %s's %s", item, actor, this));
                    }
                }
            }

            if (previousLocation == null) previousLocation = currentLocation;
            else previousLocation.setGround(new ToxicWaste());


        }
        previousLocation = currentLocation;
    }

    /**
     * Removes a random inventory item and places fire under the adjacent target for four turns.
     *
     * @param actor the rider using this upgrade
     * @param map the map containing the actors
     * @param target the adjacent actor to ignite
     * @return a narrative description of the ignite outcome
     */
    @Override
    public String ignite(Actor actor, GameMap map, Actor target) {
        List<Item> actorItems = actor.getInventory().getItems();
        int removeItemIndex = random.nextInt(actorItems.size());
        Item removedItem = actorItems.get(removeItemIndex);
        actorItems.remove(removeItemIndex);
        map.locationOf(target).addItem(new Fire(FIRE_DURATION));
        return String.format("%s uses %s to ignite %s and throw Fire to %s", actor, this, removedItem, target);
    }

    /**
     * While mounted, offers ignite against each adjacent actor.
     *
     * @param owner the actor carrying this upgrade
     * @param map the map the owner occupies
     * @return actions to ignite adjacent actors when present
     */
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (owner.hasAbility(VehicleAbilities.MOUNTED)) {
            for (Exit exit : map.locationOf(owner).getExits()) {
                Location here = exit.getDestination();
                if (here.containsAnActor() && !owner.getInventory().getItems().isEmpty()) {
                    actions.add(new IgniteAction(this, here.getActor()));
                }
            }
        }

        return actions;
    }
}
