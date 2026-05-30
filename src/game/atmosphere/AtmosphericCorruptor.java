/**
 * Abstraction for classes that take an {@link AirQualityReport} and apply
 * complex, cross-component changes to the game world.
 * <p>
 * Implementations of this interface do not know anything about HTTP or JSON.
 * They simply read the already-parsed report and decide how badly the
 * Eclipse Nebula facility should suffer: damage over time, poisoned workers,
 * toxic waste spreading, door glitches, weird spawn rates, and so on.
 * </p>
 *
 * This separation keeps the API boundary (parsing) and the game logic
 * (corruption) cleanly decoupled, which helps a lot when writing tests.
 *
 * @author esoo0013
 */
package game.atmosphere;

import edu.monash.fit2099.engine.positions.GameMap;

public interface AtmosphericCorruptor {

    /**
     * Apply atmospheric effects to the given map based on the supplied report.
     * Implementations are free to hurt actors, change grounds, spawn things,
     * and generally make a mess, as long as they stay within the existing
     * engine API.
     *
     * @param map    the GameMap to mutate
     * @param report the current atmospheric conditions snapshot
     */
    void corrupt(GameMap map, AirQualityReport report);
}
