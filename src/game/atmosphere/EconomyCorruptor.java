package game.atmosphere;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.EclipseStatistics;
import game.grounds.SuperComputer;

/**
 * Atmospheric corruptor that models economic disruption caused by SO2.
 * <p>
 * When sulphur dioxide is the dominant pollutant, this class reduces the
 * Eclipse credits of actors on the map to represent damaged trade and
 * equipment reliability. It also updates the super computer state so selling
 * prices can react to the current atmosphere event.
 * </p>
 *
 * @author esoo0013
 */
public class EconomyCorruptor implements AtmosphericCorruptor {

    private static final int CREDIT_PENALTY = 10;

    /**
     * Applies the economy corruption effect for the current scan result.
     *
     * @param map the map being affected
     * @param report the air quality report for this scan
     * @param anchorLocation the monitor location that triggered the scan
     */
    @Override
    public void corrupt(GameMap map, AirQualityReport report, Location anchorLocation) {
        String dominant = report.getDominantPollutant();
        boolean disrupted = "so2".equalsIgnoreCase(dominant);

        SuperComputer.setEconomyDisrupted(disrupted);
        if (!disrupted) {
            return;
        }

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location here = map.at(x, y);
                Actor actor = here.getActor();
                if (actor == null) {
                    continue;
                }

                if (actor.hasStatistic(EclipseStatistics.CREDITS)) {
                    int currentCredits = actor.getStatistic(EclipseStatistics.CREDITS);
                    int deduction = Math.min(CREDIT_PENALTY, currentCredits);
                    if (deduction > 0) {
                        actor.modifyStatistic(EclipseStatistics.CREDITS, StatisticOperations.DECREASE, deduction);
                    }
                }
            }
        }
    }
}
