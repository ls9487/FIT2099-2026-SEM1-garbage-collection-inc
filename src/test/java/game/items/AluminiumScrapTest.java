package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AluminiumScrap (REQ1).
 * Tests deposit value, item properties, and that it is not sellable.
 *
 * @author eche0116
 */
class AluminiumScrapTest {

    /**
     * Deposit value must be 50 across three fresh instances.
     */
    @Test
    void getDepositValue_typicalCondition_returns50() {
        assertEquals(50, new AluminiumScrap().getDepositValue());
        assertEquals(50, new AluminiumScrap().getDepositValue());
        assertEquals(50, new AluminiumScrap().getDepositValue());
    }

    /**
     * AluminiumScrap cannot be sold. asCapability must return empty.
     * Three instances checked.
     */
    @Test
    void sellable_negativeCondition_isNotSellable() {
        assertTrue(new AluminiumScrap().asCapability(Sellable.class).isEmpty());
        assertTrue(new AluminiumScrap().asCapability(Sellable.class).isEmpty());
        assertTrue(new AluminiumScrap().asCapability(Sellable.class).isEmpty());
    }

    /**
     * Deposit value must be strictly greater than 0.
     * Three instances checked.
     */
    @Test
    void getDepositValue_boundaryCondition_isPositive() {
        assertTrue(new AluminiumScrap().getDepositValue() > 0);
        assertTrue(new AluminiumScrap().getDepositValue() > 0);
        assertTrue(new AluminiumScrap().getDepositValue() > 0);
    }
}