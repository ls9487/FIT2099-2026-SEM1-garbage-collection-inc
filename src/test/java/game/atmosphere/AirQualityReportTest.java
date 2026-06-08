package game.atmosphere;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the small AirQualityReport value object to make sure its constructor
 * and getters keep the AQI and dominant pollutant values exactly as expected.
 *
 * @author esoo0013
 */

class AirQualityReportTest {

    /**
     * Checks that the AQI integer handed into the constructor is the same
     * value that getAqi returns later. Picks a moderately bad number (4) so
     * a default like 0 or 1 would obviously fail.
     */
    @Test
    void constructorStoresAqiValue() {
        AirQualityReport report = new AirQualityReport(4, "so2");

        assertEquals(4, report.getAqi());
    }

    /**
     * Confirms that the dominant pollutant string survives the trip through
     * the constructor unchanged. Uses "no2" because the rest of the codebase
     * relies on that exact lowercase token to drive corruption logic.
     */
    @Test
    void constructorStoresDominantPollutant() {
        AirQualityReport report = new AirQualityReport(2, "no2");

        assertEquals("no2", report.getDominantPollutant());
    }

    /**
     * Builds a different report (AQI 5 with pm10) and checks both fields at
     * once. The point of this case is to show the value object holds its own
     * data, so a second instance does not accidentally share state with the
     * earlier ones.
     */
    @Test
    void gettersReturnValuesForDifferentReport() {
        AirQualityReport report = new AirQualityReport(5, "pm10");

        assertEquals(5, report.getAqi());
        assertEquals("pm10", report.getDominantPollutant());
    }
}