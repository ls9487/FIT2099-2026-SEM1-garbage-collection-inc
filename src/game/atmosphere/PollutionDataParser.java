package game.atmosphere;

/**
 * Strategy interface for turning raw API JSON into an {@link AirQualityReport}.
 *
 * This abstraction keeps the rest of the atmosphere system independent from any
 * one API format. It also allows the game to swap between live and fallback
 * parsers without changing the higher-level scan flow.
 *
 * @author esoo0013
 */
public interface PollutionDataParser {

    /**
     * Parses raw JSON into a simplified air quality report.
     *
     * @param json the raw JSON response
     * @return a simplified air quality report
     */
    AirQualityReport parse(String json);
}
