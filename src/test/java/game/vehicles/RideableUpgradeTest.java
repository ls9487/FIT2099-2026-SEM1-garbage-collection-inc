package game.vehicles;

import edu.monash.fit2099.engine.items.ItemAbility;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RideableUpgradeTest {

    private static class StubRideableUpgrade extends RideableUpgrade {
        public StubRideableUpgrade(String name, char displayChar, int weight) {
            super(name, displayChar, weight);
        }
    }

    @Test
    void portability_NormalCondition_AllowsPickingUpAndDropping() {
        StubRideableUpgrade upgrade = new StubRideableUpgrade("Test Upgrade", 'U', 10);

        // Case 1: Initial state can explicitly make portable
        upgrade.makePortable();
        assertTrue(upgrade.hasAbility(ItemAbility.PORTABLE));

        // Case 2: Changing to non-portable removes it cleanly
        upgrade.makeNonPortable();
        assertFalse(upgrade.hasAbility(ItemAbility.PORTABLE));
    }

    @Test
    void portability_BoundaryCondition_RedundantCallsDoNotStateFlip() {
        StubRideableUpgrade upgrade = new StubRideableUpgrade("Test Upgrade", 'U', 10);

        // Case 3: Calling makePortable multiple times keeps it portable without double-adding
        upgrade.makePortable();
        upgrade.makePortable();
        assertTrue(upgrade.hasAbility(ItemAbility.PORTABLE));

        // Case 4: Calling makeNonPortable multiple times handles clean state restriction
        upgrade.makeNonPortable();
        upgrade.makeNonPortable();
        assertFalse(upgrade.hasAbility(ItemAbility.PORTABLE));
    }

    @Test
    void portability_NegativeCondition_NullCheckCapabilitySearch() {
        StubRideableUpgrade upgrade = new StubRideableUpgrade("Test Upgrade", 'U', 10);

        // Case 5: Ensure custom capability system handles missing capability cleanly without exception
        assertFalse(upgrade.hasAbility(VehicleAbilities.EXTRA_ENERGY));
    }
}