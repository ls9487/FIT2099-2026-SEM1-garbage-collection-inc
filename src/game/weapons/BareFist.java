package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * BareFist is a class representing bare fists. As an intrinsic weapon.
 * Taken from the forest example and modified.
 *
 * @author Adrian Kristanto
 * @author echu0057
 */
public class BareFist extends IntrinsicWeapon {

    /**
     * Constructor for the BareFist class.
     * Allows a custom value for damage and hit rate.
     * @param damage The damage dealt by the weapon.
     * @param hitRate The hit rate of the weapon.
     */
    public BareFist(int damage, int hitRate) {
        super(damage, "punches", hitRate, "bare fists");
    }

}
