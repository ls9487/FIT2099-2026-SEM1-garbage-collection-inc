package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * QuotaManager tracks the company quota cycle for the SuperComputer.
 * It manages company credits, quota targets, turn countdown, and rank progression.
 * A single instance is created in EclipseNebula and shared across all SuperComputers.
 *
 * @author eche0116
 */
public class QuotaManager {

    private static final int BASE_QUOTA = 100;
    private static final int BASE_TURNS = 200;
    private static final double QUOTA_INCREASE = 0.05;
    private static final double TURNS_INCREASE = 0.10;
    private static final int STARTING_RANK = 1;
    private static final int DEFAULT_CREDITS = 0;

    private int companyCredits;
    private int quota;
    private int turnsRemaining;
    private int rank;
    private boolean deadlinePassed = false;
    private int currentCycleDeadline = BASE_TURNS;

    private final Display display = new Display();

    /**
     * Constructs a QuotaManager starting at Rank 1 with base quota and turn limit.
     */
    public QuotaManager() {
        this.companyCredits = DEFAULT_CREDITS;
        this.quota = BASE_QUOTA;
        this.turnsRemaining = BASE_TURNS;
        this.rank = STARTING_RANK;
    }

    /**
     * Adds company credits toward the current quota.
     * Called by DepositAction when a worker deposits a resource.
     *
     * @param amount The number of company credits to add.
     */
    public void addCompanyCredits(int amount) {
        this.companyCredits += amount;
        display.println(String.format("[Quota] %d company credits deposited. Total: %d / %d",
                amount, companyCredits, quota));
    }

    /**
     * Each game turn tickTracker is called once. Decrements the turn counter and
     * checks if the deadline has been reached.
     *
     */
    public void tickTracker(Location superComputerLocation) {
        turnsRemaining--;
        onEveryTick(superComputerLocation);

        display.println(getStatus()); // print HUD here instead
    }

    /**
     * Checks if deadline has past
     * @return a boolean if deadline has past
     *
     */
    public boolean isPastDeadline() {
        return turnsRemaining <= 0;
    }

    /**
     * Called when the turn countdown reaches zero.
     * If quota is met, advances the cycle (next round). Otherwise fires adjacent workers.
     *
     */
    private void onEveryTick(Location superComputerLocation) {
        if (deadlinePassed) return; // added this guard to prevent the checking when deadline has past

        if (isQuotaMet()) { // checks if quota is met, if it is then straight away goes next cycle.
            display.println(String.format(
                    "[Quota] Quota met! Rank %d complete. Advancing cycle...", rank));
            advanceCycle();
        } else if (isPastDeadline()) {
            deadlinePassed = true;
            display.println(String.format(
                    "[Quota] WARNING DEADLINE REACHED. Quota not met (%d / %d). " +
                            "The SuperComputer fires adjacent workers!", companyCredits, quota));

            fireAdjacentWorkers(superComputerLocation);
        }
    }

    /**
     * Checks whether the accumulated company credits meet or exceed the quota.
     *
     * @return true if quota is met.
     */
    public boolean isQuotaMet() {
        return companyCredits >= quota;
    }

    /**
     * Increases quota by 5% and turn limit by 10% (both rounded up),
     * resets company credits, and increments rank.
     */
    private void advanceCycle() {
        quota = (int) Math.ceil(quota * (1 + QUOTA_INCREASE));
        currentCycleDeadline = (int) Math.ceil(currentCycleDeadline * (1 + TURNS_INCREASE));
        turnsRemaining = currentCycleDeadline;
        companyCredits = DEFAULT_CREDITS;
        rank++;
        display.println(String.format(
                "[Quota] New cycle started. Rank: %d | New Quota: %d | Turns: %d",
                rank, quota, turnsRemaining));
    }

    /**
     * Scans all maps for workers standing adjacent to a SuperComputer tile
     * and makes them unconscious (fired).
     * Workers avoiding the SuperComputer are unaffected.
     *
     */
    private void fireAdjacentWorkers(Location superComputerLocation) {
        for (Exit exit : superComputerLocation.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                Actor worker = adjacent.getActor();
                display.println(String.format(
                        "[Quota] %s is fired by the SuperComputer!", worker));
                worker.unconscious(adjacent.map());
            }
        }
    }

    /**
     * Returns a HUD string showing current quota progress and turns remaining.
     * Displayed to the player each turn via ContractedWorker.playTurn().
     *
     * @return Display for rank, company creds, and turns left
     */
    public String getStatus() {
        return String.format("[Rank %d | Company Credits: %d / %d | Turns left: %d]",
                rank, companyCredits, quota, turnsRemaining);
    }

}