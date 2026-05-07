package game.inventories;

/**
 * The worker's stash of company credits. Hard cap of 1000 because the company
 * doesn't trust expendable workers with serious money. Anything over the cap
 * just spills into the void.
 *
 * @author esoo0013
 */
public class Wallet {

    /** The corporate-mandated maximum. */
    public static final int MAX_CREDITS = 1000;

    private int credits;

    /**
     * Constructor. Starts empty because expendable workers don't get signing bonuses.
     */
    public Wallet() {
        this.credits = 0;
    }

    /**
     * @return Current balance.
     */
    public int getBalance() {
        return credits;
    }

    /**
     * Add credits. Negative amounts are ignored. Excess over MAX_CREDITS is dropped.
     * @param amount Credits to add.
     */
    public void add(int amount) {
        if (amount <= 0) return;
        credits = Math.min(MAX_CREDITS, credits + amount);
    }

    /**
     * Remove credits. Negative amounts are ignored. Will NOT go below 0.
     * @param amount Credits to remove.
     */
    public void subtract(int amount) {
        if (amount <= 0) return;
        credits = Math.max(0, credits - amount);
    }

    /**
     * @param amount The price to check AGAINST.
     * @return True if balance is at LEAST the given amount.
     */
    public boolean canAfford(int amount) {
        return credits >= amount;
    }
}