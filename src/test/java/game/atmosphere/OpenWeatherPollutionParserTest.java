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

    @Test
    void parseExtractsAqiAndDominantPollutantWhenNo2IsHigher() {
        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();

        String json = "{\"aqi\":4,\"no2\":18.7,\"so2\":7.2}";

        AirQualityReport report = parser.parse(json);

        assertEquals(4, report.getAqi());
        assertEquals("no2", report.getDominantPollutant());
    }

    @Test
    void parseChoosesSo2WhenItIsHigher() {
        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();

        String json = "{\"aqi\":3,\"no2\":4.5,\"so2\":11.9}";

        AirQualityReport report = parser.parse(json);

        assertEquals(3, report.getAqi());
        assertEquals("so2", report.getDominantPollutant());
    }
}


