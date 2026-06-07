package game.turrets;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
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
 * Unit test for GunTurret.
 * Includes targeting tests and readiness to fire.
 * Note that since all turrets currently have the same targeting system, the targeting tests
 * essentially cover Turret as a whole.
 *
 * @author echu0057
 */
class GunTurretTest {

    /**
     * Dummy actor to be used in these tests.
     * This is used because PLAYER ability is to be checked, but since Actor's hasAbility
     * is final, Mockito cannot override it for testing, so when() can't be used.
     */
    private static class TestActor extends Actor {
        private static final int TEST_HEALTH = 1;

        public TestActor() {
            super("Dummy", 'A', TEST_HEALTH, new BasicInventory());
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }

    /**
     * Tests that the GunTurret will target a non-player dummy by returning the dummy's location.
     */
    @Test
    public void gunTurretTargetsNonPlayer() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and enemy.
        Location turretLocation = mock(Location.class);
        Location enemyLocation = mock(Location.class);
        // Create the testing dummy. This won't have the PLAYER ability.
        Actor enemy = new TestActor();
        // Create the actual turret.
        Turret gunTurret = new GunTurret();

        // Enemy location contains the dummy.
        when(enemyLocation.containsAnActor()).thenReturn(true);
        when(enemyLocation.getActor()).thenReturn(enemy);
        // The nearby locations of the turret will just have this one enemy's location for this test.
        List<Location> enemyLocationList = new ArrayList<>();
        enemyLocationList.add(enemyLocation);
        when(turretLocation.getNearbyLocations(gunTurret.getStatistic(GroundStatistics.DETECTION_RADIUS)))
                .thenReturn(enemyLocationList);

        // ACT: Have the turret get a target's destination.
        Location targetLocation = gunTurret.getTargetDestination(turretLocation);

        // ASSERT: Ensure the targetLocation is not null and is the same as the enemyLocation.
        assertNotNull(targetLocation);
        assertEquals(enemyLocation, targetLocation);
    }

    /**
     * Tests that the GunTurret will NOT target a player dummy, so it won't return its location.
     */
    @Test
    public void gunTurretDoesNotTargetPlayer() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and friendly.
        Location turretLocation = mock(Location.class);
        Location friendlyLocation = mock(Location.class);
        // Create the testing dummy. This has the PLAYER ability.
        Actor friendly = new TestActor();
        friendly.enableAbility(ActorAbilities.PLAYER);
        // Create the actual turret.
        Turret gunTurret = new GunTurret();

        // Friendly location contains the dummy.
        when(friendlyLocation.containsAnActor()).thenReturn(true);
        when(friendlyLocation.getActor()).thenReturn(friendly);
        // The nearby locations will just have this one dummy's location for this test.
        List<Location> friendlyLocationList = new ArrayList<>();
        friendlyLocationList.add(friendlyLocation);
        when(turretLocation.getNearbyLocations(gunTurret.getStatistic(GroundStatistics.DETECTION_RADIUS)))
                .thenReturn(friendlyLocationList);

        // ACT: Have the turret get a target's destination.
        Location targetLocation = gunTurret.getTargetDestination(turretLocation);

        // ASSERT: Ensure the targetLocation is null since the turret wouldn't target players.
        assertNull(targetLocation);
    }

    /**
     * Tests that the GunTurret is able to fire with at least 1 ammo and cooldown waited out.
     */
    @Test
    public void gunTurretIsReady() {
        // ARRANGE: Set up the turret with 1 ammo and cooldown waited out.
        Turret gunTurret = new GunTurret();
        gunTurret.modifyStatistic(GroundStatistics.AMMUNITION, StatisticOperations.UPDATE, 1);
        gunTurret.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);

        // ACT: Check if the turret is able to fire.
        boolean ready = gunTurret.isReady();

        // ASSERT: Ensure the turret is ready to fire.
        assertTrue(ready);
    }

    /**
     * Tests that the GunTurret is not able to fire with no ammo, even if the cooldown was waited.
     */
    @Test
    public void depletedGunTurretIsNotReady() {
        // ARRANGE: Set up the turret with depleted ammo.
        Turret gunTurret = new GunTurret();
        gunTurret.modifyStatistic(GroundStatistics.AMMUNITION, StatisticOperations.UPDATE, 0);
        gunTurret.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE, 0);

        // ACT: Check if the turret is able to fire.
        boolean ready = gunTurret.isReady();

        // ASSERT: Ensure the turret is not ready to fire.
        assertFalse(ready);
    }

    /**
     * Tests that the GunTurret is not able to fire with cooldown active, even with ammo.
     */
    @Test
    public void cooldownGunTurretIsNotReady() {
        // ARRANGE: Set up the turret with 1 ammo, but cooldown not fully waited out.
        Turret gunTurret = new GunTurret();
        gunTurret.modifyStatistic(GroundStatistics.AMMUNITION, StatisticOperations.UPDATE, 1);
        gunTurret.modifyStatistic(GroundStatistics.COOLDOWN, StatisticOperations.UPDATE, 1);

        // ACT: Check if the turret is able to fire.
        boolean ready = gunTurret.isReady();

        // ASSERT: Ensure the turret is not ready to fire.
        assertFalse(ready);
    }

}