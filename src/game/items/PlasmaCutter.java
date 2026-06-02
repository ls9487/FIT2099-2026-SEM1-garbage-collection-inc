package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statuses.BurnStatus;
import game.statuses.Flammable;

/**
 * PlasmaCutter is a tool that allows workers to cut facility structures
 * (Aluminium Doors, Vents) and items (Alien Cubes) to harvest resources
 * for depositing toward the company quota.
 *
 * Buying it from the SuperComputer is dangerous, it ejects from the
 * delivery chute at searing temperatures, dealing 5 damage and burning
 * the buyer for 5 turns.
 * Note: This item cannot be sold back to the SuperComputer.
 *
 * @author eche0116
 */
public class PlasmaCutter extends EclipseItem implements Buyable {

    private static final int BUY_PRICE = 50;
    private static final int WEIGHT = 7;

    private static final int PURCHASE_DAMAGE = 5;
    private static final int BURN_DURATION = 5;
    private static final int BURN_INTENSITY = 1;

    /**
     * Constructs a PlasmaCutter with weight 7 and display char '>'.
     */
    public PlasmaCutter() {
        super("Plasma Cutter", '>', WEIGHT);
        this.enableAbility(ItemAbilities.CUTTER);
    }

    /**
     * Returns the buy price of the Plasma Cutter.
     *
     * @return 50 worker credits.
     */
    @Override
    public int getBuyPrice() {
        return BUY_PRICE;
    }

    /**
     * Purchase side effect: the cutter ejects from the delivery chute at
     * searing temperatures, dealing 5 damage and burning the buyer for 5 turns.
     * The item is then added to the buyer's inventory.
     *
     * @param buyer The actor buying the Plasma Cutter.
     * @param map   The map the buyer is on.
     * @return A description of the purchase and its effects.
     */
    @Override
    public String boughtBy(Actor buyer, GameMap map) {
        buyer.hurt(PURCHASE_DAMAGE);
        buyer.addStatus(new BurnStatus(BURN_DURATION, BURN_INTENSITY, (Flammable) buyer));
        buyer.getInventory().add(this);
        return String.format(
                "%s buys the Plasma Cutter for %d credits. " + "It ejects from the chute at searing temperatures! " +
                "%s takes %d damage and is burned for %d turns!",
                buyer, BUY_PRICE, buyer, PURCHASE_DAMAGE, BURN_DURATION);
    }

    /**
     * The buyer simply cannot afford it.
     *
     * @param buyer The actor who lacks credits.
     * @param map   The map the buyer is on.
     * @return A refusal message.
     */
    @Override
    public String cannotAfford(Actor buyer, GameMap map) {
        return buyer + " cannot afford the Plasma Cutter (costs " + BUY_PRICE + " credits).";
    }
}