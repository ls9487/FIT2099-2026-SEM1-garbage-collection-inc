package game.vehicles;

/**
 * Abilities granted by rideable vehicles and rideable upgrades while mounted.
 *
 * @author lyan0121
 * @version 1.0
 */
public enum VehicleAbilities {
    /**
     * Allows entering holes and vents, immunity to toxic waste damage, and hover blast when combined with extra energy.
     */
    HOVER,
    /**
     * Allows crushing doors and walls into dirt.
     */
    CRUSH,
    /**
     * Allows purifying toxic waste into dirt.
     */
    PURIFY,
    /**
     * Allows bulldozing walls into sand and pushing bulldozeable actors backward.
     */
    BULLDOZE,
    /**
     * Unlocks high-energy special actions such as hover blast and warp teleportation.
     */
    EXTRA_ENERGY
}
