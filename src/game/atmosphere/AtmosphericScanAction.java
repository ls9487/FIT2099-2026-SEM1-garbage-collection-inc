package game.atmosphere;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.List;

/**
 * Action that runs the full REQ5 atmospheric scan pipeline.
 * <p>
 * This action fetches raw JSON from the API client, parses it into an
 * {@link AirQualityReport}, and then passes that report to each registered
 * {@link AtmosphericCorruptor}. In this way, the action acts as the main link
 * between the external API layer and the in-game corruption effects.
 * </p>
 *
 * @author esoo0013
 */
public class AtmosphericScanAction extends Action {

    private static final int SAFE_AQI_THRESHOLD = 2;
    private static final int MODERATE_AQI = 3;

    private final PollutionDataParser parser;
    private final List<AtmosphericCorruptor> corruptors;
    private final AtmosphericApiClient apiClient;

    /**
     * Constructor.
     *
     * @param parser the parser used to convert raw JSON into an air quality report
     * @param corruptors the list of corruptors that apply the in-game effects
     * @param apiClient the API client used to fetch raw air pollution data
     */
    public AtmosphericScanAction(PollutionDataParser parser,
                                 List<AtmosphericCorruptor> corruptors,
                                 AtmosphericApiClient apiClient) {
        this.parser = parser;
        this.corruptors = corruptors;
        this.apiClient = apiClient;
    }

    /**
     * Executes the atmospheric scan using the supplied atmospheric anchor.
     *
     * @param anchor the atmospheric anchor that acts as the scan source
     * @param map the map containing the atmospheric monitor
     * @return a summary string showing the AQI tier and dominant pollutant
     */
    public String execute(AtmosphericAnchor anchor, GameMap map) {
        String json = apiClient.fetch(anchor, map);
        AirQualityReport report = parser.parse(json);

        for (AtmosphericCorruptor corruptor : corruptors) {
            corruptor.corrupt(map, report);
        }

        String tier;
        if (report.getAqi() <= SAFE_AQI_THRESHOLD) {
            tier = "Safe : no effects";
        } else if (report.getAqi() == MODERATE_AQI) {
            tier = "Moderate : local contamination spreading";
        } else {
            tier = "SEVERE : facility-wide toxic event!";
        }
        return String.format("[Atmospheric Scan] AQI %d (%s), dominant pollutant: %s.",
                report.getAqi(), tier, report.getDominantPollutant().toUpperCase());
    }

    /**
     * Unused menu description retained because this class extends Action.
     *
     * @param actor the actor requesting the menu description
     * @return a generic description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " scans the atmosphere";
    }

    /**
     * Unsupported actor-based execute method.
     *
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return the actor-based scan result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        throw new UnsupportedOperationException(
                "Use execute(AtmosphericAnchor, GameMap) for atmospheric monitor scans."
        );
    }
}
