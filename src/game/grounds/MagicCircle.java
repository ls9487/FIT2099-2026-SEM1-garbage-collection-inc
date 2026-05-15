package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Ritual circle that teleports a standing worker to another registered circle
 * and leaves a flask beside the arrival site.
 *
 * @author eche0116
 * @version 1.0
 */
public class MagicCircle extends Ground implements Teleporter {

    private static final Random random = new Random();
    private final MagicCircleGroup group;
    private final List<Supplier<Item>> spawnableItems;

    /**
     * Creates a circle belonging to a shared teleport network.
     *
     * @param group  peer registry
     */
    public MagicCircle(MagicCircleGroup group, List<Supplier<Item>> spawnableItems) {
        super('◎', "Magic Circle");
        this.group = group;
        this.spawnableItems = spawnableItems;
    }

    /**
     * Offers within-map teleportation when the worker stands on the circle.
     *
     * @param actor     the acting actor
     * @param location  this circle's tile
     * @param direction facing context from the engine
     * @return actions including teleport when peers exist
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        Location peer = group.randomPeerExcluding(location);
        if (peer == null) {
            return actions;
        }
        actions.add(new TeleportAction(this, peer));

        return actions;
    }

    @Override
    public void spawn(Location arrival) {
        Actor traveller = arrival.getActor();
        List<Location> candidates = new ArrayList<>();
        for (Exit exit : arrival.getExits()) {
            Location step = exit.getDestination();
            if (step.getGround().canActorEnter(traveller)) {
                candidates.add(step);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        Item spawnedItem = spawnableItems.get(random.nextInt(spawnableItems.size())).get();

        candidates.get(random.nextInt(candidates.size())).addItem(spawnedItem);
    }

    @Override
    public String teleport(Actor actor, GameMap map, Location destination) {
        destination.map().moveActor(actor, destination);
        spawn(destination);
        return String.format("%s teleported to %s by %s.",
                actor,
                destination,
                this
        );
    }
}