package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
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
     * The radiation kicks in immediately on purchase -> Picks a random item
     * from the buyer's existing inventory and permanently deletes it.
     * The box itself is added AFTER this runs (BuyAction's responsibility),
     * so it CANNOT accidentally erase itself.
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        List<Item> existing = buyer.getInventory().getItems();
        if (existing.isEmpty()) {
            return "The radiation finds nothing to erase.";
        }
        Item victim = existing.get(random.nextInt(existing.size()));
        buyer.getInventory().remove(victim);
        return "The radiation erases " + victim + " from the inventory.";
    }

}
