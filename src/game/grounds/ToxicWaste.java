package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Toxic Waste is a permanently corrupted ground tile.
 * Any actor standing on it takes 1 damage per turn.
 * Created when an Alien Cube is used, corrupting adjacent tiles.
 *
 * @author eche0116
 */
public class ToxicWaste extends Ground {
    private static final int DAMAGE_PER_TURN = 1;

    public ToxicWaste() {
        super('≈', "Toxic Waste");
    }

    /**
     * Called once per turn. Damages any actor standing on this tile.
     * @param location The location of this Toxic Waste tile.
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.hurt(DAMAGE_PER_TURN);
        }
    }
}