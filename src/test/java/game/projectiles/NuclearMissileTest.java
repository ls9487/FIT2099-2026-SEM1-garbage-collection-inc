package game.projectiles;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;
import game.inventories.BasicInventory;
import game.items.Fire;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for NuclearMissile (REQ 4).
 * Includes tests for hitting a location, impacting actors and terrain.
 *
 * @author echu0057
 */
class NuclearMissileTest {

    /**
     * Dummy actor to be used in these tests. Superbuffed because missiles hurt.
     * This is used because PLAYER ability is to be checked, but since Actor's hasAbility
     * is final, Mockito cannot override it for testing, so when() can't be used.
     */
    private static class TestActor extends Actor {
        private static final int TEST_HEALTH = 1000;

        public TestActor() {
            super("Tank", 'A', TEST_HEALTH, new BasicInventory());
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return null;
        }
    }

    /**
     * Tests if the NuclearMissile hits a target directly for 500 damage and modifies the terrain
     * (fire and toxic waste appears) there.
     */
    @Test
    public void missileDirectHit() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock location for the enemy.
        Location targetLocation = mock(Location.class);
        // Create the testing dummy.
        Actor enemy = new TestActor();
        // Create the missile (blast radius of 4).
        final int test_radius = 4;
        NuclearMissile missile = new NuclearMissile(targetLocation, test_radius);

        // Enemy location contains the dummy.
        when(targetLocation.containsAnActor()).thenReturn(true);
        when(targetLocation.getActor()).thenReturn(enemy);
        // No nearby locations for this test.
        when(targetLocation.getNearbyLocations(test_radius)).thenReturn(new ArrayList<>());

        // ACT: Activate the hit effect of the projectile here.
        missile.onHitEffect(targetLocation);

        // ASSERT: Ensure the projectile did hit the target, by checking the enemy's health.
        // Also, a fire (item) and toxic waste (ground) should have appeared at the impact site.
        assertEquals(500, enemy.getStatistic(ActorStatistics.HEALTH));
        verify(targetLocation).addItem(any(Fire.class));
        verify(targetLocation).setGround(any(ToxicWaste.class));
    }

    /**
     * Tests if the NuclearMissile indirectly hits multiple targets for 5 damage each.
     * Also, fires should spawn on where the targets have been hit too.
     * Does not test for toxic waste since it's random.
     */
    @Test
    public void missileIndirectHit() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock location for the enemy.
        Location targetLocation = mock(Location.class);
        Location enemyLocation1 = mock(Location.class);
        Location enemyLocation2 = mock(Location.class);
        // Create the testing dummies.
        Actor enemy1 = new TestActor();
        Actor enemy2 = new TestActor();
        // Create the missile (blast radius of 4).
        final int test_radius = 4;
        NuclearMissile missile = new NuclearMissile(targetLocation, test_radius);

        // Target location doesn't have anyone...
        when(targetLocation.containsAnActor()).thenReturn(false);
        // But enemies are in the blast zone...
        when(enemyLocation1.containsAnActor()).thenReturn(true);
        when(enemyLocation1.getActor()).thenReturn(enemy1);
        when(enemyLocation2.containsAnActor()).thenReturn(true);
        when(enemyLocation2.getActor()).thenReturn(enemy2);
        // Adjacent locations contain the enemies.
        List<Location> enemyLocationList = new ArrayList<>();
        enemyLocationList.add(enemyLocation1);
        enemyLocationList.add(enemyLocation2);
        when(targetLocation.getNearbyLocations(test_radius)).thenReturn(enemyLocationList);

        // ACT: Activate the hit effect of the projectile here.
        missile.onHitEffect(targetLocation);

        // ASSERT: Ensure the targets have been hit for 5 damage (indirect).
        // Also, a fire (item) should have appeared on each one enemy locations...
        assertEquals(995, enemy1.getStatistic(ActorStatistics.HEALTH));
        assertEquals(995, enemy2.getStatistic(ActorStatistics.HEALTH));
        verify(enemyLocation1).addItem(any(Fire.class));
        verify(enemyLocation2).addItem(any(Fire.class));
    }

    /**
     * Tests if the NuclearMissile, even if no actors were present in the blast,
     * still spawns fires and toxic waste when it hits a location.
     * Does not test for toxic waste for adjacent locations in the blast since it's random.
     */
    @Test
    public void missileMisses() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock locations.
        Location targetLocation = mock(Location.class);
        Location secondaryLocation = mock(Location.class);
        // No dummies for this test.
        // Create the missile (blast radius of 4).
        final int test_radius = 4;
        NuclearMissile missile = new NuclearMissile(targetLocation, test_radius);

        // These locations don't have anyone.
        when(targetLocation.containsAnActor()).thenReturn(false);
        when(secondaryLocation.containsAnActor()).thenReturn(false);

        List<Location> secondaryLocationList = new ArrayList<>();
        secondaryLocationList.add(secondaryLocation);
        when(targetLocation.getNearbyLocations(test_radius)).thenReturn(secondaryLocationList);

        // ACT: Activate the hit effect of the projectile here.
        missile.onHitEffect(targetLocation);

        // ASSERT: Even without any actors blasted, fire should've spawned on all locations hit.
        // Also, a toxic waste is guaranteed to spawn at the target location, so check for that too.
        verify(targetLocation).addItem(any(Fire.class));
        verify(secondaryLocation).addItem(any(Fire.class));
        verify(targetLocation).setGround(any(ToxicWaste.class));
    }

}