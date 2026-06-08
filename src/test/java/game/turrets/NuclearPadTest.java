package game.turrets;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
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
 * Unit test for NuclearPad (REQ 4).
 * Includes tests for arming progression.
 *
 * @author echu0057
 */
class NuclearPadTest {

    /**
     * Dummy actor to be used in these tests.
     * This is used because PLAYER ability is to be checked, but since Actor's hasAbility
     * is final, Mockito cannot override it for testing, so when() can't be used.
     */
    private static class TestActor extends Actor
    {
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
     * Tests that the turret progresses with arming when a player is adjacent to it.
     */
    @Test
    public void nuclearPadArmsWithAdjacentPlayer() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and friendly.
        Location turretLocation = mock(Location.class);
        Location friendlyLocation = mock(Location.class);
        // Create the testing dummy. This has the PLAYER ability.
        Actor friendly = new TestActor();
        friendly.enableAbility(ActorAbilities.PLAYER);
        // Create the actual turret.
        NuclearPad nuclearPad = new NuclearPad();

        // Friendly location contains the dummy.
        when(friendlyLocation.containsAnActor()).thenReturn(true);
        when(friendlyLocation.getActor()).thenReturn(friendly);
        // The nearby locations will just have this one dummy's location for this test.
        List<Location> friendlyLocationList = new ArrayList<>();
        friendlyLocationList.add(friendlyLocation);
        when(turretLocation.getNearbyLocations(1)).thenReturn(friendlyLocationList);

        // ACT: Have the turret arm.
        nuclearPad.checkArmingStatus(turretLocation);

        // ASSERT: Ensure the turret had progressed arming (-1 cooldown).
        assertEquals(nuclearPad.getMaximumStatistic(GroundStatistics.ARMING_COOLDOWN) - 1,
                nuclearPad.getStatistic(GroundStatistics.ARMING_COOLDOWN));
    }

    /**
     * Tests that the turret only progresses with arming once even with
     * multiple adjacent players around it.
     */
    @Test
    public void nuclearPadArmsOnceWithAdjacentPlayers() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and friendlies.
        Location turretLocation = mock(Location.class);
        Location friendlyLocation1 = mock(Location.class);
        Location friendlyLocation2 = mock(Location.class);
        // Create the testing dummies. These have the PLAYER ability.
        Actor friendly1 = new TestActor();
        friendly1.enableAbility(ActorAbilities.PLAYER);
        Actor friendly2 = new TestActor();
        friendly2.enableAbility(ActorAbilities.PLAYER);
        // Create the actual turret.
        NuclearPad nuclearPad = new NuclearPad();

        // Friendly locations contain the dummies.
        when(friendlyLocation1.containsAnActor()).thenReturn(true);
        when(friendlyLocation1.getActor()).thenReturn(friendly1);
        when(friendlyLocation2.containsAnActor()).thenReturn(true);
        when(friendlyLocation2.getActor()).thenReturn(friendly2);
        // The nearby locations will just have these dummies' locations for this test.
        List<Location> friendlyLocationList = new ArrayList<>();
        friendlyLocationList.add(friendlyLocation1);
        friendlyLocationList.add(friendlyLocation2);
        when(turretLocation.getNearbyLocations(1)).thenReturn(friendlyLocationList);

        // ACT: Have the turret arm.
        nuclearPad.checkArmingStatus(turretLocation);

        // ASSERT: Ensure the turret had progressed arming only once (-1 cooldown).
        // So this ensures the arming isn't accelerated by the presence of a 2nd person.
        assertEquals(nuclearPad.getMaximumStatistic(GroundStatistics.ARMING_COOLDOWN) - 1,
                nuclearPad.getStatistic(GroundStatistics.ARMING_COOLDOWN));
    }

    /**
     * Tests that the turret does not progress with arming when a non-player is adjacent to it.
     */
    @Test
    public void nuclearPadDoesNotArmWithAdjacentNonPlayer() {
        // ARRANGE: Set up the turret, necessary mocks and what they return for this test.
        // Create the mock locations for the turret and enemy.
        Location turretLocation = mock(Location.class);
        Location enemyLocation = mock(Location.class);
        // Create the testing dummy. This won't have the PLAYER ability.
        Actor enemy = new TestActor();
        // Create the actual turret.
        NuclearPad nuclearPad = new NuclearPad();

        // Enemy location contains the dummy.
        when(enemyLocation.containsAnActor()).thenReturn(true);
        when(enemyLocation.getActor()).thenReturn(enemy);
        // The nearby locations of the turret will just have this one enemy's location for this test.
        List<Location> enemyLocationList = new ArrayList<>();
        enemyLocationList.add(enemyLocation);
        when(turretLocation.getNearbyLocations(1)).thenReturn(enemyLocationList);

        // ACT: Have the turret arm.
        nuclearPad.checkArmingStatus(turretLocation);

        // ASSERT: Ensure the turret had NOT progressed arming (cooldown remains at max).
        assertEquals(nuclearPad.getMaximumStatistic(GroundStatistics.ARMING_COOLDOWN),
                nuclearPad.getStatistic(GroundStatistics.ARMING_COOLDOWN));
    }

}