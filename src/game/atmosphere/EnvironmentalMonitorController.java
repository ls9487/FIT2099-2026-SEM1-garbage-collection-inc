package game.atmosphere;

import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Controller class that periodically triggers an {@link AtmosphericScanner}.
 * <p>
 * This is the automatic scanning part of the REQ5 feature. Once attached to an
 * {@link AtmosphericMonitor}, it keeps its own internal tick counter and runs a
 * full atmospheric scan at a fixed interval.
 * </p>
 *
 * @author esoo0013
 */
public class EnvironmentalMonitorController {

    private static final int REFRESH_INTERVAL = 1;

    private final PollutionDataParser parser;
    private final AtmosphericApiClient apiClient;
    private final AtmosphericServicesFactory factory;

    private int ticks = 0;

    /**
     * Constructor.
     *
     * @param parser the parser used to convert raw JSON into an air quality report
     * @param apiClient the API client used to fetch live air pollution data
     * @param factory the factory used to create the atmosphere corruptors
     */
    public EnvironmentalMonitorController(PollutionDataParser parser,
                                          AtmosphericApiClient apiClient,
                                          AtmosphericServicesFactory factory) {
        this.parser = parser;
        this.apiClient = apiClient;
        this.factory = factory;
    }

    /**
     * Runs the monitor controller for one tick.
     *
     * @param anchor the atmospheric anchor owned by the monitor
     * @param location the current location of the atmospheric monitor
     */
    public void operate(AtmosphericAnchor anchor, Location location) {
        ticks++;
        if (ticks % REFRESH_INTERVAL != 0) {
            return;
        }

        List<AtmosphericCorruptor> corruptors = factory.createCorruptors();
        new AtmosphericScanner(parser, corruptors, apiClient).scan(anchor, location.map());
    }
}
