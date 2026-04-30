package game.items;

/**
 * A class representing a small rectangular piece of plastic that holds entirely
 * too much power over your ability to walk through doors.
 * Its primary function is to beep happily when the player has clearance, and beep
 * angrily when they don't.
 * Essential for progressing the plot,
 *
 * @author Adrian Kristanto
 * @author echu0057
 */
public class AccessCard extends EclipseItem {

    /**
     * Constructor for the AccessCard class.
     * Has a weight of 1 unit, and possesses the UNLOCKER capability (i.e. can open doors).
     */
    public AccessCard() {
        super("Access Card", '▤', 1);
        this.enableAbility(ItemAbilities.UNLOCKER);
    }

}
