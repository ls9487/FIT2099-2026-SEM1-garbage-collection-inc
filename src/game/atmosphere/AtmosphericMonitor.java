package game.atmosphere;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Atmospheric monitor ground used for the REQ5 toxic atmosphere feature.
 *
 * This class represents the fixed monitor station placed on the map. Instead
 * of behaving like a living actor, it stays as environmental infrastructure and
 * triggers atmospheric scans through its attached
 * {@link EnvironmentalMonitorBehaviour}. It also implements
 * {@link AtmosphericAnchor} so other REQ5 systems can use it as the centre
 * point for hotspot corruption and pollution-based spawning.
 *
 * @author esoo0013
 */
public class AtmosphericMonitor extends Ground implements AtmosphericAnchor {

    private static final char DISPLAY_CHARACTER = '⌬';
    private static final String NAME = "Atmospheric Monitor";

    private final EnvironmentalMonitorBehaviour behaviour;

    /**
     * Constructor.
     *
     * @param behaviour the behaviour that controls periodic atmospheric scans
     */
    public AtmosphericMonitor(EnvironmentalMonitorBehaviour behaviour) {
        super(DISPLAY_CHARACTER, NAME);
        this.behaviour = behaviour;
    }

    /**
     * Ticks the atmospheric monitor once per game turn.
     *
     * @param location the location of the monitor on the map
     */
    @Override
    public void tick(Location location) {
        behaviour.operate(this, location);
    }
}
