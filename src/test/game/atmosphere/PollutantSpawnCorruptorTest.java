//package game.atmosphere;
//
//import org.junit.jupiter.api.Test;
//
///**
// * Tests PollutantSpawnCorruptor to confirm that harmless reports do not trigger
// * spawning side effects when the atmosphere should remain stable.
// *
// * @author esoo0013
// */
//
//class PollutantSpawnCorruptorTest {
//
//    @Test
//    void corruptDoesNothingBelowSevereAqiWhenMapIsNull() {
//        PollutantSpawnCorruptor corruptor = new PollutantSpawnCorruptor();
//        AirQualityReport report = new AirQualityReport(3, "no2");
//
//        corruptor.corrupt(null, report, null);
//    }
//}
