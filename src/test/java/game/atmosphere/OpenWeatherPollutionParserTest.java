package game.atmosphere;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests OpenWeatherPollutionParser with representative payloads to confirm it
 * reads AQI values and identifies the strongest pollutant correctly.
 *
 * @author esoo0013
 */

class OpenWeatherPollutionParserTest {

    /**
     * Gives the parser a realistic-looking payload where the NO2 reading is
     * clearly larger than the SO2 reading. The AQI should come out as 4 and
     * the dominant pollutant as "no2". This is the happy path for the
     * "NO2 wins" branch of the comparison.
     */
    @Test
    void parseExtractsAqiAndDominantPollutantWhenNo2IsHigher() {
        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();

        String json = "{\"aqi\":4,\"no2\":18.7,\"so2\":7.2}";

        AirQualityReport report = parser.parse(json);

        assertEquals(4, report.getAqi());
        assertEquals("no2", report.getDominantPollutant());
    }

    /**
     * Flips the values so SO2 is the larger number. The parser should still
     * read the AQI correctly (3 this time) and now report "so2" as dominant.
     * Pairing this with the NO2 case proves the dominant-pollutant choice
     * really does depend on the comparison, not a hard-coded winner.
     */
    @Test
    void parseChoosesSo2WhenItIsHigher() {
        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();

        String json = "{\"aqi\":3,\"no2\":4.5,\"so2\":11.9}";

        AirQualityReport report = parser.parse(json);

        assertEquals(3, report.getAqi());
        assertEquals("so2", report.getDominantPollutant());
    }
}


