package game.items;

import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.EclipseStatistics;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Random;

/**
 * Top-tier clearance card. Unlocks every door tier in the game.
 * <p>
 * Due to The Company's predatory pricing algorithms, there is an independent
 * 50% chance the SuperComputer will slap an extra 50-credit hidden fee onto
 * the buyer immediately after the base purchase is processed. The hidden
 * deduction is applied directly to the buyer's CREDITS statistic via
 * {@link EclipseStatistics#CREDITS}.
 *
 * @author esoo0013
 */
public class AccessCardL3 extends AccessCard {

    /** Credit cost charged by the SuperComputer. */
    private static final int PRICE = 200;

    /** Inventory weight in units. */
    private static final int WEIGHT = 3;

    /** Map display symbol for this card. */
    private static final char SYMBOL = '◐';

    /** Hidden fee charged on top of the base price. */
    private static final int HIDDEN_FEE = 50;

    /** Probability of the hidden fee triggering on a given purchase. */
    private static final double HIDDEN_FEE_CHANCE = 0.5;

    /** RNG used to roll the hidden-fee chance. */
    private static final Random random = new Random();

    /**
     * Constructor for the L3 access card.
     * @author esoo0013
     */
    public AccessCardL3() {
        super("Access Card (Level 3)", SYMBOL, WEIGHT, PRICE, 3);
    }

    /**
     * 50% chance to roll the hidden fee and pull an extra 50 credits out of
     * the buyer's account on top of the 200 already deducted by the
     * surrounding BuyAction. The card is then placed in the inventory.
     *
     * @param buyer the buyer who just paid 200 credits
     * @param map   the buyer's map
     * @return a description of the purchase and whether the hidden fee triggered
     * @author esoo0013
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        StringBuilder msg = new StringBuilder(buyer + " buys an L3 access card for "
                + getBuyPrice() + " credits.");
        // Random check if the 50 credit hidden fee will be applied (50% chance).
        if (random.nextDouble() < HIDDEN_FEE_CHANCE && buyer.hasStatistic(EclipseStatistics.CREDITS)) {
            buyer.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.DECREASE, HIDDEN_FEE);
        }
        msg.append(".. a hidden fee?! The terminal pockets an extra ")
                .append(HIDDEN_FEE).append(" credits.");
        buyer.getInventory().add(this);
        return msg.toString();
    }

    /**
     * Nothing bad happens. The buyer just can't have the item.
     * @param buyer The actor who lacks credits.
     * @param map The map the buyer is on.
     * @return A description of the failure.
     */
    @Override
    public String cannotAfford(Actor buyer, GameMap map) {
        return buyer + " cannot afford to buy the L3 access card.";
    }

}