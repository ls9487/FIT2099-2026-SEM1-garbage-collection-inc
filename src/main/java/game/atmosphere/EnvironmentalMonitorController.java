package game.atmosphere;

import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Controller class that triggers atmospheric scans from the monitor ground.
 * <p>
 * This class is used by {@link AtmosphericMonitor} during its regular ground
 * tick. The controller keeps the scheduling logic separate from the ground so
 * the monitor itself stays small and focused.
 * </p>
 *
 * @author esoo0013
 */
public class EnvironmentalMonitorController {

    private final AtmosphericScanner scanner;

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
        List<AtmosphericCorruptor> corruptors = factory.createCorruptors();
        this.scanner = new AtmosphericScanner(parser, corruptors, apiClient);
    }

    /**
     * Runs the monitor controller for one game turn.
     *
     * @param location the current location of the atmospheric monitor
     */
    public void operate(Location location) {
        scanner.scan(location.map(), location);
    }
}
