package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.SwapItemsAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Swaps portable items between two random ground tiles within range.
 *
 * @author lyan0121
 * @version 1.0
 */
public class SwapItemsBehaviour implements Behaviour<Actor, Action> {
    private static final Random random = new Random();
    private final int swapRange;

    /**
     * @param swapRange maximum Chebyshev distance to search for swappable tiles
     */
    public SwapItemsBehaviour(int swapRange) {
        this.swapRange = swapRange;
    }
    /**
     * Swaps portable items between two random ground tiles within range.
     * @param entity   the entity performing the behaviour
     * @param location the entity's current tile
     * @return a SwapItemsAction, or null when fewer than two valid tiles exist
     */
    @Override
    public Action operate(Actor entity, Location location) {
        List<Location> targetedTiles = new ArrayList<>();

        for (Location here : location.getNearbyLocations(swapRange)) {
            for (Item item : here.getItems()) {
                if (item.hasAbility(ItemAbility.PORTABLE)) {
                    targetedTiles.add(here);
                    break;
                }
            }
        }

        if (targetedTiles.size() < 2) return null;

        int fromIndex = random.nextInt(targetedTiles.size());
        Location from = targetedTiles.get(fromIndex);
        targetedTiles.remove(fromIndex);
        Item itemFrom = getRandomItemFromLocation(from);

        int toIndex = random.nextInt(targetedTiles.size());
        Location to = targetedTiles.get(toIndex);
//        targetedTiles.remove(toIndex);
        Item itemTo = getRandomItemFromLocation(to);

        return new SwapItemsAction(itemFrom, from, itemTo, to);

    }

    private Item getRandomItemFromLocation(Location location) {
        List<Item> items = new ArrayList<>();
        for (Item item : location.getItems()) {
            if (item.hasAbility(ItemAbility.PORTABLE))
                items.add(item);
        }
        int chosenItemIndex = random.nextInt(items.size());
        return items.get(chosenItemIndex);
    }
}
