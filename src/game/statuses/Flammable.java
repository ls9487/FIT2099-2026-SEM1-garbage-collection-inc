package game.statuses;

/**
 * Flammable is an interface for entities that can be burned.
 * The mars example was used as a reference.
 *
 * @author echu0057
 */
public interface Flammable {

    /**
     * This method will handle the logic for being burned, like taking damage.
     * @param damage The damage dealt via burn.
     */
    public void burn(int damage);

}
