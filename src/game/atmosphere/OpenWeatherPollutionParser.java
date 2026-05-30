package game.atmosphere;

/**
 * Parser for OpenWeather's Air Pollution API response. To keep within the
 * assignment constraint of not adding external JSON libraries, this
 * implementation uses simple string searches to extract the AQI and a
 * dominant pollutant.
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

    private int extractInt(String json, String key, int defaultValue) {
        int idx = json.indexOf(key);
        if (idx == -1) {
            return defaultValue;
        }
        idx += key.length();
        int end = idx;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        try {
            return Integer.parseInt(json.substring(idx, end));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double extractDouble(String json, String key, double defaultValue) {
        int idx = json.indexOf(key);
        if (idx == -1) {
            return defaultValue;
        }
        idx += key.length();
        int end = idx;
        while (end < json.length()
                && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) {
            end++;
        }
        try {
            return Double.parseDouble(json.substring(idx, end));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
