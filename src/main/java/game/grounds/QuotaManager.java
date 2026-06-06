package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;

/**
 * QuotaManager tracks the company quota cycle for the SuperComputer.
 * It manages company credits, quota targets, turn countdown, and rank progression.
 * Don't reach the deadline!
 * Note that this doesn't inherit GameEntity so statistics are off-limits.
 *
 * @author eche0116
 */
public class QuotaManager {

    private static final int BASE_QUOTA = 100;
    private static final int BASE_TURNS = 200;
    private static final double QUOTA_INCREASE_FACTOR = 1.05;
    private static final double TURNS_INCREASE_FACTOR = 1.1;
    private static final int STARTING_RANK = 1;
    private static final int DEFAULT_CREDITS = 0;

    private int companyCredits;
    private int quota;
    private int turnsRemaining;
    private int turnsLimit;
    private int rank;

    private final Display display = new Display();

    /**
     * Constructs a QuotaManager starting at Rank 1 with base quota and turn limit.
     */
    public QuotaManager() {
        this.companyCredits = DEFAULT_CREDITS;
        this.quota = BASE_QUOTA;
        this.turnsLimit = BASE_TURNS;
        this.turnsRemaining = BASE_TURNS;
        this.rank = STARTING_RANK;
    }

    /**
     * Attempts to add company credits toward the current quota.
     * Called by DepositAction when a worker deposits a resource.
     * Tries to rank up as soon as when the quota is met (considering multiplayer).
     * Not permitted when the deadline has passed.
     *
     * @param amount The number of company credits to add.
     */
    public void addCompanyCredits(int amount) {
        if (isPastDeadline()) {
            // Since the deadline has passed, even if a worker (somehow) forces a deposit,
            // the item will be taken away but quota remains as is.
            display.println("[Quota] Thanks for the item deposited, but as the deadline" +
                    " has passed, the quota will not be updated.");
            return;
        }

        this.companyCredits += amount;
        display.println(String.format("[Quota] %d company credits deposited. Quota: %d / %d",
                amount, companyCredits, quota));
        // Then, it'll try to update the rank. It'll do this immediately after a deposit so
        // multiple players can deposit in the same turn without major credit wastage.
        if (isQuotaMet()) {
            rankUp();
        }

    }

    /**
     * Called (every turn) by whoever possesses this QuotaManager. Decrements the turn counter and
     * checks if the deadline has been reached.
     * @param superComputerLocation The location of the supercomputer.
     */
    public void updateTurn(Location superComputerLocation) {
        // Tick down the turns remaining. Don't worry if it goes negative (non-positive means
        // the deadline has passed).
        turnsRemaining--;

        // Do the firing only if the turns remaining just hit the deadline (exactly 0).
        if (isOnDeadline()) {
            fireAdjacentWorkers(superComputerLocation);
        }

        display.println(getStatus()); // print HUD here instead
    }

    /**
     * Checks whether the accumulated company credits meet or exceed the quota.
     * @return true if quota is met, false otherwise.
     */
    public boolean isQuotaMet() {
        return companyCredits >= quota;
    }

    /**
     * Checks if deadline has past.
     * @return a boolean if deadline has past
     */
    public boolean isPastDeadline() {
        return turnsRemaining <= 0;
    }

    /**
     * Checks if deadline has *just* past. Which means it's time for some firing!
     * @return a boolean if deadline has just past
     *
     */
    public boolean isOnDeadline() {
        return turnsRemaining == 0;
    }

    /**
     * Called when quota is reached, causing a rank increase. Excess quota is not carried over.
     * Increases quota by 5% and turn limit by 10% (both rounded up),
     * resets company credits, and increments rank.
     * To be used internally within this class only.
     */
    private void rankUp() {
        // Increase the max quota by 5%, and turns limit by 10%, rounded up.
        quota = (int) Math.ceil(quota * QUOTA_INCREASE_FACTOR);
        turnsLimit = (int) Math.ceil(turnsLimit * TURNS_INCREASE_FACTOR);
        // Reset current credits to 0 and the turns remaining to what the limit is.
        companyCredits = DEFAULT_CREDITS;
        turnsRemaining = turnsLimit;
        // Increment rank by 1.
        rank++;
        // Lastly, display a message about it.
        display.println(String.format(
                "[Quota] Rank up! Good job. Rank: %d | New Quota: %d | New turn limit: %d",
                rank, quota, turnsRemaining));
    }

    /**
     * Scans the adjacent tiles (of a supercomputer) for workers standing around
     * and makes them unconscious (fired).
     * This only happens exactly on the deadline and not again after.
     * @param superComputerLocation The location to do firing around.
     */
    private void fireAdjacentWorkers(Location superComputerLocation) {
        // Print a warning message.
        display.println(String.format(
                "[Quota] Deadline reached. Quota unmet (%d / %d). " +
                        "Prepare for imminent annihilation.", companyCredits, quota));

        for (Exit exit : superComputerLocation.getExits()) {
            Location adjacent = exit.getDestination();
            // Only workers (or any actors with the PLAYER ability) are susceptible to firing.
            if (adjacent.containsAnActor() &&
                    adjacent.getActor().hasAbility(ActorAbilities.PLAYER)) {
                Actor worker = adjacent.getActor();
                display.println(String.format(
                        "[Quota] %s has been fired!", worker));
                worker.unconscious(adjacent.map());
            }
        }
    }

    /**
     * Returns a string showing current quota progress and turns remaining,
     * or if the deadline passed.
     * @return A string status of the quota manager.
     */
    public String getStatus() {
        if (!isPastDeadline()) {
            return String.format("[Quota Status] [Rank %d | Company Credits: %d / %d | Turns left: %d]",
                    rank, companyCredits, quota, turnsRemaining);
        } else {
            return String.format("[Quota Status] [Rank %d | DEADLINE PASSED]", rank);
        }
    }

}