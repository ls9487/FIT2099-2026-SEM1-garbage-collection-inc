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

    /**
     * Constructor for the SterilisationBox class.
     * Has a weight of 7 units, and possesses the STERILISER capability.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣', 7);
        this.enableAbility(ItemAbilities.STERILISER);
    }

    /**
     * Mid-tier purchase. 750 credits.
     * @author esoo0013
     */
    @Override
    public int buyPrice(Actor buyer) {
        return 750;
    }

    /**
     * The radiation kicks in immediately on purchase. The box is added to
     * the inventory first, and then a random item is sampled from the full
     * inventory and permanently erased.
     *
     * The box is eligible to be its OWN victim (radiation spread to itself)
     * , in which case the buyer pays 750
     * credits for nothing.
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        StringBuilder result = new StringBuilder();
        result.append(buyer).append(" buys ").append(this)
                .append(" for 750 credits.");

        // Box is added FIRST so it is eligible to be the victim, per Ed clarification.
        buyer.getInventory().add(this);

        List<Item> inventory = new ArrayList<>(buyer.getInventory().getItems());
        Item victim = inventory.get(random.nextInt(inventory.size()));
        buyer.getInventory().remove(victim);

        if (victim == this) {
            result.append(" The radiation erases the box itself.");
        } else {
            result.append(" Radiation erases ").append(victim).append(".");
        }

        return result.toString();
    }

}
