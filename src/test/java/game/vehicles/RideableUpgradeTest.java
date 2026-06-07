package game.vehicles;

import edu.monash.fit2099.engine.items.ItemAbility;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RideableUpgrade} portable and non-portable state transitions.
 *
 * @author lyan0121
 * @version 1.0
 */
class RideableUpgradeTest {

    private static class StubRideableUpgrade extends RideableUpgrade {
        public StubRideableUpgrade(String name, char displayChar, int weight) {
            super(name, displayChar, weight);
        }
    }

    /**
     * Tests that {@link RideableUpgrade#makePortable()} and {@link RideableUpgrade#makeNonPortable()}
     * toggle the {@link ItemAbility#PORTABLE} ability.
     */
    @Test
    void portability_NormalCondition_AllowsPickingUpAndDropping() {
        StubRideableUpgrade upgrade = new StubRideableUpgrade("Test Upgrade", 'U', 10);

        upgrade.makePortable();
        assertTrue(upgrade.hasAbility(ItemAbility.PORTABLE));

        upgrade.makeNonPortable();
        assertFalse(upgrade.hasAbility(ItemAbility.PORTABLE));
    }

    /**
     * Tests that repeated portable and non-portable calls do not corrupt ability state.
     */
    @Test
    void portability_BoundaryCondition_RedundantCallsDoNotStateFlip() {
        StubRideableUpgrade upgrade = new StubRideableUpgrade("Test Upgrade", 'U', 10);

        upgrade.makePortable();
        upgrade.makePortable();
        assertTrue(upgrade.hasAbility(ItemAbility.PORTABLE));

        upgrade.makeNonPortable();
        upgrade.makeNonPortable();
        assertFalse(upgrade.hasAbility(ItemAbility.PORTABLE));
    }

    /**
     * Tests that querying an unregistered vehicle ability returns false without error.
     */
    @Test
    void portability_NegativeCondition_NullCheckCapabilitySearch() {
        StubRideableUpgrade upgrade = new StubRideableUpgrade("Test Upgrade", 'U', 10);

        assertFalse(upgrade.hasAbility(VehicleAbilities.EXTRA_ENERGY));
    }
}
