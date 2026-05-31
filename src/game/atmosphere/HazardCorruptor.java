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
 * implementations and creates small localised toxic spills. At severe
 * pollution levels, it additionally corrupts large regions of the map,
 * including the outer border and an area surrounding the atmospheric anchor.
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
    private static final int LOCAL_WASTE_SPREAD_CHANCE_DIVISOR = 4;
    private static final int LOCAL_WASTE_SPREAD_TRIGGER = 0;
    private static final int HOTSPOT_RADIUS = 2;

    private final Random random = new Random();

    /**
     * Applies AQI-driven hazard effects to the provided map.
     *
     * Safe or mild pollution causes no change. Moderate pollution applies
     * actor-specific atmospheric reactions and may create local toxic puddles.
     * Severe pollution further mutates the wider environment by surrounding the
     * map border with {@link ToxicWaste} and creating a pollution hotspot near
     * an atmospheric anchor.
     *
     * @param map the game map to corrupt
     * @param report the air quality report describing the current atmospheric state
     */
    @Override
    public void corrupt(GameMap map, AirQualityReport report) {
        int aqi = report.getAqi();

        if (aqi <= SAFE_AQI_THRESHOLD) {
            return;
        }

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location location = map.at(x, y);
                Actor actor = location.getActor();
                if (actor == null) {
                    continue;
                }

                actor.asCapability(AtmosphereSensitiveActor.class).ifPresent(sensitive -> {
                    sensitive.applyAtmosphere(report, location);

                    if (aqi == MODERATE_AQI) {
                        spreadLocalWaste(location);
                    }
                });
            }
        }

        if (aqi >= SEVERE_AQI_THRESHOLD) {
            createBorderWasteRing(map);
            createAnchorHotspot(map);
        }
    }

    /**
     * Randomly spreads small pockets of {@link ToxicWaste} around a centre
     * location to simulate local contamination.
     *
     * Only empty adjacent tiles may be corrupted.
     *
     * @param centre the source location around which waste may spread
     */
    private void spreadLocalWaste(Location centre) {
        for (Exit exit : centre.getExits()) {
            Location dest = exit.getDestination();
            if (dest.containsAnActor()) {
                continue;
            }

            if (random.nextInt(LOCAL_WASTE_SPREAD_CHANCE_DIVISOR) == LOCAL_WASTE_SPREAD_TRIGGER) {
                dest.setGround(new ToxicWaste());
            }
        }
    }

    /**
     * Corrupts the outer border of the map with {@link ToxicWaste} to represent
     * severe, persistent atmospheric contamination.
     *
     * @param map the map whose perimeter should be mutated
     */
    private void createBorderWasteRing(GameMap map) {
        int minX = map.getXRange().min();
        int maxX = map.getXRange().max();
        int minY = map.getYRange().min();
        int maxY = map.getYRange().max();

        for (int x = minX; x <= maxX; x++) {
            map.at(x, minY).setGround(new ToxicWaste());
            map.at(x, maxY).setGround(new ToxicWaste());
        }

        for (int y = minY; y <= maxY; y++) {
            map.at(minX, y).setGround(new ToxicWaste());
            map.at(maxX, y).setGround(new ToxicWaste());
        }
    }

    /**
     * Locates a ground marked as an {@link AtmosphericAnchor} and creates a
     * concentrated hotspot of {@link ToxicWaste} within Manhattan distance 2.
     *
     * Only empty tiles may be corrupted. If no atmospheric anchor exists on the
     * map, no hotspot is created.
     *
     * @param map the map in which to search for the atmospheric anchor
     */
    private void createAnchorHotspot(GameMap map) {
        Location anchorLocation = null;

        outer:
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location here = map.at(x, y);
                if (here.getGroundAs(AtmosphericAnchor.class) != null) {
                    anchorLocation = here;
                    break outer;
                }
            }
        }

        if (anchorLocation == null) {
            return;
        }

        int centreX = anchorLocation.x();
        int centreY = anchorLocation.y();

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
                    here.setGround(new ToxicWaste());
                }
            }
        }
    }
}
