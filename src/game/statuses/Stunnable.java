package game.statuses;

/**
 * Stunnable is an interface for entities that can be stunned.
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Stunnable {

    /**
     * This method will handle the logic for being stunned, like taking damage.
     */
    public void stun(int damage);
}
