package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import java.util.Random;

/**
 * CrtMonitor is a class representing a CRT monitor.
 * Also ancient technology. Doesn't really have a purpose for now beyond testing weight limits.
 *
 * @author echu0057
 */
public class CrtMonitor extends EclipseItem implements Sellable {
    private static final int FIRE_DURATION = 5;
    private final Random random = new Random();

    /**
     * Constructor for the CrtMonitor class.
     * Has a weight of 30 units, which is heavy!
     */
    public CrtMonitor() {
        super("CRT Monitor", '◙', 30);
    }

    /**
     * Twenty-five credits, fixed. Heaviest single-payout item in the game by weight ratio.
     * @author esoo0013
     */
    @Override
    public int sellPrice(Actor seller) {
        return 25;
    }

    /**
     * Selling unconditionally will heal 5 HP from the relief of finally putting
     * down 30 units of dead weight. Then there's a 20% chance the ancient
     * hardware shorts out: 2 damage to the seller, fire on every neighbour.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        seller.heal(5);
        StringBuilder msg = new StringBuilder("Offloading the monitor heals " + seller + " for 5 HP.");
        if (random.nextDouble() < 0.20) {
            seller.hurt(2);
            for (var adjacent : map.locationOf(seller).getNearbyLocations(1)) {
                adjacent.addItem(new Fire(FIRE_DURATION));
            }
            msg.append(" The terminal shorts out, dealing 2 damage and igniting the area.");
        }
        return msg.toString();
    }

}
