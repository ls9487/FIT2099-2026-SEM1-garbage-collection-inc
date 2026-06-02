package game.atmosphere;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests EconomyCorruptor to check that the global economy disruption flag reacts
 * correctly to the dominant pollutant reported by the parsed atmosphere data.
 *
 * @author esoo0013
 */

class EconomyCorruptorTest {

    @Test
    void corruptEnablesEconomyDisruptionWhenSo2IsDominant() {
        EconomyCorruptor.ECONOMY_DISRUPTED = false;
        EconomyCorruptor corruptor = new EconomyCorruptor();

        AirQualityReport report = new AirQualityReport(4, "so2");

        try {
            corruptor.corrupt(null, report);
        } catch (NullPointerException ignored) {
            // The flag is set before map iteration begins.
        }

        assertTrue(EconomyCorruptor.ECONOMY_DISRUPTED);
    }

    @Test
    void corruptDisablesEconomyDisruptionWhenDominantPollutantIsNotSo2() {
        EconomyCorruptor.ECONOMY_DISRUPTED = true;
        EconomyCorruptor corruptor = new EconomyCorruptor();

        AirQualityReport report = new AirQualityReport(4, "no2");

        corruptor.corrupt(null, report);

        assertFalse(EconomyCorruptor.ECONOMY_DISRUPTED);
    }
}
