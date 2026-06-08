package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlasmaCutter (REQ1).
 * Tests buy price, CUTTER ability, item properties,
 * and that it is Buyable but NOT Sellable.
 *
 * @author eche0116
 */
class PlasmaCutterTest {

    /**
     * Buy price must be 50. Three instances checked.
     */
    @Test
    void getBuyPrice_typicalCondition_returns50() {
        assertEquals(50, new PlasmaCutter().getBuyPrice());
        assertEquals(50, new PlasmaCutter().getBuyPrice());
        assertEquals(50, new PlasmaCutter().getBuyPrice());
    }


    /**
     * PlasmaCutter must grant the CUTTER ability needed by CutAction.
     * Three instances checked.
     */
    @Test
    void cutterAbility_typicalCondition_abilityIsEnabled() {
        assertTrue(new PlasmaCutter().hasAbility(ItemAbilities.CUTTER));
        assertTrue(new PlasmaCutter().hasAbility(ItemAbilities.CUTTER));
        assertTrue(new PlasmaCutter().hasAbility(ItemAbilities.CUTTER));
    }

    /**
     * PlasmaCutter cannot be sold back. asCapability(Sellable) must be empty.
     * Three instances checked.
     */
    @Test
    void sellable_negativeCondition_isNotSellable() {
        assertTrue(new PlasmaCutter().asCapability(Sellable.class).isEmpty());
        assertTrue(new PlasmaCutter().asCapability(Sellable.class).isEmpty());
        assertTrue(new PlasmaCutter().asCapability(Sellable.class).isEmpty());
    }

    /**
     * Buy price must be exactly 50, boundary check against adjacent values.
     * Three checks: equals 50, not 49, not 51.
     */
    @Test
    void getBuyPrice_boundaryCondition_exactlyFifty() {
        PlasmaCutter cutter = new PlasmaCutter();
        assertEquals(50, cutter.getBuyPrice());
        assertNotEquals(49, cutter.getBuyPrice());
        assertNotEquals(51, cutter.getBuyPrice());
    }
}