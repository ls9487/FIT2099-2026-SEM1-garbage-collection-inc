package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class representing a sterilisation box.
 * Has the ability to somehow make pathogen or toxin-riddled things safe to consume.
 * The microbes come back quickly though so you'd better eat those things immediately.
 * (Safe consumption only occurs if the actor eating it can sterilise it on the spot.)
 *
 * @author echu0057
 */
public class SterilisationBox extends EclipseItem implements Buyable {

    private final Random random = new Random();

    /** Credit cost charged by the SuperComputer. */
    private static final int BUY_PRICE = 750;

    /** Inventory weight in units. */
    private static final int WEIGHT = 7;

    /** Map display symbol for this box. */
    private static final char SYMBOL = '▣';

    /**
     * Constructor for the SterilisationBox class.
     * Has a weight of 7 units, and possesses the STERILISER capability.
     */
    public SterilisationBox() {
        super("Sterilisation Box", SYMBOL, WEIGHT);
        this.enableAbility(ItemAbilities.STERILISER);
    }

    /**
     * Mid-tier purchase. 750 credits.
     * @author esoo0013
     */
    @Override
    public int getBuyPrice() {
        return BUY_PRICE;
    }

    /**
     * The radiation kicks in immediately on purchase. The box is added to
     * the inventory first, and then a random item is sampled from the full
     * inventory and permanently erased.
     * The box is eligible to be its OWN victim (radiation spread to itself),
     * in which case the buyer pays 750 credits for nothing.
     * @param buyer The actor doing the buying.
     * @param map The map the buyer is on.
     * @return A full description describing the purchase and its effects.
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        StringBuilder result = new StringBuilder(buyer + " buys a sterilisation box for " +
                getBuyPrice() + " credits.");

        // Box is added FIRST so it is eligible to be the victim, per Ed clarification.
        buyer.getInventory().add(this);

        // Get the worker's inventory, pick a random item from it and remove it.
        List<Item> inventory = new ArrayList<>(buyer.getInventory().getItems());
        Item victim = inventory.get(random.nextInt(inventory.size()));
        buyer.getInventory().remove(victim);
        // This is just to have a custom message if the box itself vanishes (truly unfortunate).
        if (victim == this) {
            result.append(" The radiation erases the box that was just bought!.");
        } else {
            result.append(" Radiation erases ").append(victim).append(".");
        }

        return result.toString();
    }

    /**
     * Nothing bad happens. The buyer just can't have the item.
     * @param buyer The actor who lacks credits.
     * @param map The map the buyer is on.
     * @return A description of the failure.
     */
    @Override
    public String cannotAfford(Actor buyer, GameMap map) {
        return buyer + " cannot afford to buy the (overpriced) sterilisation box.";
    }

}
