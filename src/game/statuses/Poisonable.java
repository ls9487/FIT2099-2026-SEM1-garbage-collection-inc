package game.statuses;

/**
 * Poisonable is an interface for entities that can be poisoned.
 * The mars example was used as a reference.
 *
 * @author echu0057
 */
public interface Poisonable {

    /**
     * This method will handle the logic for being poisoned, like taking damage.
     * @param damage The damage dealt via poison.
     */
    public void poison(int damage);

}
