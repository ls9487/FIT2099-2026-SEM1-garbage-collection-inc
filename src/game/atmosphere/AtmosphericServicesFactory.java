package game.atmosphere;

/**
 * Factory class that wires together the main REQ5 atmosphere services.
 * <p>
 * High-level classes such as {@link EnvironmentalMonitorController} and
 * {@link AtmosphericScanner} use this factory so they can depend on the
 * parser and corruptor abstractions instead of directly depending on concrete
 * implementations.
 * </p>
 *
 * @author esoo0013
 */
public class AtmosphericServicesFactory {

    /**
     * Creates the parser used to turn raw OpenWeather JSON into an
     * {@link AirQualityReport}.
     * <p>
     * If no API key is configured, this method returns the fallback parser so
     * the game can still run safely without a real API request.
     * </p>
     *
     * @return the parser to use for the current environment setup
     */
    public PollutionDataParser createParser() {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("[Toxic Atmosphere] No OPENWEATHER_API_KEY detected. Using fallback pollution data preset for demonstration.");
            return new FallbackPollutionParser();
        }

        System.out.println("[Toxic Atmosphere] OPENWEATHER_API_KEY detected. Using live OpenWeather pollution data.");
        return new OpenWeatherPollutionParser();
    }

    /**
     * Creates the list of atmospheric corruptors used by REQ5.
     *
     * The returned corruptors cover hazard effects, economy disruption, and
     * severe-AQI enemy spawning.
     *
     * @return the list of atmospheric corruptors to run after each scan
     */
    public java.util.List<AtmosphericCorruptor> createCorruptors() {
        return java.util.List.of(
                new HazardCorruptor(),
                new EconomyCorruptor(),
                new PollutantSpawnCorruptor()
        );
    }
}
