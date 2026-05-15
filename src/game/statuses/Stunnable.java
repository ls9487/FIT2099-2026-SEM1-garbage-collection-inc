package game.statuses;

/**
 * Flammable is an interface for entities that can be burned.
 * The mars example was used as a reference.
 *
 * @author lyan0121
 * @version 1.0
 */
public interface Stunnable {

    /**
     * This method will handle the logic for being burned, like taking damage.
     */
    public void stun(int damage);
}
