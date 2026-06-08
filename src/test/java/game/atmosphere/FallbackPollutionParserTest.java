package game.atmosphere;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests FallbackPollutionParser to make sure it always returns a safe default
 * air quality report when usable pollution data is missing or ignored.
 *
 * @author esoo0013
 */

class FallbackPollutionParserTest {

    /**
     * Hands the parser a null payload. The game should still keep running
     * with no atmospheric damage, so the parser must return AQI 1 and the
     * "none" pollutant. If this ever returns null itself, the scanner would
     * crash, which is exactly what the fallback exists to prevent.
     */
    @Test
    void parseReturnsSafeFallbackReportWhenJsonIsNull() {
        FallbackPollutionParser parser = new FallbackPollutionParser();

        AirQualityReport report = parser.parse(null);

        assertEquals(1, report.getAqi());
        assertEquals("none", report.getDominantPollutant());
    }

    /**
     * Passes a "nasty" looking real-style JSON (severe AQI and high SO2) to
     * make sure the fallback really does ignore its input. The whole point of
     * this parser is that it never trusts the JSON, so even a payload that
     * looks valid must still come back as the safe AQI 1 result.
     */
    @Test
    void parseIgnoresInputAndStillReturnsSafeFallbackReport() {
        FallbackPollutionParser parser = new FallbackPollutionParser();

        AirQualityReport report = parser.parse("{\"aqi\":5,\"so2\":99.0}");

        assertEquals(1, report.getAqi());
        assertEquals("none", report.getDominantPollutant());
    }
}
