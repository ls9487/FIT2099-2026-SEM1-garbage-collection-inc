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

    private final List<Location> superComputerLocations;
    private int companyCredits;
    private int quota;
    private int turnsRemaining;
    private int rank;

    private final Display display = new Display();

    /**
     * Constructs a QuotaManager starting at Rank 1 with base quota and turn limit.
     */
    public QuotaManager() {
        this.companyCredits = DEFAULT_CREDITS;
        this.quota = BASE_QUOTA;
        this.turnsRemaining = BASE_TURNS;
        this.rank = STARTING_RANK;
        this.superComputerLocations = new ArrayList<>();
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
     * Each game turn tick is called once. Decrements the turn counter and
     * checks if the deadline has been reached.
     *
     */
    public void tick() {
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            onDeadline();
        }
    }

    /**
     * Called when the turn countdown reaches zero.
     * If quota is met, advances the cycle (next round). Otherwise fires adjacent workers.
     *
     */
    private void onDeadline() {
        if (isQuotaMet()) {
            display.println(String.format(
                    "[Quota] Quota met! Rank %d complete. Advancing cycle...", rank));
            advanceCycle();
        } else {
            display.println(String.format(
                    "[Quota] WARNING DEADLINE REACHED. Quota not met (%d / %d). " +
                            "The SuperComputer fires adjacent workers!", companyCredits, quota));
            fireAdjacentWorkers();
            // Reset turns so the game continues (workers avoiding SC can still perish naturally)
            turnsRemaining = BASE_TURNS;
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
        turnsRemaining = (int) Math.ceil(BASE_TURNS * Math.pow(1 + TURNS_INCREASE, rank));
        companyCredits = 0;
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
    private void fireAdjacentWorkers() {
        for (Location location : superComputerLocations) {
            for (Exit exit : location.getExits()) {
                Location adjacent = exit.getDestination();
                if (adjacent.containsAnActor()) {
                    Actor worker = adjacent.getActor();
                    display.println(String.format(
                            "[Quota] %s is fired by the SuperComputer!", worker));
                    worker.unconscious(adjacent.map());
                }
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

    public void registerSuperComputerLocation(Location location) {
        superComputerLocations.add(location);
    }
}