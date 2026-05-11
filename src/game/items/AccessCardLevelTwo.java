package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Mid-tier clearance card. Unlocks Aluminium and Iron doors (REQ2).
 * <p>
 * To "calibrate" the card to its new owner the SuperComputer forcefully
 * extracts a blood sample from the buyer at the exact moment of purchase,
 * dealing exactly 5 HP of damage.
 *
 * @author esoo0013
 */
public class AccessCardLevelTwo extends AccessCard {

    /** Credit cost charged by the SuperComputer. */
    private static final int PRICE = 100;

    /** Inventory weight in units. */
    private static final int WEIGHT = 2;

    /** Map display symbol for this card. */
    private static final char SYMBOL = 'α';

    /** Flat HP cost of the "blood sample" calibration. */
    private static final int BLOOD_SAMPLE_DMG = 5;

    /**
     * Constructor for the L2 access card.
     * @author esoo0013
     */
    public AccessCardLevelTwo() {
        super("Access Card (Level 2)", SYMBOL, WEIGHT, PRICE, ClearanceLevel.LEVEL_2);
    }

    /**
     * Applies the 5 HP calibration damage to the buyer immediately on
     * purchase. The damage is delivered via {@code Actor.hurt} so it
     * interacts correctly with the engine's HEALTH statistic.
     *
     * @param buyer the buyer who just paid 100 credits
     * @param map   the buyer's map
     * @return a description of the blood-sample calibration
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        buyer.hurt(BLOOD_SAMPLE_DMG);
        return "The terminal jabs " + buyer + " for a blood sample (-" + BLOOD_SAMPLE_DMG + " HP).";
    }
}
