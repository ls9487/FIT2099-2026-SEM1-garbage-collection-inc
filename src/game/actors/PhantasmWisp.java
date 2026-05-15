package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Teleporter;
import game.inventories.ItemLimitedInventory;
import game.states.*;

import java.util.TreeMap;

public class PhantasmWisp extends StatefulCreature implements Teleporter {
    private static final String NAME = "Phantasm Wisp";
    private static final char DISPLAY_CHAR = '✦';
    private static final int HIT_POINTS = 30;
    private static final Emotion INITIAL_EMOTION = Emotion.MISCHIEVOUS;
    private static final int INVENTORY_SIZE = 3;
    private static final int VIGILANCE_RANGE = 5;

    private int itemHolding;
    public PhantasmWisp() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, initialiseInventory(), INITIAL_EMOTION, new TreeMap<>());
        itemHolding = 0;
        addNewState(Emotion.MISCHIEVOUS, new LureState(this));
        addNewState(Emotion.CAUTION, new ShadowState(this));
        addNewState(Emotion.CURIOUS, new TinkerState(this));
        addNewState(Emotion.FEARFUL, new FleeState(this));
    }

    private static Inventory initialiseInventory() {
        return new ItemLimitedInventory(INVENTORY_SIZE);
    }

    @Override
    public int getVigilanceRange() {
        return VIGILANCE_RANGE;
    }

    @Override
    public boolean isInventoryFull() {
        return itemHolding >= INVENTORY_SIZE;
    }

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
