package game.atmosphere;

import edu.monash.fit2099.engine.items.Inventory;
import game.actors.EclipseActor;

/**
 * Atmospheric monitor actor used for the REQ5 toxic atmosphere feature.
 *
 * This actor acts as the in-game monitor station that periodically triggers an
 * atmospheric scan through its attached {@link EnvironmentalMonitorBehaviour}.
 * It also implements {@link AtmosphericAnchor} so other REQ5 systems can use
 * it as the centre point for hotspot corruption and pollution-based spawning.
 *
 * @author esoo0013
 */
public class AtmosphericMonitor extends EclipseActor implements AtmosphericAnchor {

    /**
     * Constructor.
     *
     * @param inventory the inventory owned by the monitor
     * @param behaviour the behaviour that controls periodic atmospheric scans
     */
    public AtmosphericMonitor(Inventory inventory, EnvironmentalMonitorBehaviour behaviour) {
        super("Atmospheric Monitor", 'M', 1, inventory);
        this.addNewBehaviour(999, behaviour);
    }
}
