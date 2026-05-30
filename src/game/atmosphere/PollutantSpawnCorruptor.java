package game.atmosphere;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Undead;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Atmospheric corruptor that reanimates the dead when severe pollution floods
 * the facility.
 *
 * <p>
 * When the AQI reported by the external API reaches a severe level (≥ 4),
 * this corruptor locates the atmospheric monitor on the map and spawns one
 * {@link Undead} on a valid empty adjacent tile. This represents the moon's
 * necrotic field being energised by concentrated pollutants.
 * </p>
 *
 * <p>
 * Design notes:
 * <ul>
 *   <li>Depends strictly on {@link AtmosphericCorruptor} and
 *       {@link AirQualityReport} : no concrete corruptor types referenced.</li>
 *   <li>Intentionally distinct from {@link HazardCorruptor} (terrain mutation)
 *       and {@link EconomyCorruptor} (credit erosion): this class focuses solely
 *       on pollution-driven entity spawning.</li>
 * </ul>
 * </p>
 *
 * @author esoo0013
 */
public class PollutantSpawnCorruptor implements AtmosphericCorruptor {

    private final Random random = new Random();

    /**
     * Spawns an {@link Undead} near the atmospheric monitor when the AQI is
     * severe (≥ 4). Does nothing on safe or moderate pollution.
     *
     * @param map    the game map to act on
     * @param report the current atmospheric conditions
     */
    @Override
    public void corrupt(GameMap map, AirQualityReport report) {
        if (report.getAqi() < 4) {
            return;
        }

        Location monitorLocation = findMonitor(map);
        if (monitorLocation == null) {
            return;
        }

        List<Location> candidates = collectEmptyNeighbours(monitorLocation);
        if (candidates.isEmpty()) {
            return;
        }

        Location spawnLocation = candidates.get(random.nextInt(candidates.size()));
        try {
            spawnLocation.addActor(new Undead());
            System.out.println("[Toxic Atmosphere] AQI " + report.getAqi()
                    + " : the polluted atmosphere reanimates a corpse near the monitor!");
        } catch (GameEngineException e) {
            // Destination became occupied between the check and the spawn; skip.
        }
    }

    /**
     * Scans the map for a location containing an atmospheric anchor.
     *
     * @param map the map to search
     * @return the monitor location, or {@code null} if none exists on this map
     */
    private Location findMonitor(GameMap map) {
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location here = map.at(x, y);
                if (here.getGroundAs(AtmosphericAnchor.class) != null) {
                    return here;
                }
            }
        }
        return null;
    }

    /**
     * Collects all empty tiles adjacent to a given centre location.
     *
     * @param centre the location to inspect exits from
     * @return a list of empty neighbouring locations
     */
    private List<Location> collectEmptyNeighbours(Location centre) {
        List<Location> result = new ArrayList<>();
        for (Exit exit : centre.getExits()) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor()) {
                result.add(dest);
            }
        }
        return result;
    }
}
