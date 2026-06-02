/**
 * Strategy abstraction for turning a raw JSON string from the AirVisual API
 * into an {@link AirQualityReport} that the rest of the game can understand.
 * <p>
 * Implementations of this interface are deliberately kept pure: they do not
 * perform any HTTP calls or mutate the game. Their only job is to translate
 * the JSON payload into a strongly typed snapshot of the atmosphere.
 * </p>
 *
 * @author esoo0013
 */
package game.atmosphere;

public interface PollutionDataParser {

    /**
     * Parse a raw JSON response string into an {@link AirQualityReport}.
     * Implementations may throw an unchecked exception if the payload is
     * malformed, but they should aim to be robust to missing fields.
     *
     * @param json raw JSON payload from the AirVisual API
     * @return a populated AirQualityReport representing the current conditions
     */
    AirQualityReport parse(String json);

}
