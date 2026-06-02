/* DISABLED FOR APP RUN
package game.atmosphere;

import game.grounds.SuperComputer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests EconomyCorruptor to check that the global economy disruption flag reacts
 * correctly to the dominant pollutant reported by the parsed atmosphere data.
 *
 * @author esoo0013
 */

//class EconomyCorruptorTest {
//
//    @Test
//    void corruptEnablesEconomyDisruptionWhenSo2IsDominant() {
//        SuperComputer.isEconomyDisrupted() = false;
//        EconomyCorruptor corruptor = new EconomyCorruptor();
//
//        AirQualityReport report = new AirQualityReport(4, "so2");
//
//        try {
//            corruptor.corrupt(null, report);
//        } catch (NullPointerException ignored) {
//            // The flag is set before map iteration begins.
//        }
//
//        assertTrue(SuperComputer.isEconomyDisrupted());
//    }
//
//    @Test
//    void corruptDisablesEconomyDisruptionWhenDominantPollutantIsNotSo2() {
//        SuperComputer.isEconomyDisrupted() = true;
//        EconomyCorruptor corruptor = new EconomyCorruptor();
//
//        AirQualityReport report = new AirQualityReport(4, "no2");
//
//        corruptor.corrupt(null, report);
//
//        assertFalse(SuperComputer.isEconomyDisrupted());
//    }
//}