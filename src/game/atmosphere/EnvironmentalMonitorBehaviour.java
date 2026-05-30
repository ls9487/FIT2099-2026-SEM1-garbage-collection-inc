/**
 * Behaviour that periodically triggers an {@link AtmosphericScanAction}.
 * <p>
 * This is the "set and forget" part of the feature. Once wired to an
 * AtmosphericMonitor actor, it keeps its own internal tick counter and every
 * N turns asks the monitor to scan the atmosphere again.
 * </p>
 *
 * @author esoo0013
 */
package game.atmosphere;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

public class EnvironmentalMonitorBehaviour implements Behaviour<Actor, Action> {

    private static final int REFRESH_INTERVAL = 1;

    private final PollutionDataParser parser;
    private final AtmosphericApiClient apiClient;
    private final AtmosphericServicesFactory factory;

    private int ticks = 0;

    public EnvironmentalMonitorBehaviour(PollutionDataParser parser,
                                         AtmosphericApiClient apiClient,
                                         AtmosphericServicesFactory factory) {
        this.parser = parser;
        this.apiClient = apiClient;
        this.factory = factory;
    }

    @Override
    public Action operate(Actor actor, Location location) {
        // Only trigger a scan every REFRESH_INTERVAL turns.
        ticks++;
        if (ticks % REFRESH_INTERVAL != 0) {
            return null;
        }

        List<AtmosphericCorruptor> corruptors = factory.createCorruptors();
        return new AtmosphericScanAction(parser, corruptors, apiClient);
    }
}
