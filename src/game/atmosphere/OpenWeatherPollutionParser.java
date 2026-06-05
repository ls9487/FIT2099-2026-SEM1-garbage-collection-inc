package game.atmosphere;

/**
 * Parser for OpenWeather's Air Pollution API response. To keep within the
 * assignment constraint of not adding external JSON libraries, this
 * implementation uses simple string searches to extract the AQI and the
 * dominant pollutant between {@code no2} and {@code so2}.
 *
 * @author esoo0013
 */
public class OpenWeatherPollutionParser implements PollutionDataParser {

    private static final int DEFAULT_SAFE_AQI = 1;
    private static final double DEFAULT_COMPONENT_LEVEL = 0.0;
    private static final String UNKNOWN_POLLUTANT = "unknown";

    @Override
    public AirQualityReport parse(String json) {
        if (json == null || json.isEmpty()) {
            return new AirQualityReport(DEFAULT_SAFE_AQI, UNKNOWN_POLLUTANT);
        }

        // Default to AQI 1 (safe) when a field is missing so the game does
        // not apply effects when the API returns an empty or malformed payload.
        int aqi = extractInt(json, "\"aqi\":", DEFAULT_SAFE_AQI);
        double no2 = extractDouble(json, "\"no2\":", DEFAULT_COMPONENT_LEVEL);
        double so2 = extractDouble(json, "\"so2\":", DEFAULT_COMPONENT_LEVEL);
        String dominant = no2 >= so2 ? "no2" : "so2";

        return new AirQualityReport(aqi, dominant);
    }

    /**
     * Extracts an integer value that follows the given key in the JSON string.
     *
     * @param json the JSON payload to scan
     * @param key the key (including the colon) to look for, e.g. {@code "\"aqi\":"}
     * @param defaultValue the value to return if the key is missing or the digits cannot be parsed
     * @return the parsed integer, or {@code defaultValue} if extraction fails
     */
    private int extractInt(String json, String key, int defaultValue) {
        int startIndex = json.indexOf(key);
        if (startIndex == -1) {
            return defaultValue;
        }
        startIndex += key.length();
        int endIndex = startIndex;
        while (endIndex < json.length() && Character.isDigit(json.charAt(endIndex))) {
            endIndex++;
        }
        try {
            return Integer.parseInt(json.substring(startIndex, endIndex));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Extracts a decimal value that follows the given key in the JSON string.
     *
     * @param json the JSON payload to scan
     * @param key the key (including the colon) to look for, e.g. {@code "\"no2\":"}
     * @param defaultValue the value to return if the key is missing or the digits cannot be parsed
     * @return the parsed double, or {@code defaultValue} if extraction fails
     */
    private double extractDouble(String json, String key, double defaultValue) {
        int startIndex = json.indexOf(key);
        if (startIndex == -1) {
            return defaultValue;
        }
        startIndex += key.length();
        int endIndex = startIndex;
        while (endIndex < json.length()
                && (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '.')) {
            endIndex++;
        }
        try {
            return Double.parseDouble(json.substring(startIndex, endIndex));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
