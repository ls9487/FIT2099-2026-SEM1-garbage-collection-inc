//package game.atmosphere;
//
//import edu.monash.fit2099.engine.positions.GameMap;
//import edu.monash.fit2099.engine.positions.Location;
//import edu.monash.fit2099.engine.positions.NumberRange;
//import game.grounds.SuperComputer;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// * Tests EconomyCorruptor to check that the global economy disruption flag reacts
// * correctly to the dominant pollutant reported by the parsed atmosphere data.
// *
// * @author esoo0013
// */
//
//class EconomyCorruptorTest {
//
//    @Test
//    void corruptEnablesEconomyDisruptionWhenSo2IsDominant() {
//        SuperComputer.setEconomyDisrupted(false);
//        EconomyCorruptor corruptor = new EconomyCorruptor();
//
//        AirQualityReport report = new AirQualityReport(4, "so2");
//        GameMap map = mock(GameMap.class);
//        Location emptyLocation = mock(Location.class);
//
//        when(map.getYRange()).thenReturn(new NumberRange(0, 1));
//        when(map.getXRange()).thenReturn(new NumberRange(0, 1));
//        when(map.at(0, 0)).thenReturn(emptyLocation);
//        when(emptyLocation.getActor()).thenReturn(null);
//
//        corruptor.corrupt(map, report, null);
//
//        assertTrue(SuperComputer.isEconomyDisrupted());
//    }
//
//    @Test
//    void corruptDisablesEconomyDisruptionWhenDominantPollutantIsNotSo2() {
//        SuperComputer.setEconomyDisrupted(true);
//        EconomyCorruptor corruptor = new EconomyCorruptor();
//
//        AirQualityReport report = new AirQualityReport(4, "no2");
//        GameMap map = mock(GameMap.class);
//
//        corruptor.corrupt(map, report, null);
//
//        assertFalse(SuperComputer.isEconomyDisrupted());
//    }
//}
