package game.vehicles;

/**
 * Portable rideable upgrade that grants VehicleAbilities.BULLDOZE while mounted,
 * allowing the rider to push bulldozeable actors backward and turn walls into sand.
 *
 * @author lyan0121
 * @version 1.0
 */
public class BulldozerPlough extends RideableUpgrade {
    private static final String NAME = "Bulldozer Plough";
    private static final char DISPLAY_CHAR = '◛';
    private static final int WEIGHT = 6;

    /**
     * Creates a bulldozer plough upgrade with bulldoze ability registered.
     */
    public BulldozerPlough() {
        super(NAME, DISPLAY_CHAR, WEIGHT);
        enableAbility(VehicleAbilities.BULLDOZE);
    }
}
