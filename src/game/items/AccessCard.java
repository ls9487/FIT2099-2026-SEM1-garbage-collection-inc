package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;

/**
 * Abstract base for the Company's plastic keys.
 * <p>
 * Each concrete subclass encodes its own tier: price, weight, display symbol,
 * clearance level, and the strange thing that happens to the buyer when the
 * card is purchased. Pushing the per-tier behaviour into subclasses means
 * adding a new clearance tier later is just another concrete class -- the
 * base no longer has to be updated, and no central {@code switch} statement
 * has to be maintained.
 * <p>
 * REQ2 doors will read {@link #getClearanceLevel()} when deciding whether
 * this card is allowed to open them, so that getter must stay stable across
 * subclasses.
 *
 * @author esoo0013
 */
public abstract class AccessCard extends EclipseItem implements Buyable {

    /** What the SuperComputer charges for this card. Set once by the subclass. */
    private final int buyPrice;

    /**
     * Constructor invoked by every concrete card subclass.
     * The clearance tier is stored as the {@link ItemStatistics#CLEARANCE_LEVEL}
     * statistic so that REQ2 door logic can read it without needing the old enum.
     *
     * @param name           the card's display name (e.g. {@code "Access Card (Level 1)"})
     * @param displayChar    map symbol shown when the card is on the ground
     * @param weight         unit weight (counts toward the worker's 50-unit cap)
     * @param buyPrice          credit cost charged by the SuperComputer
     * @param clearanceLevel integer tier (1 = Aluminium, 2 = Iron, 3 = Titanium)
     * @author esoo0013
     */
    public AccessCard(String name, char displayChar, int weight, int buyPrice, int clearanceLevel) {
        super(name, displayChar, weight);
        this.buyPrice = buyPrice;
        this.addNewStatistic(ItemStatistics.CLEARANCE_LEVEL, new BaseStatistic(clearanceLevel));
        this.enableAbility(ItemAbilities.UNLOCKER);
    }

    /**
     * Returns the integer clearance tier stored as an {@link ItemStatistics#CLEARANCE_LEVEL}
     * statistic. REQ2 door logic calls this to decide whether the card is
     * allowed to open a given door.
     *
     * @return the card's clearance tier (e.g. 1, 2, 3)
     * @author esoo0013
     */
    public int getClearanceLevel() {
        return getStatistic(ItemStatistics.CLEARANCE_LEVEL);
    }

    /**
     * Reports the credit cost. The price is fixed per subclass and does not
     * vary by buyer.
     *
     * @return the credit cost
     * @author esoo0013
     */
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

}
