package game.statuses;

/**
 * Infectable is an interface for entities that can be parasitically infected.
 *
 * @author echu0057
 */
public interface Infectable {

    /**
     * This method will handle the logic for being infected, like spawning a new parasite.
     */
    public void infection();

}
