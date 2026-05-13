package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Starter clearance card. Spawned on the armoured ship at the start of the
 * game and unlocks Aluminium doors (REQ2). Buying a fresh one from the
 * SuperComputer has no purchase side-effect.
 *
 * @author esoo0013
 */
public class AccessCardL1 extends AccessCard {

    /** Credit cost charged by the SuperComputer. */
    private static final int PRICE = 50;

    /** Inventory weight in units. */
    private static final int WEIGHT = 1;

    /** Map display symbol for this card. */
    private static final char SYMBOL = '▤';

    /**
     * Constructor for the basic L1 access card.
     * @author esoo0013
     */
    public AccessCardL1() {
        super("Access Card (Level 1)", SYMBOL, WEIGHT, PRICE, 1);
    }

    /**
     * L1 purchase has no side effect; the card simply goes into the inventory
     * after the cost has been deducted.
     *
     * @param buyer the buyer
     * @param map   the buyer's map
     * @return a description of the (uneventful) purchase
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        buyer.getInventory().add(this);
        return buyer + " buys an L1 access card for " + PRICE + " credits.";
    }
}
