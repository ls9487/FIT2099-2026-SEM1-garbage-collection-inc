package game.atmosphere;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.grounds.SuperComputer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests EconomyCorruptor to check that the global economy disruption flag reacts
 * correctly to the dominant pollutant reported by the parsed atmosphere data.
 *
 * @author esoo0013
 */

class EconomyCorruptorTest {

    /**
     * Starts the SuperComputer with the disruption flag off, then runs the
     * corruptor on a tiny 1*1 mocked map with an SO2-dominant report. After
     * the call the flag should be on, which proves the corruptor reacts to
     * SO2 in particular and is the class actually flipping that switch.
     */
    @Test
    void corruptEnablesEconomyDisruptionWhenSo2IsDominant() {
        SuperComputer.setEconomyDisrupted(false);
        EconomyCorruptor corruptor = new EconomyCorruptor();

        AirQualityReport report = new AirQualityReport(4, "so2");
        GameMap map = mock(GameMap.class);
        Location emptyLocation = mock(Location.class);

        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
        when(map.at(0, 0)).thenReturn(emptyLocation);
        when(emptyLocation.getActor()).thenReturn(null);

        corruptor.corrupt(map, report, null);

        assertTrue(SuperComputer.isEconomyDisrupted());
    }

    /**
     * Pre-sets the disruption flag to true and runs the corruptor with an
     * NO2-dominant report. The corruptor should clear the flag back to false
     * because anything that is not SO2 means the shop should behave normally
     * again. This guards against a previous turn leaving disruption stuck on.
     */
    @Test
    void corruptDisablesEconomyDisruptionWhenDominantPollutantIsNotSo2() {
        SuperComputer.setEconomyDisrupted(true);
        EconomyCorruptor corruptor = new EconomyCorruptor();

        AirQualityReport report = new AirQualityReport(4, "no2");
        GameMap map = mock(GameMap.class);

        corruptor.corrupt(map, report, null);

        assertFalse(SuperComputer.isEconomyDisrupted());
    }
}
