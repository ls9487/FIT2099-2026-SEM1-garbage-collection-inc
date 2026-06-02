/* DISABLED FOR APP RUN
package game.atmosphere;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests FallbackPollutionParser to make sure it always returns a safe default
 * air quality report when usable pollution data is missing or ignored.
 *
 * @author esoo0013
 */

//class FallbackPollutionParserTest {
//
//    @Test
//    void parseReturnsSafeFallbackReportWhenJsonIsNull() {
//        FallbackPollutionParser parser = new FallbackPollutionParser();
//
//        AirQualityReport report = parser.parse(null);
//
//        assertEquals(1, report.getAqi());
//        assertEquals("none", report.getDominantPollutant());
//    }
//
//    @Test
//    void parseIgnoresInputAndStillReturnsSafeFallbackReport() {
//        FallbackPollutionParser parser = new FallbackPollutionParser();
//
//        AirQualityReport report = parser.parse("{\"aqi\":5,\"so2\":99.0}");
//
//        assertEquals(1, report.getAqi());
//        assertEquals("none", report.getDominantPollutant());
//    }
//}
