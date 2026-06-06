package game.atmosphere;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Strategy interface for applying atmosphere-related corruption effects.
 *
 * Different implementations can react to the same air quality report in their
 * own way, such as spreading hazards, disrupting the economy, or spawning
 * enemies.
 *
 * @author esoo0013
 */
public interface AtmosphericCorruptor {

    /**
     * Applies this corruptor to the game map based on the current air quality.
     *
     * @param map the map to corrupt
     * @param report the air quality report driving the corruption
     * @param anchorLocation the location of the atmospheric monitor
     */
    void corrupt(GameMap map, AirQualityReport report, Location anchorLocation);
}
