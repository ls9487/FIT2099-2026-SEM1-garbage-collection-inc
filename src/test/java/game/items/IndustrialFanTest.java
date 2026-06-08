package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IndustrialFan (REQ1).
 * Tests sell price, deposit value, item properties,
 * and that it implements both Sellable and Depositable.
 *
 * @author eche0116
 */
class IndustrialFanTest {

    /**
     * Sell price must be 150. Three instances checked.
     */
    @Test
    void getSellPrice_typicalCondition_returns150() {
        assertEquals(150, new IndustrialFan().getSellPrice());
        assertEquals(150, new IndustrialFan().getSellPrice());
        assertEquals(150, new IndustrialFan().getSellPrice());
    }

    /**
     * Deposit value must be 10. Three instances checked.
     */
    @Test
    void getDepositValue_typicalCondition_returns10() {
        assertEquals(10, new IndustrialFan().getDepositValue());
        assertEquals(10, new IndustrialFan().getDepositValue());
        assertEquals(10, new IndustrialFan().getDepositValue());
    }

    /**
     * Sell price and deposit value must be positive and not equal to each other.
     * Three checks: sell > 0, deposit > 0, sell != deposit.
     */
    @Test
    void prices_boundaryCondition_positiveAndDistinct() {
        IndustrialFan fan = new IndustrialFan();
        assertTrue(fan.getSellPrice() > 0);
        assertTrue(fan.getDepositValue() > 0);
        assertNotEquals(fan.getSellPrice(), fan.getDepositValue());
    }

    /**
     * IndustrialFan must be both Sellable and Depositable.
     * Three checks: Sellable present, Depositable present, neither is empty.
     */
    @Test
    void capabilities_typicalCondition_isSellableAndDepositable() {
        IndustrialFan fan = new IndustrialFan();
        assertFalse(fan.asCapability(Sellable.class).isEmpty());
        assertFalse(fan.asCapability(Depositable.class).isEmpty());
        // Both must resolve to the same object
        assertEquals(fan, fan.asCapability(Sellable.class).get());
    }
}