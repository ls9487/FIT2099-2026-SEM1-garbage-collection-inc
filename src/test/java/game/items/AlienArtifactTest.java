package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AlienArtifact(REQ1).
 * Tests sell price, deposit value, item properties,
 * and that it is both Sellable and Depositable.
 *
 * @author eche0116
 */
class AlienArtifactTest {

    /**
     * Sell price must be 200. Three instances checked.
     */
    @Test
    void getSellPrice_typicalCondition_returns200() {
        assertEquals(200, new AlienArtifact().getSellPrice());
        assertEquals(200, new AlienArtifact().getSellPrice());
        assertEquals(200, new AlienArtifact().getSellPrice());
    }

    /**
     * Deposit value must be 100. Three instances checked.
     */
    @Test
    void getDepositValue_typicalCondition_returns100() {
        assertEquals(100, new AlienArtifact().getDepositValue());
        assertEquals(100, new AlienArtifact().getDepositValue());
        assertEquals(100, new AlienArtifact().getDepositValue());
    }

    /**
     * Sell price and deposit value must be positive and not equal.
     * Three checks.
     */
    @Test
    void prices_boundaryCondition_positiveAndDistinct() {
        AlienArtifact artifact = new AlienArtifact();
        assertTrue(artifact.getSellPrice() > 0);
        assertTrue(artifact.getDepositValue() > 0);
        assertNotEquals(artifact.getSellPrice(), artifact.getDepositValue());
    }

    /**
     * AlienArtifact must be both Sellable and Depositable.
     * Three checks: Sellable present, Depositable present, same instance returned.
     */
    @Test
    void capabilities_typicalCondition_isSellableAndDepositable() {
        AlienArtifact artifact = new AlienArtifact();
        assertFalse(artifact.asCapability(Sellable.class).isEmpty());
        assertFalse(artifact.asCapability(Depositable.class).isEmpty());
        assertEquals(artifact, artifact.asCapability(Depositable.class).get());
    }
}