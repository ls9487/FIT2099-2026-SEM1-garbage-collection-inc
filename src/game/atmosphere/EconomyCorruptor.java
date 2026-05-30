package game.atmosphere;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.EclipseStatistics;

/**
 * Atmospheric corruptor that models economic disruption caused by SO2.
 * <p>
 * When sulphur dioxide is the dominant pollutant, this class reduces the
 * Eclipse credits of actors on the map to represent damaged trade and
 * equipment reliability.
 * </p>
 */
public class EconomyCorruptor implements AtmosphericCorruptor {

    public static boolean ECONOMY_DISRUPTED = false;

    private static final int CREDIT_PENALTY = 10;

    @Override
    public void corrupt(GameMap map, AirQualityReport report) {
        String dominant = report.getDominantPollutant();

        // Economy disruption only happens when sulphur dioxide dominates.
        ECONOMY_DISRUPTED = "so2".equalsIgnoreCase(dominant);
        if (!ECONOMY_DISRUPTED) {
            return;
        }

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location here = map.at(x, y);
                Actor actor = here.getActor();
                if (actor == null) {
                    continue;
                }

                // Only actors that actually track credits are affected.
                if (!actor.hasStatistic(EclipseStatistics.CREDITS)) {
                    continue;
                }

                int credits = actor.getStatistic(EclipseStatistics.CREDITS);
                int newCredits = Math.max(0, credits - CREDIT_PENALTY);
                actor.modifyStatistic(EclipseStatistics.CREDITS,
                        StatisticOperations.UPDATE,
                        newCredits);
                System.out.println("[Economy Disrupted] SO2 toxins corrode " + actor
                        + "'s equipment : loses " + CREDIT_PENALTY + " credits! (now: "
                        + newCredits + ")");
            }
        }
    }
}
