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

    @Test
    void constructorStoresAqiValue() {
        AirQualityReport report = new AirQualityReport(4, "so2");

        assertEquals(4, report.getAqi());
    }

    @Test
    void constructorStoresDominantPollutant() {
        AirQualityReport report = new AirQualityReport(2, "no2");

        assertEquals("no2", report.getDominantPollutant());
    }

    @Test
    void gettersReturnValuesForDifferentReport() {
        AirQualityReport report = new AirQualityReport(5, "pm10");

        assertEquals(5, report.getAqi());
        assertEquals("pm10", report.getDominantPollutant());
    }
}