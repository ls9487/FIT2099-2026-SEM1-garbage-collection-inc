package game.turrets;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.ActorAbilities;
import game.grounds.GroundStatistics;
import game.inventories.BasicInventory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for SiphonTurret (REQ 4).
 * Includes registering tests.
 *
 * @author echu0057
 */
class SiphonTurretTest {

    /**
     * Dummy actor to be used in these tests.
     * This is used because PLAYER ability is to be checked, but since Actor's hasAbility
     * is final, Mockito cannot override it for testing, so when() can't be used.
     */
    private static class TestActor extends Actor {
        private static final int TEST_HEALTH = 10;

        public TestActor() {
            super("Dummy", 'A', TEST_HEALTH, new BasicInventory());
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }

    /**
     * Tests that the SiphonTurret will register a player adjacent to it.
     * The player registered will have taken 3 damage, and the turret will be ready to fire.
     */
    @Test
    public void siphonTurretRegistersPlayer() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and friendly.
        Location turretLocation = mock(Location.class);
        Location friendlyLocation = mock(Location.class);
        // Create the testing dummy. This has the PLAYER ability.
        Actor friendly = new TestActor();
        friendly.enableAbility(ActorAbilities.PLAYER);
        // Create the actual turret, with 1 ammo and cooldown waited out.
        SiphonTurret siphonTurret = new SiphonTurret();
        siphonTurret.modifyStatistic(GroundStatistics.AMMUNITION, StatisticOperations.UPDATE, 1);
        siphonTurret.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);

        // Friendly location contains the dummy.
        when(friendlyLocation.containsAnActor()).thenReturn(true);
        when(friendlyLocation.getActor()).thenReturn(friendly);
        // The adjacent locations will just have this one dummy's location for this test.
        List<Location> friendlyLocationList = new ArrayList<>();
        friendlyLocationList.add(friendlyLocation);
        when(turretLocation.getNearbyLocations(1))
                .thenReturn(friendlyLocationList);

        // ACT: Have the turret try to register the player.
        siphonTurret.registerSurroundingActor(turretLocation);

        // ASSERT: Ensure that the friendly was registered as it took 3 damage (7 hp left).
        // Also, the turret would then be ready to fire in this test.
        assertEquals(7, friendly.getStatistic(ActorStatistics.HEALTH));
        assertTrue(siphonTurret.isReady());
    }

    /**
     * Tests that the SiphonTurret will not register a dead player adjacent to it.
     */
    @Test
    public void siphonTurretDoesNotRegisterDeadPlayer() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and friendly.
        Location turretLocation = mock(Location.class);
        Location friendlyLocation = mock(Location.class);
        // Create the testing dummy. This has the PLAYER ability, and is also very dead.
        Actor friendly = new TestActor();
        friendly.enableAbility(ActorAbilities.PLAYER);
        friendly.hurt(9999);
        // Create the actual turret, with 1 ammo and cooldown waited out.
        SiphonTurret siphonTurret = new SiphonTurret();
        siphonTurret.modifyStatistic(GroundStatistics.AMMUNITION, StatisticOperations.UPDATE, 1);
        siphonTurret.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);

        // Friendly location contains the dummy.
        when(friendlyLocation.containsAnActor()).thenReturn(true);
        when(friendlyLocation.getActor()).thenReturn(friendly);
        // The adjacent locations will just have this one dummy's location for this test.
        List<Location> friendlyLocationList = new ArrayList<>();
        friendlyLocationList.add(friendlyLocation);
        when(turretLocation.getNearbyLocations(1))
                .thenReturn(friendlyLocationList);

        // ACT: Have the turret try to register the dead player.
        siphonTurret.registerSurroundingActor(turretLocation);

        // ASSERT: No point in checking the health of a dead guy, but the turret shouldn't be ready.
        assertFalse(siphonTurret.isReady());
    }

    /**
     * Tests that the SiphonTurret will not register a non-player adjacent to it.
     */
    @Test
    public void siphonTurretDoesNotRegisterNonPlayer() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and enemy.
        Location turretLocation = mock(Location.class);
        Location enemyLocation = mock(Location.class);
        // Create the testing dummy. This does not have the PLAYER ability.
        Actor enemy = new TestActor();
        // Create the actual turret, with 1 ammo and cooldown waited out.
        SiphonTurret siphonTurret = new SiphonTurret();
        siphonTurret.modifyStatistic(GroundStatistics.AMMUNITION, StatisticOperations.UPDATE, 1);
        siphonTurret.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);

        // Enemy location contains the dummy.
        when(enemyLocation.containsAnActor()).thenReturn(true);
        when(enemyLocation.getActor()).thenReturn(enemy);
        // The adjacent locations will just have this one dummy's location for this test.
        List<Location> enemyLocationList = new ArrayList<>();
        enemyLocationList.add(enemyLocation);
        when(turretLocation.getNearbyLocations(1))
                .thenReturn(enemyLocationList);

        // ACT: Have the turret try to register the non-player.
        siphonTurret.registerSurroundingActor(turretLocation);

        // ASSERT: Ensure that the non-player was not registered, as it wouldn't take 3 damage.
        // Also, the turret wouldn't be ready to fire.
        assertEquals(10, enemy.getStatistic(ActorStatistics.HEALTH));
        assertFalse(siphonTurret.isReady());
    }

}