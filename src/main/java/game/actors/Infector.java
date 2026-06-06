package game.actors;

import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Infector is an interface representing something that can cause a parasitic infection.
 *
 * @author echu0057
 */
public interface Infector {

    /**
     * This method defines what happens to the infector when it infected something.
     * Note that it's not responsible for creating the infection effect.
     * @param map The map where the infection took place.
     * @return A String description of what happened to the infector.
     */
    public String infectingSelfEffect(GameMap map);

}
