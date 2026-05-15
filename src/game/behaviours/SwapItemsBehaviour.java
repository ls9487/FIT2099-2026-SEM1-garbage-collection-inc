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

public class SwapItemsBehaviour implements Behaviour<Actor, Action> {
    private static final Random random = new Random();
    private final int swapRange;

    public SwapItemsBehaviour(int swapRange) {
        this.swapRange = swapRange;
    }
    /**
     * A Behaviour represents a kind of objective that an entity can have.  For example,
     * it might want to seek out a particular kind of object, or follow another entity,
     * or run away and hide.  Each implementation of Behaviour helps the
     * entity to achieve its objective (returning a result or null if no useful result are available).
     * method that determines which Behaviour to perform.  This allows the Behaviour's logic
     * to be reused in other Actors via delegation instead of inheritance.
     * For example, an Actor(entity T)'s {@code playTurn()} method can use Behaviours to help decide which Action(result R) to
     * perform next.  It can also simply create Actions itself, and for simpler Actors this is
     * likely to be sufficient.
     * Using Behaviours allows us to modularise the code that decides what to do, and that means that it can be
     * reused if (e.g.) more than one kind of Actor needs to be able to seek, follow, or hide.
     *
     * @param entity   The entity performing the behaviour
     * @param location The location of the current entity
     * @return The result of the behaviour, or null if no valid operation could be performed
     * @author Riordan D. Alfredo
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
