package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.items.PickUpAction;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Picks up a random portable item on the actor's current tile.
 * @author lyan0121
 * @version 1.0
 */
public class PickItemBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();

    /**
     * Picks up a random portable item on the actor's current tile.
     *
     * @param entity   the entity performing the behaviour
     * @param location the entity's current tile
     * @return a pick-up action for a random item here, or null if the tile is empty
     */
    @Override
    public Action operate(Actor entity, Location location) {
        ActionList actions = new ActionList();
        for (Item item : location.getItems()) {
            if (item.hasAbility(ItemAbility.PORTABLE))
                actions.add(new PickUpAction(item));
        }

        if (actions.size() == 0) return null;
        return actions.get(random.nextInt(actions.size()));
    }
}
