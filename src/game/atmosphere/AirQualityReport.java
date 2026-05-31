package game.atmosphere;

/**
 * Immutable value object that stores the parsed atmospheric conditions for REQ5.
 *
 * This class keeps together the AQI tier and the dominant pollutant returned by
 * the parser layer. Other REQ5 classes can then use this object instead of
 * depending on raw JSON strings.
 *
 * @author esoo0013
 */
public final class AirQualityReport {

    /**
     * Air Quality Index tier on the OpenWeather 1 to 5 scale.
     */
    private final int aqi;

    /**
     * Short code for the dominant pollutant, such as "no2" or "so2".
     */
    private final String dominantPollutant;

    /**
     * Constructor.
     *
     * @param aqi the AQI tier parsed from the API response
     * @param dominantPollutant the dominant pollutant code parsed from the API response
     */
    public AirQualityReport(int aqi, String dominantPollutant) {
        this.aqi = aqi;
        this.dominantPollutant = dominantPollutant;
    }

    /**
     * Getter for the AQI tier.
     *
     * @return the AQI value on the OpenWeather 1 to 5 scale
     */
    public int getAqi() {
        return aqi;
    }

    /**
     * Getter for the dominant pollutant code.
     *
     * @return the dominant pollutant code, such as "no2" or "so2"
     */
    public String getDominantPollutant() {
        return dominantPollutant;
    }
}
