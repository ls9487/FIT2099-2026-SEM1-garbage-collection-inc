package game.actors;

import edu.monash.fit2099.engine.items.Inventory;
import game.inventories.ItemLimitedInventory;
import game.states.*;

import java.util.TreeMap;

public class Muckraker extends StatefulCreature {
    private static final String NAME = "Muckraker";
    private static final char DISPLAY_CHAR = 'Д';
    private static final int HIT_POINTS = 50;
    private static final Emotion INITIAL_EMOTION = Emotion.CURIOUS;
    private static final int INVENTORY_SIZE = 3;
    private static final int VIGILANCE_RANGE = 3;

    private int numberOfItemHolding;
    public Muckraker() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, initialiseInventory(), INITIAL_EMOTION, new TreeMap<>());
        numberOfItemHolding = 0;
        addNewState(Emotion.CURIOUS, new ScavengeState(this));
        addNewState(Emotion.GREEDY, new HoardingState(this));
        addNewState(Emotion.FEARFUL, new PanicState(this));
        addNewState(Emotion.ANGRY, new DefensiveState(this));
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
        return numberOfItemHolding >= INVENTORY_SIZE;
    }
}
