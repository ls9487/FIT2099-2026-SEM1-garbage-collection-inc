//package game.atmosphere;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
///**
// * Tests the default and fallback behaviour of OpenWeatherPollutionParser so null
// * or incomplete input still produces a safe and predictable air quality report.
// *
// * @author esoo0013
// */
//
//class OpenWeatherPollutionParserDefaultTest {
//
//    @Test
//    void parseReturnsSafeDefaultsForNullJson() {
//        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();
//
//        AirQualityReport report = parser.parse(null);
//
//        assertEquals(1, report.getAqi());
//        assertEquals("unknown", report.getDominantPollutant());
//    }
//
//    @Test
//    void parseReturnsSafeAqiWhenAqiFieldIsMissing() {
//        OpenWeatherPollutionParser parser = new OpenWeatherPollutionParser();
//
//        String json = "{\"no2\":2.0,\"so2\":9.0}";
//
//        AirQualityReport report = parser.parse(json);
//
//        assertEquals(1, report.getAqi());
//        assertEquals("so2", report.getDominantPollutant());
//    }
//}
