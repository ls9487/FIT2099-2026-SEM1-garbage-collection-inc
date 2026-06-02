package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Random;

/**
 * Aluminium Scrap is produced by cutting an Aluminium Door with a Plasma Cutter.
 * It can only be deposited at the SuperComputer (not sold).
 *
 * @author eche0116
 */
public class AluminiumScrap extends EclipseItem implements Depositable {

    private static final int WEIGHT = 2;
    private static final int DEPOSIT_VALUE = 50;
    private static final int CUT_DAMAGE = 5;
    private static final double CUT_CHANCE = 0.20;

    private final Random random = new Random();

    /**
     * Constructs Aluminium Scrap with weight 2 and display char '%'.
     */
    public AluminiumScrap() {
        super("Aluminium Scrap", '%', WEIGHT);
    }

    /**
     * Returns the company credit value of depositing this scrap.
     *
     * @return 50 company credits.
     */
    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    /**
     * Depositing side effect: shoving jagged metal into the deposit chute
     * has a 20% chance of cutting the worker for 5 damage.
     * Removes this item from the actor's inventory.
     *
     * @param actor The actor depositing this item.
     * @param map   The map the actor is on.
     * @return A description of the deposit and any side effects.
     */
    @Override
    public String depositedBy(Actor actor, GameMap map) {
        StringBuilder result = new StringBuilder(
                String.format("%s deposits Aluminium Scrap for %d company credits.",
                        actor, DEPOSIT_VALUE));
        if (random.nextDouble() < CUT_CHANCE) {
            actor.hurt(CUT_DAMAGE);
            result.append(String.format(
                    " The jagged metal slices %s's hand! %d damage taken.",
                    actor, CUT_DAMAGE));
        }
        actor.getInventory().remove(this);
        return result.toString();
    }
}