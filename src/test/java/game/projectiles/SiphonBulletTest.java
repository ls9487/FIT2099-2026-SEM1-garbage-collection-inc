package game.projectiles;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.inventories.BasicInventory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for SiphonBullet.
 * Includes tests for hitting a location.
 *
 * @author echu0057
 */
class SiphonBulletTest {

    /**
     * Dummy actor to be used in these tests.
     * This is used because PLAYER ability may be checked, but since Actor's hasAbility
     * is final, Mockito cannot override it for testing, so when() can't be used.
     */
    private static class TestActor extends Actor
    {
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
     * Tests if the SiphonBullet hits a target directly for 3 damage, and heals the source.
     */
    @Test
    public void siphonBulletDirectHit() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock location for the enemy.
        Location targetLocation = mock(Location.class);
        // Create the testing dummies (source and enemy).
        Actor source = new TestActor();
        source.hurt(5); // Source is injured (5/10 hp) for this test!
        Actor enemy = new TestActor();
        // Create the actual projectile.
        SiphonBullet siphonBullet = new SiphonBullet(targetLocation, source);

        // Enemy location contains the dummy.
        when(targetLocation.containsAnActor()).thenReturn(true);
        when(targetLocation.getActor()).thenReturn(enemy);
        // No adjacent locations for this test.
        when(targetLocation.getNearbyLocations(1)).thenReturn(new ArrayList<>());

        // ACT: Activate the hit effect of the projectile here.
        String result = siphonBullet.onHitEffect(targetLocation);

        // ASSERT: Ensure the projectile did hit the target, according to the string.
        // The dummies' health should also have changed accordingly...
        assertTrue(result.contains("dealing a total of 3 damage, which healed Dummy"));
        assertEquals(8, source.getStatistic(ActorStatistics.HEALTH));
        assertEquals(7, enemy.getStatistic(ActorStatistics.HEALTH));
    }

    /**
     * Tests if the SiphonBullet hits two targets indirectly for 1 damage each.
     * Since the total damage dealt was 2, it should also heal the source by 2 hp.
     */
    @Test
    public void siphonBulletIndirectHits() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock locations for the enemies.
        Location targetLocation = mock(Location.class);
        Location enemyLocation1 = mock(Location.class);
        Location enemyLocation2 = mock(Location.class);
        // Create the testing dummies (source and 2 enemies).
        Actor source = new TestActor();
        source.hurt(5); // Source is injured (5/10 hp) for this test!
        Actor enemy1 = new TestActor();
        Actor enemy2 = new TestActor();
        // Create the actual projectile.
        SiphonBullet siphonBullet = new SiphonBullet(targetLocation, source);

        // Target location contains no one...
        when(targetLocation.containsAnActor()).thenReturn(false);
        // But the enemy locations do...
        when(enemyLocation1.containsAnActor()).thenReturn(true);
        when(enemyLocation1.getActor()).thenReturn(enemy1);
        when(enemyLocation2.containsAnActor()).thenReturn(true);
        when(enemyLocation2.getActor()).thenReturn(enemy2);

        // Adjacent locations contain the enemies.
        List<Location> enemyLocationList = new ArrayList<>();
        enemyLocationList.add(enemyLocation1);
        enemyLocationList.add(enemyLocation2);
        when(targetLocation.getNearbyLocations(1)).thenReturn(enemyLocationList);

        // ACT: Activate the hit effect of the projectile here.
        String result = siphonBullet.onHitEffect(targetLocation);

        // ASSERT: Ensure the projectile did hit the targets, according to the string.
        assertTrue(result.contains("dealing a total of 2 damage, which healed Dummy"));
        // Source was healed 2 hp, each enemy took 1 damage each.
        assertEquals(7, source.getStatistic(ActorStatistics.HEALTH));
        assertEquals(9, enemy1.getStatistic(ActorStatistics.HEALTH));
        assertEquals(9, enemy2.getStatistic(ActorStatistics.HEALTH));
    }

    /**
     * Tests if the SiphonBullet hits a target directly for 3 damage, even without a source.
     * This is TECHNICALLY allowed... it just won't heal anyone.
     */
    @Test
    public void siphonBulletDirectHitWithoutSource() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock location for the enemy.
        Location targetLocation = mock(Location.class);
        // Create the testing enemy dummy.
        Actor enemy = new TestActor();
        // Create the actual projectile (no source).
        SiphonBullet siphonBullet = new SiphonBullet(targetLocation, null);

        // Enemy location contains the dummy.
        when(targetLocation.containsAnActor()).thenReturn(true);
        when(targetLocation.getActor()).thenReturn(enemy);
        // No adjacent locations for this test.
        when(targetLocation.getNearbyLocations(1)).thenReturn(new ArrayList<>());

        // ACT: Activate the hit effect of the projectile here.
        String result = siphonBullet.onHitEffect(targetLocation);

        // ASSERT: Ensure the projectile did hit the target, according to the string.
        // The dummies' health should also have changed accordingly...
        assertTrue(result.contains("dealing a total of 3 damage, but it couldn't heal..."));
        assertEquals(7, enemy.getStatistic(ActorStatistics.HEALTH));
    }

}