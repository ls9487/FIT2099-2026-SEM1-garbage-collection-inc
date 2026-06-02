package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/**
 * A class representing a solid wall. Yes, that's it.
 * Actors can't pass through it.
 *
 * @author echu0057
 */
public class Wall extends Ground {

    /**
     * Constructor for the Wall class.
     */
    public Wall() {
        super('#', "Wall");
    }

    /**
     * No going through walls!
     * @param actor The actor to check.
     * @return false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Walls will always block projectiles.
     * @return true
     */
    @Override
    public boolean blocksThrownObjects() {
        return true;
    }

}
