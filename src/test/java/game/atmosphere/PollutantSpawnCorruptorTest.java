package game.atmosphere;

import org.junit.jupiter.api.Test;

/**
 * Tests PollutantSpawnCorruptor to confirm that harmless reports do not trigger
 * spawning side effects when the atmosphere should remain stable.
 *
 * @author esoo0013
 */

class PollutantSpawnCorruptorTest {

    /**
     * Calls corrupt with a moderate AQI of 3 and a null map. The corruptor
     * should return early before it touches the map or tries to spawn
     * anything, because severe spawning only kicks in at AQI 4 or above.
     * If it ever reached the spawn logic this would throw, so passing
     * without exception confirms the early-exit guard works.
     */
    @Test
    void corruptDoesNothingBelowSevereAqiWhenMapIsNull() {
        PollutantSpawnCorruptor corruptor = new PollutantSpawnCorruptor();
        AirQualityReport report = new AirQualityReport(3, "no2");

        corruptor.corrupt(null, report, null);
    }
}
