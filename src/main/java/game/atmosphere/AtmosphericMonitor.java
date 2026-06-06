package game.atmosphere;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Atmospheric monitor ground used for the REQ5 toxic atmosphere feature.
 *
 * This class represents the fixed monitor station placed on the map. Instead
 * of behaving like a living actor, it stays as environmental infrastructure and
 * triggers atmospheric scans through its attached
 * {@link EnvironmentalMonitorController}. Other REQ5 systems centre their
 * effects on this monitor's map location, passed in on each ground tick, so
 * they can locate the scan source without any extra marker interface.
 *
 * @author esoo0013
 */
public class AtmosphericMonitor extends Ground {

    private static final char DISPLAY_CHARACTER = '⌬';
    private static final String NAME = "Atmospheric Monitor";

    private final EnvironmentalMonitorController controller;

    /**
     * Constructor.
     *
     * @param controller the controller that manages periodic atmospheric scans
     */
    public AtmosphericMonitor(EnvironmentalMonitorController controller) {
        super(DISPLAY_CHARACTER, NAME);
        this.controller = controller;
    }

    /**
     * Ticks the atmospheric monitor once per game turn.
     *
     * @param location the location of the monitor on the map
     */
    @Override
    public void tick(Location location) {
        controller.operate(location);
    }
}
