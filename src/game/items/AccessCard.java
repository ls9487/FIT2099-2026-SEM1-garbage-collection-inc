package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.EclipseActor;

import java.util.Random;

/**
 * The plastic key to every door in the facility, which consists of THREE types.
 * We use ONE class instead of three subclasses since the level basically decides
 * everything else (price, weight, symbol, and what bad thing happens
 * when you buy it). SuperComputer just calls the right factory method.
 *
 * REQ2 doors will read getClearanceLevel() when checking if this card
 * can make them them open, so that getter has to STAY stable.
 *
 * @author esoo0013
 */
public class AccessCard extends EclipseItem implements Buyable {

    // L1: starter card, no purchase nasties
    private static final int L1_PRICE = 50;
    private static final int L1_WEIGHT = 1;
    private static final char L1_SYMBOL = '▤';

    // L2: "blood sample" calibration
    private static final int L2_PRICE = 100;
    private static final int L2_WEIGHT = 2;
    private static final char L2_SYMBOL = 'α';
    private static final int L2_BLOOD_SAMPLE_DMG = 5;

    // L3: 50% chance the buy stings for an extra 50 credits
    private static final int L3_PRICE = 200;
    private static final int L3_WEIGHT = 3;
    private static final char L3_SYMBOL = '◐';
    private static final int L3_HIDDEN_FEE = 50;
    private static final double L3_HIDDEN_FEE_CHANCE = 0.5;

    private final ClearanceLevel clearanceLevel;
    private final int price;
    private final Random random;

    /**
     * Direct constructor. Usually you'd grab one of the levelN() factories
     * below instead, but this is here in case you ever want a non-standard card.
     *
     * @param name the card's display name
     * @param displayChar map symbol
     * @param weight unit weight (counts toward the 50-unit cap)
     * @param price what SuperComputer charges for it
     * @param clearanceLevel which tier of door it can open
     * @author esoo0013
     */
    public AccessCard(String name, char displayChar, int weight, int price, ClearanceLevel clearanceLevel) {
        super(name, displayChar, weight);
        this.clearanceLevel = clearanceLevel;
        this.price = price;
        this.random = new Random();
        this.enableAbility(ItemAbilities.UNLOCKER);
    }

    /**
     * Starter card. Given to the worker at the start of the game.
     * @author esoo0013
     */
    public static AccessCard levelOne() {
        return new AccessCard("Access Card (Level 1)", L1_SYMBOL, L1_WEIGHT, L1_PRICE, ClearanceLevel.LEVEL_1);
    }

    /**
     * Mid-tier card. Buying it immediately costs the worker 5 HP.
     * @author esoo0013
     */
    public static AccessCard levelTwo() {
        return new AccessCard("Access Card (Level 2)", L2_SYMBOL, L2_WEIGHT, L2_PRICE, ClearanceLevel.LEVEL_2);
    }

    /**
     * Highest-tier card. Has a chance to apply an additional hidden fee.
     * @author esoo0013
     */
    public static AccessCard levelThree() {
        return new AccessCard("Access Card (Level 3)", L3_SYMBOL, L3_WEIGHT, L3_PRICE, ClearanceLevel.LEVEL_3);
    }

    /**
     * Returns the clearance level used by REQ2 doors for unlocking checks.
     *
     * @return the card's clearance tier
     * @author esoo0013
     */
    public ClearanceLevel getClearanceLevel() {
        return clearanceLevel;
    }

    @Override
    public int buyPrice(Actor buyer) {
        return price;
    }

    /**
     * As for side effects of buying this card. Switching on the level keeps all the
     * weird per-tier behaviour in one place rather than spreading it across
     * subclasses or some external handler.
     * PS: adding a new tier later is just another enum value plus another case here
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        switch (clearanceLevel) {
            // Level 1 card. Simple starter clearance for Aluminium doors
            case LEVEL_1:
                return buyer + " buys an L1 access card.";

            case LEVEL_2:
                // Level 2 card. Better clearance, but the SuperComputer takes a blood sample on purchase
                buyer.hurt(L2_BLOOD_SAMPLE_DMG);
                return "The terminal jabs " + buyer + " for a blood sample (-" + L2_BLOOD_SAMPLE_DMG + " HP).";

            case LEVEL_3:
                // Level 3 card. Highest clearance in the game, with a chance of hidden extra fees
                if (random.nextDouble() < L3_HIDDEN_FEE_CHANCE) {
                    deductHiddenFee(buyer);
                    return "Hidden fee! The terminal pockets an extra " + L3_HIDDEN_FEE + " credits.";
                }
                return buyer + " buys an L3 access card.";

            default:
                return buyer + " buys an access card.";
        }
    }

    /**
     * Applies the hidden Level 3 processing fee
     * The extra 50 credits are deducted directly from the buyer's
     * credit balance if they are an EclipseActor.
     *
     * @author esoo0013
     */
    private void deductHiddenFee(Actor buyer) {
        buyer.asCapability(EclipseActor.class)
                .ifPresent(a -> a.deductCredits(L3_HIDDEN_FEE));
    }
}