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

    /** Moderate AQI preset for demo mode. */
    private static final int MODERATE_AQI = 3;

    /** Severe AQI preset for demo mode. */
    private static final int SEVERE_AQI = 4;

    /** Dominant pollutant returned when no real data is available. */
    private static final String NO_POLLUTANT = "none";

    /** Nitrogen dioxide preset for non-economy demo corruption. */
    private static final String NO2_POLLUTANT = "no2";

    /** Sulphur dioxide preset for economy disruption demo corruption. */
    private static final String SO2_POLLUTANT = "so2";

    /**
     * Ignores the supplied JSON and returns the currently selected fallback
     * report.
     * This defaults to a safe AQI-1 result, but the commented demo presets
     * below can be toggled locally to simulate moderate, severe, or
     * economy-disruption scenarios when no real API key is available.
     *
     * @param json ignored : may be null or empty
     * @return the active fallback {@link AirQualityReport} preset
     */
    @Override
    public AirQualityReport parse(String json) {
        // DEMO PRESETS: keep exactly ONE return line active at a time.
        // Comment out the current active line, then uncomment the single mode
        // you want to demonstrate while running without OPENWEATHER_API_KEY.

        // Safe mode: no atmospheric corruption and no economy disruption.
        return new AirQualityReport(SAFE_AQI, NO_POLLUTANT);

        // Moderate toxic mode: actor reactions + local ToxicWaste spread,
        // but no economy disruption.
        // return new AirQualityReport(MODERATE_AQI, NO2_POLLUTANT);

        // Severe toxic mode: stronger actor effects, border/hotspot corruption,
        // and Undead spawning, but no economy disruption.
        // return new AirQualityReport(SEVERE_AQI, NO2_POLLUTANT);

        // Severe economy-disruption mode: severe atmospheric effects plus SO2,
        // which enables disrupted selling behaviour.
        // return new AirQualityReport(SEVERE_AQI, SO2_POLLUTANT);
    }
}
