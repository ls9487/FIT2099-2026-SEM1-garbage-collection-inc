package game.atmosphere;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the default and fallback behaviour of OpenWeatherPollutionParser so null
 * or incomplete input still produces a safe and predictable air quality report.
 *
 * @author esoo0013
 */

class OpenWeatherPollutionParserDefaultTest {

    /**
     * Feeds null into the real OpenWeather parser to make sure it does not
     * throw and instead returns the safe defaults of AQI 1 and the
     * "unknown" pollutant label. This is the "we never even got a payload"
     * case, distinct from the fallback parser's "we will never trust input"
     * case.
     */
    @Test
    void parseReturnsSafeDefaultsForNullJson() {
        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();

        AirQualityReport report = parser.parse(null);

        assertEquals(1, report.getAqi());
        assertEquals("unknown", report.getDominantPollutant());
    }

    /**
     * Sends a payload that has the two pollutant numbers but is missing the
     * AQI field. The parser should default AQI to 1 so no effects fire,
     * while still picking SO2 as dominant because 9.0 is higher than 2.0.
     * This proves the AQI default and the dominant-pollutant logic are
     * decoupled.
     */
    @Test
    void parseReturnsSafeAqiWhenAqiFieldIsMissing() {
        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();

        String json = "{\"no2\":2.0,\"so2\":9.0}";

        AirQualityReport report = parser.parse(json);

        assertEquals(1, report.getAqi());
        assertEquals("so2", report.getDominantPollutant());
    }
}
