package game.actors;

import edu.monash.fit2099.engine.items.Inventory;
import game.inventories.ItemLimitedInventory;
import game.states.*;

import java.util.TreeMap;

/**
 * Scavenging creature that hoards items and panics when workers are nearby.
 *
 * @author lyan0121
 * @version 1.0
 */
public class Muckraker extends StatefulCreature {
    private static final String NAME = "Muckraker";
    private static final char DISPLAY_CHAR = 'Д';
    private static final int HIT_POINTS = 50;
    private static final Emotion INITIAL_EMOTION = Emotion.CURIOUS;
    private static final int INVENTORY_SIZE = 3;
    private static final int VIGILANCE_RANGE = 3;


    /**
     * Creates a Muckraker with default stats and emotion states.
     * */
    public Muckraker() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, initialiseInventory(), INITIAL_EMOTION, new TreeMap<>());
        addNewState(Emotion.CURIOUS, new ScavengeState(this));
        addNewState(Emotion.GREEDY, new HoardingState(this));
        addNewState(Emotion.FEARFUL, new PanicState(this));
        addNewState(Emotion.ANGRY, new DefensiveState(this));
    }

    /**
     * initialise Muckraker inventory
     * @return the inventory of Muckraker which is an ItemLimitedInventory
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
}
