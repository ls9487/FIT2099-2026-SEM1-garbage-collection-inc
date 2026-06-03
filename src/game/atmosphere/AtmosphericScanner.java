package game.atmosphere;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.SuperComputer;

import java.util.List;

/**
 * Service class that runs the full REQ5 atmospheric scan pipeline.
 * <p>
 * This scanner fetches raw JSON from the API client, parses it into an
 * {@link AirQualityReport}, and then passes that report to each registered
 * {@link AtmosphericCorruptor}. In this way, the scanner acts as the main link
 * between the external API layer and the in-game corruption effects.
 * </p>
 *
 * @author esoo0013
 */
public class AtmosphericScanner {

    public static final int SAFE_AQI_THRESHOLD = 2;
    public static final int MODERATE_AQI = 3;

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
    public AtmosphericScanner(PollutionDataParser parser,
                              List<AtmosphericCorruptor> corruptors,
                              AtmosphericApiClient apiClient) {
        this.parser = parser;
        this.corruptors = corruptors;
        this.apiClient = apiClient;
    }

    /**
     * Runs the atmospheric scan using the supplied atmospheric anchor.
     *
     * @param anchor the atmospheric anchor that acts as the scan source
     * @param map the map containing the atmospheric monitor
     * @param anchorLocation the current location of the atmospheric monitor
     */
    public void scan(AtmosphericAnchor anchor, GameMap map, Location anchorLocation) {
        String json = apiClient.fetch(anchor, map, anchorLocation);
        AirQualityReport report = parser.parse(json);

        for (AtmosphericCorruptor corruptor : corruptors) {
            corruptor.corrupt(map, report, anchorLocation);
        }

        System.out.println(buildScanSummary(report));
    }

    /**
     * Builds a player-facing summary of the current atmospheric scan result.
     * <p>
     * The summary explains the AQI tier, the dominant pollutant, and the main
     * gameplay consequences expected for that tier. Mild air reports that no
     * corruption occurs. Moderate air reports actor-specific harm and local
     * toxic puddle spread near affected actors. Severe air reports heavier
     * actor harm, large-scale toxic waste growth including border and monitor
     * hotspot corruption, and possible undead reanimation near the monitor.
     * If the dominant pollutant has disrupted the economy, the summary also
     * states that credits may be reduced, never below zero, and that super
     * computer sell payouts may fail during disruption.
     * </p>
     *
     * @param report the parsed air quality report for the current scan
     * @return a descriptive one-line summary of the atmospheric effects for this turn
     *
     * author @esoo0013
     */
    private String buildScanSummary(AirQualityReport report) {
        int aqi = report.getAqi();
        String pollutant = report.getDominantPollutant();

        if (aqi <= SAFE_AQI_THRESHOLD) {
            return "[Toxic Atmosphere] AQI " + aqi
                    + " with dominant pollutant " + pollutant
                    + ". Air remains stable; no toxic effects trigger this turn.";
        }

        if (aqi == MODERATE_AQI) {
            return "[Toxic Atmosphere] AQI " + aqi
                    + " with dominant pollutant " + pollutant
                    + ". Sensitive actors may be hurt, and local toxic waste puddles can spread around affected actors.";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("[Toxic Atmosphere] AQI ")
                .append(aqi)
                .append(" with dominant pollutant ")
                .append(pollutant)
                .append(". Severe pollution can heavily harm sensitive actors, spread toxic waste across the facility border, corrupt tiles near the monitor hotspot, and reanimate undead near the monitor.");

        if (SuperComputer.isEconomyDisrupted()) {
            summary.append(" Sulphur dioxide has disrupted the economy, so credits may be reduced but will not drop below 0, and Super Computer sell payouts may fail this turn.");
        }

        return summary.toString();
    }
}
