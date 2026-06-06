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

class MechSuitTest {

    @Test
    void tick_NormalCondition_IgnitesAdjacentTiles() {
        MechSuit mechSuit = new MechSuit();
        Actor rider = mock(Actor.class);
        Location currentLocation = mock(Location.class);

        // Case 1: Surrounding cells are set to fire tracking turn actions
        Location adjacent = mock(Location.class);
        when(currentLocation.getNearbyLocations(1)).thenReturn(List.of(adjacent));

        mechSuit.tick(currentLocation, rider);

        // Assert fire is dropped into the surrounding cell engine structure
        verify(adjacent).addItem(any());
    }

    @Test
    void overclock_BoundaryCondition_CapsPowerSacrificeAtMaximumThreshold() {
        MechSuit mechSuit = new MechSuit();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location currentLocation = mock(Location.class);

        // Case 2: High health actor tries to over-use energy beyond cap threshold values
        when(rider.getStatistic(ActorStatistics.HEALTH)).thenReturn(100);
        when(gameMap.locationOf(rider)).thenReturn(currentLocation);
        when(currentLocation.getNearbyLocations(5)).thenReturn(new ArrayList<>());

        String message = mechSuit.overclock(rider, gameMap);

        // Verify damage calculation logic caps at maximum configured used power (5 HP)
        verify(rider).hurt(5);
        assertTrue(message.contains("exploded 5 radius areas"));
    }

    @Test
    void overclock_EdgeCondition_ConsumesExactRemainingHealthPointsIfLow() {
        MechSuit mechSuit = new MechSuit();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location currentLocation = mock(Location.class);

        // Case 3: Extreme scenario where health condition matches or sits below threshold limit
        when(rider.getStatistic(ActorStatistics.HEALTH)).thenReturn(3);
        when(gameMap.locationOf(rider)).thenReturn(currentLocation);
        when(currentLocation.getNearbyLocations(3)).thenReturn(new ArrayList<>());

        String message = mechSuit.overclock(rider, gameMap);

        // Verify behavior scales dynamically matching safety bounds
        verify(rider).hurt(3);
        assertTrue(message.contains("exploded 3 radius areas"));
    }
}