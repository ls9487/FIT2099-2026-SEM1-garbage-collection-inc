package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MechSuit} tick fire spread and overclock explosion behaviour.
 *
 * @author lyan0121
 * @version 1.0
 */
class MechSuitTest {

    /**
     * Tests that ticking the mech suit places fire on adjacent locations.
     */
    @Test
    void tick_NormalCondition_IgnitesAdjacentTiles() {
        MechSuit mechSuit = new MechSuit();
        Actor rider = mock(Actor.class);
        Location currentLocation = mock(Location.class);

        Location adjacent = mock(Location.class);
        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacent));

        mechSuit.tick(currentLocation, rider);

        verify(adjacent).addItem(any());
    }

    /**
     * Tests that overclock caps sacrificed health at five hit points for high-health riders.
     */
    @Test
    void overclock_BoundaryCondition_CapsPowerSacrificeAtMaximumThreshold() {
        MechSuit mechSuit = new MechSuit();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location currentLocation = mock(Location.class);

        when(rider.getStatistic(ActorStatistics.HEALTH)).thenReturn(100);
        when(gameMap.locationOf(rider)).thenReturn(currentLocation);
        when(currentLocation.getNearbyLocations(5)).thenReturn(new ArrayList<>());

        String message = mechSuit.overclock(rider, gameMap);

        verify(rider).hurt(5);
        assertTrue(message.contains("exploded 5 radius areas"));
    }

    /**
     * Tests that overclock consumes only the rider's remaining health when below the five-point cap.
     */
    @Test
    void overclock_EdgeCondition_ConsumesExactRemainingHealthPointsIfLow() {
        MechSuit mechSuit = new MechSuit();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location currentLocation = mock(Location.class);

        when(rider.getStatistic(ActorStatistics.HEALTH)).thenReturn(3);
        when(gameMap.locationOf(rider)).thenReturn(currentLocation);
        when(currentLocation.getNearbyLocations(3)).thenReturn(new ArrayList<>());

        String message = mechSuit.overclock(rider, gameMap);

        verify(rider).hurt(3);
        assertTrue(message.contains("exploded 3 radius areas"));
    }
}
