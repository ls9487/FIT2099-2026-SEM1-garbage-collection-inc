package game.atmosphere;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;

import java.util.Random;

/**
 * Converts atmospheric pollution data into large-scale environmental hazards.
 *
 * This corruptor interprets the AQI stored in an {@link AirQualityReport} and
 * applies escalating effects to the game world. At moderate pollution levels,
 * it delegates actor-specific reactions to all {@link AtmosphereSensitiveActor}
 * implementations and can create small local waste clusters near affected
 * actors. At severe pollution levels, it also corrupts large regions of the
 * map, including the outer border and an area surrounding the atmospheric
 * anchor.
 *
 * This design keeps atmospheric mutation logic centralised while preserving
 * polymorphism by letting each affected actor define its own reaction through
 * the {@link AtmosphereSensitiveActor} abstraction.
 *
 * @author esoo0013
 */
public class HazardCorruptor implements AtmosphericCorruptor {

    private static final int SAFE_AQI_THRESHOLD = 2;
    private static final int MODERATE_AQI = 3;
    private static final int SEVERE_AQI_THRESHOLD = 4;
    private static final int LOCAL_WASTE_SPREAD_PERCENT = 25;
    private static final int RANDOM_BOUND = 100;
    private static final int HOTSPOT_RADIUS = 2;

    private final Random random = new Random();

    /**
     * Applies AQI-driven hazard effects to the provided map.
     *
     * Safe or mild pollution causes no change. Moderate pollution applies
     * actor-specific atmospheric reactions and may create small toxic puddles.
     * Severe pollution further mutates the wider environment by surrounding the
     * map border with {@link ToxicWaste} and creating a pollution hotspot near
     * an atmospheric anchor. This method also prints event messages only when a
     * visible terrain change actually happens.
     *
     * @param map the game map to corrupt
     * @param report the air quality report describing the current atmospheric state
     * @param anchorLocation the current location of the atmospheric monitor
     */
    @Override
    public void corrupt(GameMap map, AirQualityReport report, Location anchorLocation) {
        int aqi = report.getAqi();

        if (aqi <= SAFE_AQI_THRESHOLD) {
            return;
        }

        int localWasteTilesCreated = 0;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location location = map.at(x, y);
                Actor actor = location.getActor();
                if (actor == null) {
                    continue;
                }

                var sensitive = actor.asCapability(AtmosphereSensitiveActor.class);
                if (sensitive.isPresent()) {
                    sensitive.get().applyAtmosphere(report, location);

                    if (aqi == MODERATE_AQI) {
                        localWasteTilesCreated += spreadLocalWaste(location);
                    }
                }
            }
        }

        if (aqi == MODERATE_AQI && localWasteTilesCreated > 0) {
            System.out.println("[Toxic Atmosphere] Local contamination spreads: "
                    + localWasteTilesCreated + " toxic waste tile(s) form near affected actors.");
        }

        if (aqi >= SEVERE_AQI_THRESHOLD) {
            int borderTilesCorrupted = createBorderWasteRing(map);
            int hotspotTilesCorrupted = createAnchorHotspot(map, anchorLocation);

            if (borderTilesCorrupted > 0) {
                System.out.println("[Toxic Atmosphere] Severe pollution corrupts the facility border: "
                        + borderTilesCorrupted + " perimeter tile(s) become toxic waste.");
            }

            if (hotspotTilesCorrupted > 0) {
                System.out.println("[Toxic Atmosphere] The monitor hotspot mutates: "
                        + hotspotTilesCorrupted + " nearby tile(s) become toxic waste.");
            }
        }
    }

    /**
     * Randomly spreads small pockets of {@link ToxicWaste} around a centre
     * location to simulate local contamination.
     *
     * Only empty adjacent tiles may be corrupted. Existing toxic waste tiles do
     * not count again.
     *
     * @param centre the source location around which waste may spread
     * @return the number of newly corrupted adjacent tiles
     */
    private int spreadLocalWaste(Location centre) {
        int tilesCreated = 0;

        for (Exit exit : centre.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                continue;
            }

            if (random.nextInt(RANDOM_BOUND) < LOCAL_WASTE_SPREAD_PERCENT) {
                tilesCreated += corruptIfNeeded(destination);
            }
        }
        return tilesCreated;
    }

    /**
     * Corrupts the outer border of the map with {@link ToxicWaste} to represent
     * severe, persistent atmospheric contamination.
     *
     * The returned count only includes perimeter tiles that were newly changed
     * during this scan.
     *
     * @param map the map whose perimeter should be mutated
     * @return the number of perimeter tiles newly converted into toxic waste
     */
    private int createBorderWasteRing(GameMap map) {
        int minX = map.getXRange().min();
        int maxX = map.getXRange().max();
        int minY = map.getYRange().min();
        int maxY = map.getYRange().max();
        int tilesCreated = 0;

        for (int x = minX; x <= maxX; x++) {
            tilesCreated += corruptIfNeeded(map.at(x, minY));
            tilesCreated += corruptIfNeeded(map.at(x, maxY));
        }

        for (int y = minY + 1; y < maxY; y++) {
            tilesCreated += corruptIfNeeded(map.at(minX, y));
            tilesCreated += corruptIfNeeded(map.at(maxX, y));
        }
        return tilesCreated;
    }

    /**
     * Creates a concentrated hotspot of {@link ToxicWaste} around the monitor.
     *
     * Only empty tiles within Manhattan distance 2 of the atmospheric anchor
     * may be corrupted. Each eligible tile has a 50 percent chance to change.
     * The returned count only includes tiles that actually became toxic waste
     * during this scan.
     *
     * @param map the map in which to apply the hotspot effect
     * @param anchorLocation the current location of the atmospheric anchor
     * @return the number of hotspot tiles newly converted into toxic waste
     */
    private int createAnchorHotspot(GameMap map, Location anchorLocation) {
        if (anchorLocation == null) {
            return 0;
        }

        int centreX = anchorLocation.x();
        int centreY = anchorLocation.y();
        int tilesCreated = 0;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                int manhattan = Math.abs(x - centreX) + Math.abs(y - centreY);
                if (manhattan > HOTSPOT_RADIUS) {
                    continue;
                }

                Location here = map.at(x, y);
                if (here.containsAnActor()) {
                    continue;
                }

                if (random.nextBoolean()) {
                    tilesCreated += corruptIfNeeded(here);
                }
            }
        }
        return tilesCreated;
    }

    /**
     * Converts a location into toxic waste only when it is not already toxic.
     *
     * This version avoids type checks and instead compares the ground display
     * character directly with the Toxic Waste symbol.
     *
     * @param location the location to mutate
     * @return 1 if the tile was newly corrupted, otherwise 0
     */
    private int corruptIfNeeded(Location location) {
        if (location.getGround().getDisplayChar() == '≈') {
            return 0;
        }
        location.setGround(new ToxicWaste());
        return 1;
    }
}
