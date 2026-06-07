package game.vehicles;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WarpBattery} teleportation, knockback, and occupant handling.
 *
 * @author lyan0121
 * @version 1.0
 */
class WarpBatteryTest {

    /**
     * Tests that teleporting to an empty destination moves the rider and reports success.
     */
    @Test
    void teleport_NormalCondition_MovesRiderAndAppliesBasePoison() {
        WarpBattery warpBattery = new WarpBattery();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location destination = mock(Location.class);

        when(destination.map()).thenReturn(gameMap);
        when(destination.containsAnActor()).thenReturn(false);

        String result = warpBattery.teleport(rider, gameMap, destination);

        verify(gameMap).moveActor(eq(rider), eq(destination));
        assertTrue(result.contains("teleported"));
    }

    /**
     * Tests that an occupied destination knocks the occupant to an adjacent tile.
     */
    @Test
    void teleport_BoundaryCondition_KnocksBackOccupantWhenTargetOccupied() {
        WarpBattery warpBattery = new WarpBattery();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location destination = mock(Location.class);
        Actor occupant = mock(Actor.class);

        when(destination.map()).thenReturn(gameMap);
        when(destination.containsAnActor()).thenReturn(true);
        when(destination.getActor()).thenReturn(occupant);

        Location startLocation = mock(Location.class);
        when(gameMap.locationOf(rider)).thenReturn(startLocation);
        when(startLocation.x()).thenReturn(0);
        when(startLocation.y()).thenReturn(0);
        when(destination.x()).thenReturn(1);
        when(destination.y()).thenReturn(0);

        NumberRange numberRangeX = new NumberRange(0, 10);
        NumberRange numberRangeY = new NumberRange(0, 10);
        when(gameMap.getXRange()).thenReturn(numberRangeX);
        when(gameMap.getYRange()).thenReturn(numberRangeY);

        Location knockbackLocation = mock(Location.class);
        when(gameMap.at(2, 0)).thenReturn(knockbackLocation);
        when(knockbackLocation.canActorEnter(occupant)).thenReturn(true);

        String result = warpBattery.teleport(rider, gameMap, destination);

        verify(gameMap).moveActor(eq(occupant), eq(knockbackLocation));
        assertTrue(result.contains("toxicity"));
    }

    /**
     * Tests that the occupant is sent unconscious when knockback to an adjacent tile is blocked.
     */
    @Test
    void teleport_EdgeCondition_WarpsOccupantToHellIfKnockbackBlocked() {
        WarpBattery warpBattery = new WarpBattery();
        Actor rider = mock(Actor.class);
        GameMap gameMap = mock(GameMap.class);
        Location destination = mock(Location.class);
        Actor occupant = mock(Actor.class);

        when(destination.map()).thenReturn(gameMap);
        when(destination.containsAnActor()).thenReturn(true);
        when(destination.getActor()).thenReturn(occupant);

        Location startLocation = mock(Location.class);
        when(gameMap.locationOf(rider)).thenReturn(startLocation);
        when(startLocation.x()).thenReturn(0);
        when(startLocation.y()).thenReturn(0);
        when(destination.x()).thenReturn(1);
        when(destination.y()).thenReturn(0);

        NumberRange numberRangeX = new NumberRange(0, 10);
        NumberRange numberRangeY = new NumberRange(0, 10);
        when(gameMap.getXRange()).thenReturn(numberRangeX);
        when(gameMap.getYRange()).thenReturn(numberRangeY);

        Location knockbackLocation = mock(Location.class);
        when(gameMap.at(2, 0)).thenReturn(knockbackLocation);
        when(knockbackLocation.canActorEnter(occupant)).thenReturn(false);

        String result = warpBattery.teleport(rider, gameMap, destination);

        verify(occupant).unconscious(gameMap);
        assertTrue(result.contains("warp") || result.contains("hell"));
    }
}
