package game.atmosphere;

/**
 * Safe-fallback implementation of {@link PollutionDataParser}.
 *
 * <p>
 * This parser is used when no real API payload is available : for example
 * when the {@code OPENWEATHER_API_KEY} environment variable is not set, or
 * when network access fails. It always returns a benign AQI-1 report so the
 * game continues to run without applying any atmospheric side effects.
 * </p>
 *
 * <p>
 * Having a dedicated fallback class keeps {@link OpenWeatherPollutionParser}
 * focused purely on real-payload parsing and avoids the need for magic default
 * values or null checks scattered across the call stack.
 * </p>
 *
 * @author esoo0013
 */
public class FallbackPollutionParser implements PollutionDataParser {

    /** AQI returned when no real data is available; always safe, no effects. */
    private static final int SAFE_AQI = 1;

    /** Dominant pollutant returned when no real data is available. */
    private static final String NO_POLLUTANT = "none";

    /**
     * Ignores the supplied JSON and returns a fixed safe report.
     * This guarantees that missing or empty API responses never trigger
     * atmospheric hazards.
     *
     * @param json ignored : may be null or empty
     * @return an {@link AirQualityReport} with AQI 1 and no dominant pollutant
     */
    @Override
    public AirQualityReport parse(String json) {
        return new AirQualityReport(SAFE_AQI, NO_POLLUTANT);
    }
}
