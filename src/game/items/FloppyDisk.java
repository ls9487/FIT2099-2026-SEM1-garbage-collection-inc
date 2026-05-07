package game.items;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.EclipseActor;
import java.util.Random;

/**
 * FloppyDisk is a class representing a floppy disk.
 * Ancient technology. Doesn't really have a purpose for now.
 *
 * @author echu0057
 */
public class FloppyDisk extends EclipseItem implements Sellable {

    /**
     * Constructor for the FloppyDisk class.
     * Has a weight of 1 unit.
     */
    public FloppyDisk() {
        super("Floppy Disk", '⊟', 1);
    }

    private final Random random = new Random();

    /**
     * Sells for 1 credit. Although, the SuperComputer MIGHT glitch.
     * @author esoo0013
     */
    @Override
    public int sellPrice(Actor seller) {
        return 1;
    }

    /**
     * 50% chance the SuperComputer glitches AFTER paying out and yoinks
     * 50 credits straight out of the wallet. Worker has been warned.
     * @author esoo0013
     */
    @Override
    public String soldBy(Actor seller, GameMap map) {
        if (random.nextDouble() < 0.50) {
            seller.asCapability(EclipseActor.class).ifPresent(a -> a.getWallet().subtract(50));
            return "The terminal glitches and pockets 50 credits.";
        }
        return "The disk is sold without incident.";
    }
}
