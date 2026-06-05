package game.atmosphere;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Interface for actors that respond to toxic atmosphere effects in REQ5.
 *
 * Classes implementing this interface define their own guaranteed reaction once
 * HazardCorruptor decides they should be affected by the current AQI tier.
 * This keeps actor-specific behaviour inside the actor class instead of putting
 * every special case into one large corruptor.
 *
 * @author esoo0013
 */
public interface AtmosphereSensitiveActor {

    /**
     * Applies the atmospheric effect for the current AQI report to this actor.
     *
     * @param report the current atmospheric conditions
     * @param here the actor's current location on the map
     */
    void applyAtmosphere(AirQualityReport report, Location here);
}
