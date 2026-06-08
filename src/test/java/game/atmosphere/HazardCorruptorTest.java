package game.atmosphere;

import org.junit.jupiter.api.Test;

/**
 * Tests HazardCorruptor with a safe report so the class can be verified without
 * needing a real game map for this lightweight unit test.
 *
 * @author esoo0013
 */

class HazardCorruptorTest {

    /**
     * Calls corrupt with AQI 2 (the top of the mild tier) and a null map.
     * Because mild air should not touch the map at all, the corruptor must
     * exit before ever reading map.getXRange or anything else. If it did
     * touch the map this test would throw a NullPointerException. Passing
     * without exception is the assertion.
     */
    @Test
    void corruptDoesNothingForSafeAqiWhenMapIsNull() {
        HazardCorruptor corruptor = new HazardCorruptor();
        AirQualityReport report = new AirQualityReport(2, "no2");

        corruptor.corrupt(null, report, null);
    }
}
