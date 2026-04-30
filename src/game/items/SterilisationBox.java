package game.items;

/**
 * A class representing a sterilisation box.
 * Has the ability to somehow make pathogen or toxin-riddled things safe to consume.
 * The microbes come back quickly though so you'd better eat those things immediately.
 * (Safe consumption only occurs if the actor eating it can sterilise it on the spot.)
 *
 * @author echu0057
 */
public class SterilisationBox extends EclipseItem {

    /**
     * Constructor for the SterilisationBox class.
     * Has a weight of 7 units, and possesses the STERILISER capability.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣', 7);
        this.enableAbility(ItemAbilities.STERILISER);
    }

}
