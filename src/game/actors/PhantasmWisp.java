package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Teleporter;
import game.inventories.ItemLimitedInventory;
import game.states.*;

import java.util.TreeMap;

/**
 * Mischievous wisp that lures, shadows, tinkers, and flees; can teleport workers when curious.
 *
 * @author lyan0121
 * @version 1.0
 */
public class PhantasmWisp extends StatefulCreature implements Teleporter {
    private static final String NAME = "Phantasm Wisp";
    private static final char DISPLAY_CHAR = '✦';
    private static final int HIT_POINTS = 30;
    private static final Emotion INITIAL_EMOTION = Emotion.MISCHIEVOUS;
    private static final int INVENTORY_SIZE = 3;
    private static final int VIGILANCE_RANGE = 5;

    /** Creates a Phantasm Wisp with default stats and emotion states. */
    public PhantasmWisp() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, initialiseInventory(), INITIAL_EMOTION, new TreeMap<>());
        addNewState(Emotion.MISCHIEVOUS, new LureState(this));
        addNewState(Emotion.CAUTION, new ShadowState(this));
        addNewState(Emotion.CURIOUS, new TinkerState(this));
        addNewState(Emotion.FEARFUL, new FleeState(this));
    }

    /**
     * initialise PhantasmWisp inventory
     * @return the inventory of PhantasmWisp which is an ItemLimitedInventory
     */
    private static Inventory initialiseInventory() {
        return new ItemLimitedInventory(INVENTORY_SIZE);
    }

    /**
     * @return how far this creature senses workers
     */
    @Override
    public int getVigilanceRange() {
        return VIGILANCE_RANGE;
    }

    /**
     * @return true when the creature cannot pick up more items
     */
    @Override
    public boolean isInventoryFull() {
        return this.getInventory().getItems().size() == INVENTORY_SIZE;
    }

    /**
     * Teleports actor to destination
     *
     * @param actor       entity to relocate
     * @param map         map containing the actor (unused)
     * @param destination arrival tile
     * @return narrative description of the teleport
     */
    @Override
    public String teleport(Actor actor, GameMap map, Location destination) {
        destination.map().moveActor(actor, destination);
        return String.format("%s teleported to %s by %s.",
                actor,
                destination,
                this
        );
    }
}
