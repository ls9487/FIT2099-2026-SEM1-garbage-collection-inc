package game.projectiles;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ActorAbilities;
import game.inventories.BasicInventory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for FireBullet (REQ 4).
 * Includes tests for hitting a location.
 *
 * @author echu0057
 */
class FireBulletTest {

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
     * Tests whether the actor, standing where the projectile hit, gets hurt.
     * Does not test for the random fire effect since it's random.
     */
    @Test
    public void fireBulletHits() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock map for the target.
        GameMap targetMap = mock(GameMap.class);
        // We'll have to use an actual location here (getActorAs is final in the engine, and
        // will crash if called on a mock location, which FireBullet does...)
        Location targetLocation = new Location(targetMap, 0, 0);
        // Create the testing dummy.
        Actor target = new TestActor();
        // Create the actual projectile.
        FireBullet fireBullet = new FireBullet(targetLocation);

        // Target location contains the dummy (the mock map says so).
        when(targetMap.isAnActorAt(targetLocation)).thenReturn(true);
        when(targetMap.getActorAt(targetLocation)).thenReturn(target);

        // ACT: Activate the hit effect of the projectile here.
        String result = fireBullet.onHitEffect(targetLocation);

        // ASSERT: Ensure the projectile did hit the target, according to the string.
        // FireBullets also have a direct damage of 2.
        assertTrue(result.contains("Fire Bullet hits Dummy"));
        assertEquals(8, target.getStatistic(ActorStatistics.HEALTH));
    }

    /**
     * Tests whether the player actor, standing where the projectile hit, gets hurt.
     * Reminder that while turrets do care about not targeting players, projectiles don't.
     * Does not test for the random fire effect since it's random.
     */
    @Test
    public void fireBulletHitsPlayer() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // Create the mock map for the target.
        GameMap targetMap = mock(GameMap.class);
        Location targetLocation = new Location(targetMap, 0, 0);
        // Create the testing dummy. This one's a player!
        Actor target = new TestActor();
        target.enableAbility(ActorAbilities.PLAYER);
        // Create the actual projectile.
        FireBullet fireBullet = new FireBullet(targetLocation);

        // Target location contains the dummy (the mock map says so).
        when(targetMap.isAnActorAt(targetLocation)).thenReturn(true);
        when(targetMap.getActorAt(targetLocation)).thenReturn(target);

        // ACT: Activate the hit effect of the projectile here.
        String result = fireBullet.onHitEffect(targetLocation);

        // ASSERT: Ensure the projectile did hit the target, according to the string.
        // FireBullets also have a direct damage of 2.
        assertTrue(result.contains("Fire Bullet hits Dummy"));
        assertEquals(8, target.getStatistic(ActorStatistics.HEALTH));
    }

    /**
     * Tests when the FireBullet misses (hits nobody at the location).
     * Ensures that it handles hitting nothing without crashing.
     */
    @Test
    public void fireBulletMisses() {
        // ARRANGE: Set up the projectile, necessary mocks and what they return for this test.
        // No target dummy this time.
        // Create the mock map for the target.
        GameMap targetMap = mock(GameMap.class);
        Location targetLocation = new Location(targetMap, 0, 0);
        // Create the actual projectile.
        FireBullet fireBullet = new FireBullet(targetLocation);

        // ACT: Activate the hit effect of the projectile here.
        String result = fireBullet.onHitEffect(targetLocation);

        // ASSERT: Ensure the projectile handled hitting nothing gracefully.
        assertTrue(result.contains("Fire Bullet didn't hit anyone"));
    }

}