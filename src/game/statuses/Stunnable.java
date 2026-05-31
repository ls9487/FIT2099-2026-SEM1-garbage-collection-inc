package game.statuses;

/**
 * Stunnable is an interface for entities that can be stunned.
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Stunnable {

    /**
     * Handles the logic for being stunned, such as taking damage.
     *
     * @param damage hit points lost while stunned
     */
    public void stun(int damage);
}
